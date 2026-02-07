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

@HiltViewModel

class AzkarViewModel @Inject constructor(
    private val repository: AzkarRepository
) : ViewModel() {

    var azkarList by mutableStateOf<List<ZikrItemState>>(emptyList())
        private set

    var categoryTitle by mutableStateOf("")
        private set

    fun loadAzkar(categoryId: String) {
        viewModelScope.launch {

            val category = repository.getCategoryById(categoryId)
            categoryTitle = category.title


            val entities = repository.getAzkarByCategory(categoryId)
            azkarList = entities.map {
                ZikrItemState(
                    id = it.id,
                    text = it.text,
                    reward = it.reward ?: "",
                    maxCount = it.repeat
                )
            }
        }
    }

    fun incrementCount(zikrId: Int) {
        azkarList = azkarList.map {
            if (it.id == zikrId && it.currentCount < it.maxCount) {
                it.copy(currentCount = it.currentCount + 1)
            } else it
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