package com.example.sakina.ui.HolyQuran.surah_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.SurahEntity
import com.example.sakina.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahListViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {


    private val medinaSurahIds = setOf(
        2, 3, 4, 5, 8, 9, 13, 22, 24, 33, 47, 48, 49, 57, 58, 59,
        60, 61, 62, 63, 64, 65, 66, 76, 98, 110, 113, 114
    )

    private val _allSurahs = MutableStateFlow<List<Surah>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("الكل")
    val selectedFilter = _selectedFilter.asStateFlow()
    val filteredSurahs = combine(_allSurahs, _searchQuery, _selectedFilter) { surahs, query, filter ->
        surahs.filter { surah ->
            val matchesSearch = surah.nameAr.contains(query) ||
                    surah.nameEn.contains(query, ignoreCase = true)

            val matchesType = when (filter) {
                "الكل" -> true
                "مكية" -> surah.type == "مكية"
                "مدنية" -> surah.type == "مدنية"
                "قصيرة" -> surah.ayahCount <= 10
                else -> true
            }
            matchesSearch && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSurahsFromDb()
    }

    private fun loadSurahsFromDb() {
        viewModelScope.launch {
            val entities = repository.getAllSurahs()
            val uiSurahs = entities.map { it.toUiModel() }
            _allSurahs.value = uiSurahs
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }
    private fun SurahEntity.toUiModel(): Surah {
        return Surah(
            number = this.id,
            nameAr = this.nameAr,
            nameEn = this.nameEn,
            ayahCount = this.ayahCount,
            type = if (medinaSurahIds.contains(this.id)) "مدنية" else "مكية"
        )
    }
}