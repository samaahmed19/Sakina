package com.example.sakina.ui.Home

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sakina.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository,
    application: Application
) : ViewModel() {


    private val prefs = application.getSharedPreferences("sakina_prefs", Context.MODE_PRIVATE)


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



    private val _lastSurahId = mutableStateOf(-1)
    val lastSurahId: State<Int> = _lastSurahId

    private val _lastSurahName = mutableStateOf("")
    val lastSurahName: State<String> = _lastSurahName

    private val _lastAyahIndex = mutableStateOf(-1)
    val lastAyahIndex: State<Int> = _lastAyahIndex
    private val _lastAyahCount = mutableStateOf(0)
    val lastAyahCount: State<Int> = _lastAyahCount
    fun refreshLastRead() {
        _lastSurahId.value = prefs.getInt("last_surah_id", -1)
        _lastSurahName.value = prefs.getString("last_surah_name", "") ?: ""
        _lastAyahIndex.value = prefs.getInt("last_ayah_index", -1)
        _lastAyahCount.value = prefs.getInt("last_ayah_count", 0)
    }
    fun subTitle(): String {
        return if (_lastAyahIndex.value != -1) {
            "واصل من سورة ${_lastSurahName.value}"
        } else {
            "اقرأ وردك اليومي"
        }
    }
}
