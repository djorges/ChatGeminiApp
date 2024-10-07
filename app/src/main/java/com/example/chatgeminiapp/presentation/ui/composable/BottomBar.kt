package com.example.chatgeminiapp.presentation.ui.composable

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatgeminiapp.R
import com.example.chatgeminiapp.presentation.ui.state.UriCustomSaver

@Composable
fun BottomBar(
    onSendClick: (String, List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var userRequest by rememberSaveable { mutableStateOf("") }
    val imageUris by rememberSaveable(stateSaver = UriCustomSaver()) { mutableStateOf(mutableListOf<Uri>()) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { imageUris.add(it) }
        }
    )

    Column {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add image")
            }

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = userRequest,
                onValueChange = { userRequest = it },
                placeholder = { Text(text = "Upload an image and ask a question") }
            )

            IconButton(
                onClick = {
                    if (userRequest.isNotBlank()) {
                        val prompt = context.getString(R.string.prompt_template, userRequest)

                        onSendClick(prompt, imageUris)
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }

        //Show selected images
        AnimatedVisibility(visible = imageUris.size > 0) {
            Card(modifier = Modifier.fillMaxWidth()) {
                LazyRow(modifier = Modifier.padding(8.dp)) {
                    items(imageUris) { imageUri ->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Image",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .requiredSize(50.dp)
                            )
                            TextButton(onClick = { imageUris.remove(imageUri) }) {
                                Text(text = "Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}

