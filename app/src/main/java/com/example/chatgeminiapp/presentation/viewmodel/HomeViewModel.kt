package com.example.chatgeminiapp.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.chatgeminiapp.BuildConfig
import com.example.chatgeminiapp.domain.HomeUiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.type.content

class HomeViewModel(private val appContext: Context): ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private val imageRequestBuilder = ImageRequest.Builder(appContext)
    private val imageLoaderBuilder = ImageLoader.Builder(appContext).build()

    private val generativeMode: GenerativeModel

    init{
        val config = generationConfig { temperature = 0.7f }

        // Inicializar el modelo generativo con los parámetros necesarios
        generativeMode = GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = BuildConfig.apiKey,
            generationConfig = config
        )
    }

    fun handleUserRequest(
        userRequest: String,
        imageUris: List<Uri>
    ) {
        _uiState.value = HomeUiState.Loading

        if (userRequest.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                // Mapear las URIs a bitmaps
                val bitmaps = imageUris.mapNotNull { uri ->
                    loadImage(uri)
                }

                try{
                    // Preparar el contenido a generar, incluyendo las imágenes y el texto
                    val content = content {
                        for (bitmap in bitmaps){
                            image(bitmap)
                        }
                        text(userRequest)
                    }

                    // Generar un flujo de contenido utilizando el modelo generativo
                    generativeMode.generateContentStream(content).collect{
                        _uiState.value = HomeUiState.Success(it.text ?: "")
                    }
                }catch (e: Exception){
                    _uiState.value = HomeUiState.Error(e.message ?: "Unknown Error")
                }
            }
        }
    }

    /**
     * Carga una imagen desde la URI proporcionada y la convierte en un `Bitmap`.
     * Devuelve `null` si la carga falla.
     *
     * @param uri La URI de la imagen a cargar.
     * @return Un `Bitmap` si la carga es exitosa, o `null` en caso contrario.
     */
    private suspend fun loadImage(uri: Uri): Bitmap? {
        val imgRequest = imageRequestBuilder
            .data(uri)
            .size(size = THUMBNAIL_SIZE)
            .build()
        val imageResult = imageLoaderBuilder.execute(imgRequest)

        return if (imageResult is SuccessResult) {
            imageResult.drawable.toBitmap()
        } else {
            null
        }
    }

    companion object {
        const val THUMBNAIL_SIZE = 76
        const val MODEL_NAME = "gemini-1.5-flash-latest"
    }
}