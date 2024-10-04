package com.example.chatgeminiapp

sealed interface HomeUiState {
    data object Initial: HomeUiState
    data object Loading:HomeUiState
    data class Success(val outputText:String):HomeUiState
    data class Error(val error:String): HomeUiState
}