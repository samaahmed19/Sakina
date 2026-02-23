package com.sama.sakina.ui.Azkar.azkar_details
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import com.sama.sakina.data.repository.AzkarRepository
import com.sama.sakina.data.source.mapper.JsonMapper
import android.util.Log
import com.sama.sakina.data.local.database.entity.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.text.repeat


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
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            val todayStart = calendar.timeInMillis

            repository.resetOldAzkar(todayStart)

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
                    currentCount = it.currentCount
                )
            }
        }
    }

    fun incrementCount(zikrId: Int) {
        val item = azkarList.find { it.id == zikrId }
        if (item != null && item.currentCount < item.maxCount) {
            val newCount = item.currentCount + 1
            val currentTime = System.currentTimeMillis()

            // تحديث الـ UI
            azkarList = azkarList.map {
                if (it.id == zikrId) it.copy(currentCount = newCount) else it
            }

            // تحديث الـ DB مع التاريخ الجديد
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateZikrWithTimestamp(zikrId, newCount, currentTime)
            }
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