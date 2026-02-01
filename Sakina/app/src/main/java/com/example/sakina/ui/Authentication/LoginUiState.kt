package com.example.sakina.ui.authentication

data class LoginUiState(
    val name: String = "",
    val email: String = "",
    val location: String = "",
    val isLoading: Boolean = false,
    val isLoadingLocation: Boolean = false
)
