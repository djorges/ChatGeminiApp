package com.example.chatgeminiapp.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.chatgeminiapp.domain.HomeUiState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState.Initial
){
    Column(
        modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        when(uiState){
            HomeUiState.Initial -> {}
            HomeUiState.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = Color.Blue,
                        strokeWidth = 6.dp,
                        modifier = Modifier.size(100.dp),
                        trackColor = Color.LightGray,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
            is HomeUiState.Success -> {
                Card(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(text = uiState.outputText)
                }
            }
            is HomeUiState.Error -> {
                //TODO:
                Card(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(text = uiState.error)
                }
            }
        }
    }
}