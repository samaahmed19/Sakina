package com.example.sakina.ui.Prayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.repository.PrayerSettingsRepository
import com.example.sakina.data.repository.UserRepository
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.model.PrayerCalculationMethod
import com.example.sakina.domain.model.PrayerMadhab
import com.example.sakina.domain.usecase.GetDayPrayersUseCase
import com.example.sakina.domain.usecase.SetPrayerCompletionUseCase
import com.example.sakina.utils.LocationHelper
import com.example.sakina.utils.PrayerTimesCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val getDayPrayers: GetDayPrayersUseCase,
    private val setPrayerCompletion: SetPrayerCompletionUseCase,
    private val userRepository: UserRepository,
    private val locationHelper: LocationHelper,
    private val prayerSettingsRepository: PrayerSettingsRepository,
    private val prayerTimesCalculator: PrayerTimesCalculator
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

    private fun parseLatLng(value: String?): Pair<Double, Double>? {
        if (value.isNullOrBlank()) return null
        val parts = value.split(",")
        if (parts.size < 2) return null
        val lat = parts[0].trim().toDoubleOrNull() ?: return null
        val lon = parts[1].trim().toDoubleOrNull() ?: return null
        return lat to lon
    }

    private fun prayerTimeOrder(): List<PrayerKey> = listOf(
        PrayerKey.PRAYER_FAJR,
        PrayerKey.PRAYER_DHUHR,
        PrayerKey.PRAYER_ASR,
        PrayerKey.PRAYER_MAGHRIB,
        PrayerKey.PRAYER_ISHA
    )

    private fun findNextPrayer(nowMillis: Long, todayTimes: Map<PrayerKey, Long>, tomorrowTimes: Map<PrayerKey, Long>): Pair<PrayerKey, Long>? {
        val ordered = prayerTimeOrder()
        val nextToday = ordered
            .mapNotNull { key -> todayTimes[key]?.let { key to it } }
            .firstOrNull { (_, time) -> time > nowMillis }

        if (nextToday != null) return nextToday

        val nextTomorrow =
            ordered.firstNotNullOfOrNull { key -> tomorrowTimes[key]?.let { key to it } }

        return nextTomorrow
    }

    /* ---------------------------------------------------------
     * INIT
     * --------------------------------------------------------- */

    init {
        load()
        scheduleDailyRefresh()
    }

    private fun scheduleDailyRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(millisUntilNextMidnight() + 1_000L)
                load()
            }
        }
    }

    private fun millisUntilNextMidnight(nowMillis: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return (cal.timeInMillis - nowMillis).coerceAtLeast(0L)
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

            val summaryDeferred = async {
                getDayPrayers(date)
            }

            val userDeferred = async {
                runCatching { userRepository.getUserOnce() }.getOrNull()
            }

            val currentLocationDeferred = async {
                runCatching { locationHelper.getCurrentLocation() }.getOrNull()
            }

            val settingsDeferred = async {
                runCatching { prayerSettingsRepository.getOnce() }.getOrDefault(_uiState.value.settings)
            }

            runCatching {
                val summary = summaryDeferred.await()
                val user = userDeferred.await()
                val currentLocation = currentLocationDeferred.await()
                val settings = settingsDeferred.await()

                if (!currentLocation.isNullOrBlank() && currentLocation != user?.location) {
                    runCatching { userRepository.updateLocation(currentLocation) }
                }

                val locationToUse = currentLocation ?: user?.location
                val latLng = parseLatLng(locationToUse)

                val fardTimesMap: Map<PrayerKey, Long>
                val next: Pair<PrayerKey, Long>?

                if (latLng != null) {
                    val (lat, lon) = latLng
                    val calendar = Calendar.getInstance().apply {
                        time = dateFormat.parse(date) ?: Date()
                    }
                    val todayFard = prayerTimesCalculator.calculateFardTimes(lat, lon, calendar, settings).asMap()
                    val tomorrowCalendar = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
                    val tomorrowFard = prayerTimesCalculator.calculateFardTimes(lat, lon, tomorrowCalendar, settings).asMap()

                    fardTimesMap = todayFard
                    next = findNextPrayer(System.currentTimeMillis(), todayFard, tomorrowFard)
                } else {
                    fardTimesMap = emptyMap()
                    next = null
                }

                PrayerUiState(
                    isLoading = false,
                    summary = summary,
                    settings = settings,
                    fardPrayerTimes = fardTimesMap,
                    nextFardPrayerKey = next?.first,
                    nextFardPrayerTimeMillis = next?.second
                )
            }.onFailure { throwable ->
                _uiState.value = PrayerUiState(
                    isLoading = false,
                    error = throwable.message ?: "Unknown error"
                )
            }.onSuccess { newState ->
                _uiState.value = newState
            }
        }
    }

    fun setCalculationMethod(method: PrayerCalculationMethod) {
        viewModelScope.launch {
            runCatching { prayerSettingsRepository.setMethod(method) }
            load()
        }
    }

    fun setMadhab(madhab: PrayerMadhab) {
        viewModelScope.launch {
            runCatching { prayerSettingsRepository.setMadhab(madhab) }
            load()
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
