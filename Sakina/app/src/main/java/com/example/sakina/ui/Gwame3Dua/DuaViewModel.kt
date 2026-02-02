package com.example.sakina.ui.Dua.dua_list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.DuaCategoryEntity
import com.example.sakina.data.local.database.entity.DuaEntity
import com.example.sakina.data.repository.DuaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DuaViewModel @Inject constructor(
    private val repository: DuaRepository,
) : ViewModel() {

    var selectedFilterId = mutableStateOf<String?>(null)

    fun onFilterChanged(newId: String?) {
        selectedFilterId.value = newId
    }

    private val _categories = MutableStateFlow<List<DuaCategoryEntity>>(emptyList())
    val categories: StateFlow<List<DuaCategoryEntity>> = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val data = repository.getAllCategories()
            _categories.value = data
            }
        }
    }
