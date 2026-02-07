package com.example.sakina.ui.Azkar.azkar_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.sakina.data.repository.AzkarRepository
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val repository: AzkarRepository
) : ViewModel() {
    private val _allCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    private val _filteredCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _filteredCategories

    var selectedFilter = mutableStateOf("الكل")
    var selectedFilterId = mutableStateOf<String?>(null)

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {

            val testData = listOf(
                CategoryEntity(id = "1", title = "أذكار الصباح", icon = "sun"),
                CategoryEntity(id = "2", title = "أذكار المساء", icon = "moon")
            )
            _allCategories.value = testData
            _filteredCategories.value = testData
        }
    }


    fun onFilterTextChanged(filter: String) {
        selectedFilter.value = filter
        if (filter == "الكل") {
            _filteredCategories.value = _allCategories.value
        } else {
            _filteredCategories.value = _allCategories.value.filter {
                it.title.contains(filter.replace("حسب ", ""))
            }
        }
    }


    fun onFilterIdChanged(newId: String?) {
        selectedFilterId.value = newId
    }
}