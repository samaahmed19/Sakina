package com.example.sakina.ui.Prayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.usecase.GetDayPrayersUseCase
import com.example.sakina.domain.usecase.SetPrayerCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val getDayPrayers: GetDayPrayersUseCase,
    private val setPrayerCompletion: SetPrayerCompletionUseCase
) : ViewModel() {

    /* ---------------------------------------------------------
     * UI STATE
     * --------------------------------------------------------- */

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    /* ---------------------------------------------------------
     * DATE HANDLING
     * --------------------------------------------------------- */

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun today(): String = dateFormat.format(Date())

    /* ---------------------------------------------------------
     * INIT
     * --------------------------------------------------------- */

    init {
        load()
    }

    /* ---------------------------------------------------------
     * LOAD DAY SUMMARY
     * --------------------------------------------------------- */

    fun load(date: String = today()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            runCatching {
                getDayPrayers(date)
            }.onSuccess { summary ->
                _uiState.value = PrayerUiState(
                    isLoading = false,
                    summary = summary
                )
            }.onFailure { throwable ->
                _uiState.value = PrayerUiState(
                    isLoading = false,
                    error = throwable.message ?: "Unknown error"
                )
            }
        }
    }

    /* ---------------------------------------------------------
     * TOGGLE PRAYER COMPLETION
     * --------------------------------------------------------- */

    fun setPrayerChecked(key: PrayerKey, newValue: Boolean) {
        val date = _uiState.value.summary?.date ?: today()
        viewModelScope.launch {
            runCatching { setPrayerCompletion(date, key, newValue) }
                .onSuccess { summary ->
                    _uiState.value = _uiState.value.copy(summary = summary, error = null)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "Update failed")
                }
        }
    }
}