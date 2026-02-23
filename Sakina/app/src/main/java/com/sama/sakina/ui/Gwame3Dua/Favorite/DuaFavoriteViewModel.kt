package com.sama.sakina.ui.Gwame3Dua.Favorite

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
class DuaFavoriteViewModel @Inject constructor(
    private val repository: DuaRepository
) : ViewModel() {
    private val _favorites = MutableStateFlow<List<DuaEntity>>(emptyList())
    val favorites: StateFlow<List<DuaEntity>> = _favorites

    init {
        viewModelScope.launch {
            repository.getFavoriteDuas().collect { data ->
                _favorites.value = data
            }
        }
    }

    fun toggleFavorite(duaId: Int, currentFav: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(duaId, !currentFav)
        }
    }
}