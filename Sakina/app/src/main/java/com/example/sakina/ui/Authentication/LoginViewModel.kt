package com.example.sakina.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.repository.UserRepository
import com.example.sakina.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value)
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update {
            it.copy(location = value)
        }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLocation = true) }
            val location = locationHelper.getCurrentLocation()
            _uiState.update {
                it.copy(
                    location = location ?: it.location,
                    isLoadingLocation = false
                )
            }
        }
    }

    fun onStartClick() {
        val currentName = _uiState.value.name

        if (currentName.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            userRepository.saveUser(
                name = currentName.trim(),
                email = null,
                location = _uiState.value.location.ifBlank { null }
            )

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
