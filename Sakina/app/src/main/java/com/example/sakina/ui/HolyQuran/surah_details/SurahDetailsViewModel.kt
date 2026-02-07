package com.example.sakina.ui.HolyQuran.surah_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SurahDetailsViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _fontSize = mutableStateOf(22f)
    val fontSize: State<Float> = _fontSize

    private val _isBookmarked = mutableStateOf(false)
    val isBookmarked: State<Boolean> = _isBookmarked

    private val _ayatList = MutableStateFlow<List<AyahEntity>>(emptyList())
    val ayatList: StateFlow<List<AyahEntity>> = _ayatList.asStateFlow()

    private val _tafsirMap = MutableStateFlow<Map<Int, String>>(emptyMap())
    val tafsirMap: StateFlow<Map<Int, String>> = _tafsirMap.asStateFlow()

    private val _expandedAyahIndex = mutableStateOf<Int?>(null)
    val expandedAyahIndex: State<Int?> = _expandedAyahIndex

    private val _isFontSettingsVisible = mutableStateOf(false)
    val isFontSettingsVisible: State<Boolean> = _isFontSettingsVisible

    fun updateFontSize(newSize: Float) {
        _fontSize.value = newSize
    }

    fun toggleBookmark() {
        _isBookmarked.value = !_isBookmarked.value
    }
    fun loadSurahData(surahId: Int) {
        viewModelScope.launch {
            val ayahs = repository.getAyahsBySurah(surahId)
            _ayatList.value = ayahs
            val tafsirList = repository.getTafsirBySurah(surahId)
            _tafsirMap.value = tafsirList.associate { it.ayahNumber to it.text }
        }
    }

    fun toggleAyahExpansion(index: Int) {
        _expandedAyahIndex.value = if (_expandedAyahIndex.value == index) null else index
    }

    fun toggleFontSettings() {
        _isFontSettingsVisible.value = !_isFontSettingsVisible.value
    }
}