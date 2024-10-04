package com.example.chatgeminiapp

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.type.content

class HomeViewModel(private val appContext: Context): ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    private val uiState = _uiState.asStateFlow()

    private val generativeMode: GenerativeModel

    init{
        val config = generationConfig {
            temperature = 0.7f
        }

        // Inicializar el modelo generativo con los parámetros necesarios
        generativeMode = GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = BuildConfig.apiKey,
            generationConfig = config
        )
    }

    /**
     * Procesa la entrada del usuario y las imágenes seleccionadas.
     *
     * @param userInput La pregunta o entrada del usuario.
     * @param selectImages Lista de imágenes seleccionadas en formato Bitmap.
     */
    fun questioning(userInput: String, selectImages:List<Bitmap>){
        _uiState.value = HomeUiState.Loading

        val prompt = appContext.getString(R.string.prompt_template, userInput)

        viewModelScope.launch(Dispatchers.IO) {
            try{
                // Preparar el contenido a generar, incluyendo las imágenes y el texto
                val content = content {
                    for (bitmap in selectImages){
                        image(bitmap)
                    }
                    text(prompt)
                }

                // Generar un flujo de contenido utilizando el modelo generativo
                generativeMode.generateContentStream(content).collect{
                    _uiState.value = HomeUiState.Success(it.text?: "")
                }
            }catch (e: Exception){
                _uiState.value = HomeUiState.Error(e.message?: "Unknown Error")
            }
        }
    }
}