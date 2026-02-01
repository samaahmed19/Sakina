




package com.example.sakina.ui.Tasbeeh

import com.example.sakina.BreathPhase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TasbeehViewModel : ViewModel() {

    var count by mutableIntStateOf(0)
        private set


    var breathPhase by mutableStateOf(BreathPhase.INHALE)

    var hasStartedByClick by mutableStateOf(false)

    fun incrementCount() {
        count++
        if (!hasStartedByClick) {
            hasStartedByClick = true
        }
    }

    fun resetCount() {
        count = 0
    }
}