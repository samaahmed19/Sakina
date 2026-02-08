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
            try {
                val dbCategories = repository.getAllCategories()

                if (dbCategories.isEmpty()) {

                    val initialData = listOf(
                        CategoryEntity(id = "morning", title = "أذكار الصباح", icon = "sun"),
                        CategoryEntity(id = "evening", title = "أذكار المساء", icon = "moon"),
                        CategoryEntity(id = "sleep", title = "أذكار النوم", icon = "bed"),
                        CategoryEntity(id = "after prayer", title = "أذكار بعد الصلاة", icon = "carpet"),
                        CategoryEntity(id = "wake up", title = "أذكار الاستيقاظ من النوم", icon = "wakeup"),
                        CategoryEntity(id = "prayer", title = "أذكار الصلاة", icon = "carpet2"),
                      CategoryEntity(id = "mosque", title = "أذكار المسجد", icon = "mosque"),
                    CategoryEntity(id = "adhan", title = "أذكار عند سماع الأذان", icon = "azan"),
                    CategoryEntity(id = "wudu", title = "أذكار الوضوء", icon = "water"),
                    CategoryEntity(id = "home", title = "أذكار دخول وخروج المنزل", icon = "home"),
                    CategoryEntity(id = "food", title = "أذكار الطعام", icon = "food"),
                    CategoryEntity(id = "misc", title = "أذكار متفرقة", icon = "misc")
                    )



                    repository.insertCategories(initialData)
                    _allCategories.value = initialData
                    _filteredCategories.value = initialData
                } else {
                    _allCategories.value = dbCategories
                    _filteredCategories.value = dbCategories
                }
            } catch (e: Exception) {
                android.util.Log.e("AzkarError", "Error: ${e.message}")
            }
        }
    }


    fun onFilterTextChanged(filter: String) {
        selectedFilter.value = filter
        if (filter == "الكل") {
            _filteredCategories.value = _allCategories.value
        } else {

            _filteredCategories.value = _allCategories.value.filter { category ->
                category.title.contains(filter, ignoreCase = true)
            }
        }
    }


    fun onFilterIdChanged(newId: String?) {
        selectedFilterId.value = newId
    }
}