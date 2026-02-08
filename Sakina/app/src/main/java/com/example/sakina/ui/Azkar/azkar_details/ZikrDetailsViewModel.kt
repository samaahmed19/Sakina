package com.example.sakina.ui.Azkar.azkar_details
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.sakina.data.repository.AzkarRepository
import com.example.sakina.data.source.mapper.JsonMapper
import android.util.Log
import com.example.sakina.data.local.database.entity.CategoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



@HiltViewModel
class AzkarViewModel @Inject constructor(
    private val repository: AzkarRepository
) : ViewModel() {
    private val _allCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())

    var azkarList by mutableStateOf<List<ZikrItemState>>(emptyList())
        private set
    var categoryTitle by mutableStateOf("")
        private set

    init {
        loadDataFromDbOrJson()
    }

    private fun loadDataFromDbOrJson() {
        viewModelScope.launch {
            val dbCategories = repository.getAllCategories()
            if (dbCategories.isEmpty()) {
                try {

                    val jsonString = repository.loadJsonFromAssets("azkar.json")
                    val (categories, azkar) = JsonMapper.mapCategories(jsonString)

                    // حفظ البيانات في قاعدة البيانات
                    repository.insertCategories(categories)
                    repository.insertAzkar(azkar) // حفظ الأذكار ضروري جداً هنا

                    _allCategories.value = categories
                } catch (e: Exception) {
                    Log.e("AzkarError", "Failed to seed data: ${e.message}")
                }
            } else {
                _allCategories.value = dbCategories
            }
        }
    }

    // الدالة المسؤولة عن ملء بيانات شاشة الديتيلز
    fun loadAzkar(categoryId: String) {
        viewModelScope.launch {
            // جلب العنوان
            val category = repository.getCategoryById(categoryId)
            categoryTitle = category?.title ?: "الأذكار"

            // جلب الأذكار من جدول الـ azkar بناءً على الـ categoryId
            val entities = repository.getAzkarByCategory(categoryId)

            // تحويل الـ Entities لـ UI State (ZikrItemState)
            azkarList = entities.map {
                ZikrItemState(
                    id = it.id,
                    text = it.text,
                    reward = it.reward ?: "",
                    maxCount = it.repeat,
                    currentCount = 0
                )
            }
        }
    }

    fun incrementCount(zikrId: Int) {
        azkarList = azkarList.map {
            if (it.id == zikrId && it.currentCount < it.maxCount) it.copy(currentCount = it.currentCount + 1)
            else it
        }
    }
}




data class ZikrItemState(
    val id: Int,
    val text: String,
    val reward: String,
    val currentCount: Int = 0,
    val maxCount: Int
)