package com.example.sakina.ui.Home

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sakina.data.repository.TasbeehRepository
import com.example.sakina.data.repository.UserRepository
import com.example.sakina.domain.usecase.GetDayPrayersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository,
    application: Application,
    private val getDayPrayers: GetDayPrayersUseCase,
    private val tasbeehRepository: TasbeehRepository
) : ViewModel() {

    val userFlow = userRepository.getUser()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun today() = dateFormat.format(Date())

    private val _prayerCompleted = MutableStateFlow(0)
    private val _prayerTotal = MutableStateFlow(5)
    val prayerCompleted: StateFlow<Int> = _prayerCompleted.asStateFlow()
    val prayerTotal: StateFlow<Int> = _prayerTotal.asStateFlow()

    private val _tasbeehCount = MutableStateFlow(0)
    val tasbeehCount: StateFlow<Int> = _tasbeehCount.asStateFlow()

    init {
        loadPrayerAndTasbeeh()
    }

    fun loadPrayerAndTasbeeh() {
        viewModelScope.launch {
            runCatching {
                getDayPrayers(today())
            }.onSuccess { summary ->
                _prayerCompleted.value = summary.completedFardCount
                _prayerTotal.value = summary.totalFardCount
            }
            runCatching {
                tasbeehRepository.getTotalCount()
            }.onSuccess { total ->
                _tasbeehCount.value = total
            }
        }
    }
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
