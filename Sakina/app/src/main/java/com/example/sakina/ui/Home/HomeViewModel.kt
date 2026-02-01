package com.example.sakina.ui.Home

import androidx.lifecycle.ViewModel
import com.example.sakina.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    private fun greetingWithTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (hour in 5..17) "صباح الخير" else "مساء الخير"
    }

    val greetingFlow = userRepository.getUser().map { user ->
        val name = user?.name?.takeIf { it.isNotBlank() } ?: ""
        if (name.isNotEmpty()) {
            "${greetingWithTime()} $name"
        } else {
            greetingWithTime()
        }
    }
}
