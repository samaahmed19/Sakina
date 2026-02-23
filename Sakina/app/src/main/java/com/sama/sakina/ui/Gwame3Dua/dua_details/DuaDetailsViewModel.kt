package com.sama.sakina.ui.Gwame3Dua.dua_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sama.sakina.data.local.database.entity.DuaEntity
import com.sama.sakina.data.repository.DuaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaDetailsViewModel @Inject constructor(
    private val repository: DuaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val categoryId: Int = checkNotNull(savedStateHandle["categoryId"])

    val scrollDuaId: Int = savedStateHandle["scrollDuaId"] ?: -1
    private val _categoryName = MutableStateFlow("")

    val categoryName: StateFlow<String> = _categoryName
    private val _duas = MutableStateFlow<List<DuaEntity>>(emptyList())
    val duas: StateFlow<List<DuaEntity>> = _duas

    init {
        viewModelScope.launch {
            repository.getCategoryById(categoryId).collect { category ->
                _categoryName.value = category.title
            }
        }
        loadDuas()
    }

    private fun loadDuas() {
        viewModelScope.launch {
            repository.getDuasByCategory(categoryId.toString()).collect { data ->
                _duas.value = data
            }
        }
    }

    fun toggleFavorite(duaId: Int, currentFav: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(duaId, !currentFav)
        }
    }

    private var hasScrolled = false

    fun markAsScrolled() {
        hasScrolled = true
    }

    fun shouldScroll(): Boolean {
        return !hasScrolled && scrollDuaId != -1
    }
}