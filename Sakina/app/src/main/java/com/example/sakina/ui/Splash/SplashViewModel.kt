package com.example.sakina.ui.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {

            delay(2000)

            val user = userRepository.getUserOnce()

            if (user != null) {

                _authState.value = AuthState.Authenticated
            } else {

                _authState.value = AuthState.Unauthenticated
            }
        }
    }
}