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
            it.copy(
                name = value,
                nameError = null
            )
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update {
            it.copy(location = value)
        }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val location = locationHelper.getCurrentLocation()

            if (location != null) {

                _uiState.update { it.copy(location = location, isLoading = false, nameError = null) }
            } else {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nameError = "تعذر تحديد الموقع تلقائياً. تأكد من تشغيل الـ GPS أو حاول مرة أخرى."
                    )
                }
            }
        }
    }

    fun onStartClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(nameError = "ادخل اسمك أولًا") }
            return
        }

        if (currentState.location.isBlank()) {
            _uiState.update { it.copy(nameError = "يرجى تحديد موقعك أولاً") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.saveUser(
                name = currentState.name.trim(),
                email = null,
                location = currentState.location
            )
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
