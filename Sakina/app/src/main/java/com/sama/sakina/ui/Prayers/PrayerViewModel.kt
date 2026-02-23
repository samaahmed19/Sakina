package com.sama.sakina.ui.Prayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sama.sakina.data.repository.PrayerSettingsRepository
import com.sama.sakina.data.repository.UserRepository
import com.sama.sakina.domain.model.PrayerKey
import com.sama.sakina.domain.model.PrayerCalculationMethod
import com.sama.sakina.domain.model.PrayerMadhab
import com.sama.sakina.domain.model.PrayerType
import com.sama.sakina.domain.usecase.GetDayPrayersUseCase
import com.sama.sakina.domain.usecase.GetMonthlyCompletionUseCase
import com.sama.sakina.domain.usecase.SetPrayerCompletionUseCase
import com.sama.sakina.utils.LocationHelper
import com.sama.sakina.utils.PrayerTimesCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val getDayPrayers: GetDayPrayersUseCase,
    private val getMonthlyCompletion: GetMonthlyCompletionUseCase,
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
     * DATE HANDLING & FORMATTERS
     * --------------------------------------------------------- */

    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedMonth = MutableStateFlow(monthFormat.format(Date()))

    /* ---------------------------------------------------------
     * INIT
     * --------------------------------------------------------- */
    init {
        observeMonthlyData()
        load(_selectedDate.value)
        scheduleDailyRefresh()
    }

    private fun observeMonthlyData() {
        _selectedMonth
            .flatMapLatest { month ->
                getMonthlyCompletion(month)
            }
            .onEach { data ->
                _uiState.update { it.copy(monthlyCompletion = data) }
            }
            .launchIn(viewModelScope)
    }

    fun onDateSelected(date: String) {
        if (_selectedDate.value == date) return
        _selectedDate.value = date

        val newMonth = date.substring(0, 7)
        if (newMonth != _selectedMonth.value) {
            _selectedMonth.value = newMonth
        }

        load(date)
    }

    private fun today(): String = dateFormat.format(Date())

    private fun parseLatLng(v: String?): Pair<Double, Double>? {
        return try {
            val s = v?.split(",") ?: return null
            s[0].trim().toDouble() to s[1].trim().toDouble()
        } catch (e: Exception) {
            null
        }
    }

    private fun prayerTimeOrder(): List<PrayerKey> = listOf(
        PrayerKey.PRAYER_FAJR,
        PrayerKey.PRAYER_DHUHR,
        PrayerKey.PRAYER_ASR,
        PrayerKey.PRAYER_MAGHRIB,
        PrayerKey.PRAYER_ISHA
    )

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
     * LOAD DAY SUMMARY & NEXT PRAYER
     * --------------------------------------------------------- */

    fun load(date: String = _selectedDate.value) {
        viewModelScope.launch {
            if (_uiState.value.summary == null) {
                _uiState.update {
                    it.copy(isLoading = true, error = null)
                }
            }
            try {
                val summary = getDayPrayers(date)
                val settings = prayerSettingsRepository.getOnce()
                val user = userRepository.getUserOnce()

                val latLng = parseLatLng(user?.location)
                var fardTimes = emptyMap<PrayerKey, Long>()

                var nextKey: PrayerKey? = null
                var nextTime: Long? = null

                if (latLng != null) {
                    val cal = Calendar.getInstance().apply {
                        val parsedDate = dateFormat.parse(date)
                        time = parsedDate ?: Date()
                    }
                    fardTimes = prayerTimesCalculator.calculateFardTimes(
                        latLng.first,
                        latLng.second,
                        cal,
                        settings
                    ).asMap()

                    // --- حساب الصلاة القادمة لإرسالها للـ Home Screen ---
                    val now = System.currentTimeMillis()
                    val orderedKeys = prayerTimeOrder()

                    for (key in orderedKeys) {
                        val timeMillis = fardTimes[key]
                        if (timeMillis != null && timeMillis > now) {
                            nextKey = key
                            nextTime = timeMillis
                            break
                        }
                    }
                }

                _uiState.update { it.copy(
                    isLoading = false,
                    summary = summary,
                    settings = settings,
                    fardPrayerTimes = fardTimes,
                    nextFardPrayerKey = nextKey,
                    nextFardPrayerTimeMillis = nextTime
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.localizedMessage
                ) }
            }
        }
    }

    fun setCalculationMethod(method: PrayerCalculationMethod) {
        viewModelScope.launch {
            prayerSettingsRepository.setMethod(method)
            load(_selectedDate.value)
        }
    }

    fun setMadhab(madhab: PrayerMadhab) {
        viewModelScope.launch {
            prayerSettingsRepository.setMadhab(madhab)
            load(_selectedDate.value)
        }
    }

    /* ---------------------------------------------------------
     * TOGGLE PRAYER COMPLETION
     * --------------------------------------------------------- */

    fun setPrayerChecked(key: PrayerKey, newValue: Boolean) {
        viewModelScope.launch {
            val date = _selectedDate.value

            val result = runCatching { setPrayerCompletion(date, key, newValue) }

            if (result.isSuccess) {
                val updatedSummary = getDayPrayers(date)

                _uiState.update { currentState ->
                    val updatedMonthly = currentState.monthlyCompletion.toMutableMap()

                    val fardCount = updatedSummary.items.count { it.type == PrayerType.FARD && it.isCompleted }
                    val nafilaCount = updatedSummary.items.count { it.type == PrayerType.NAFILA && it.isCompleted }

                    updatedMonthly[date] = (nafilaCount * 10) + fardCount

                    currentState.copy(
                        summary = updatedSummary,
                        monthlyCompletion = updatedMonthly
                    )
                }
            }
        }
    }
}