package com.sama.sakina.ui.Gwame3Dua.dua_list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sama.sakina.data.local.database.entity.DuaCategoryEntity
import com.sama.sakina.data.local.database.entity.DuaEntity
import com.sama.sakina.data.repository.DuaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val repository: DuaRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<DuaEntity>>(emptyList())

    val searchResults: StateFlow<List<DuaEntity>> = _searchResults
    private val _allCategories = MutableStateFlow<List<DuaCategoryEntity>>(emptyList())
    private val _filteredCategories = MutableStateFlow<List<DuaCategoryEntity>>(emptyList())
    val categories: StateFlow<List<DuaCategoryEntity>> = _filteredCategories

    var selectedFilter = mutableStateOf("الكل")

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val data = repository.getAllCategories()
            _allCategories.value = data
            _filteredCategories.value = data
        }
    }

    fun onFilterChanged(filter: String) {
        selectedFilter.value = filter
        if (filter == "الكل") {
            _filteredCategories.value = _allCategories.value
        } else {
            _filteredCategories.value = _allCategories.value.filter {
                it.title.contains(filter)
            }
        }
    }

}
