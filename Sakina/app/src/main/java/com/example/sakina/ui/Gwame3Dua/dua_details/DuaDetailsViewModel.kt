package com.example.sakina.ui.Gwame3Dua.dua_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.DuaEntity
import com.example.sakina.data.repository.DuaRepository
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
    val categoryTitle: String = savedStateHandle.get<String>("categoryTitle")?.replace("_", " ") ?: ""
    private val _duas = MutableStateFlow<List<DuaEntity>>(emptyList())
    val duas: StateFlow<List<DuaEntity>> = _duas

    init {
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
}