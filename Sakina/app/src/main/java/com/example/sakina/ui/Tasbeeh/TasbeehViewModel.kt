package com.example.sakina.ui.Tasbeeh

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.BreathPhase
import com.example.sakina.data.local.database.entity.TasbeehEntity
import com.example.sakina.data.repository.TasbeehRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasbeehViewModel @Inject constructor(
    private val repository: TasbeehRepository
) : ViewModel() {


    var tasbeehList by mutableStateOf<List<TasbeehEntity>>(emptyList())
        private set


    var selectedTasbeeh: TasbeehEntity? by mutableStateOf(null)
        private set


    var count by mutableIntStateOf(0)
        private set

    private var _breathPhase by mutableStateOf(BreathPhase.INHALE)
    val breathPhase: BreathPhase get() = _breathPhase

    var hasStartedByClick by mutableStateOf(false)

    init {
        loadTasbeehData()
    }

    private fun loadTasbeehData() {
        viewModelScope.launch {
            val list = repository.getTasbeeh()
            if (list.isNotEmpty()) {
                tasbeehList = list

                if (selectedTasbeeh == null) {
                    updateSelectedTasbeeh(list.first())
                }
            }
        }
    }

    fun incrementCount() {
        val current = selectedTasbeeh
        if (current == null) {
            android.util.Log.e("Tasbeeh", "Selected Tasbeeh is NULL!")
            return
        }

        count++
        android.util.Log.d("Tasbeeh", "Count increased to: $count")

        viewModelScope.launch {
            repository.increment(current.id)
        }
    }

    fun updateSelectedTasbeeh(tasbeeh: TasbeehEntity) {
        selectedTasbeeh = tasbeeh
        count = tasbeeh.currentCount
    }

    fun resetCount() {
        val current = selectedTasbeeh ?: return
        count = 0
        viewModelScope.launch {

            repository.insertDefaults(listOf(current.copy(currentCount = 0)))
            updateSelectedTasbeeh(current.copy(currentCount = 0))
        }
    }

    fun updateBreathPhase(phase: BreathPhase) {
        _breathPhase = phase
    }
}
