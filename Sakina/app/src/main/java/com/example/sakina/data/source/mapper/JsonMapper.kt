package com.example.sakina.data.source.mapper

import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.DuaCategoryEntity
import com.example.sakina.data.local.database.entity.DuaEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.entity.TasbeehEntity
import org.json.JSONObject

object JsonMapper {

    // ===== Azkar =====
    fun mapCategories(json: String): Pair<List<CategoryEntity>, List<ZikrEntity>> {
        val categories = mutableListOf<CategoryEntity>()
        val azkar = mutableListOf<ZikrEntity>()

        val root = JSONObject(json)
        val categoriesArray = root.getJSONArray("categories")

        for (i in 0 until categoriesArray.length()) {
            val categoryObj = categoriesArray.getJSONObject(i)
            val categoryId = categoryObj.getString("id")

            categories.add(
                CategoryEntity(
                    id = categoryId,
                    title = categoryObj.getString("title"),
                    icon = categoryObj.getString("icon")
                )
            )

            val azkarArray = categoryObj.getJSONArray("azkar")
            for (j in 0 until azkarArray.length()) {
                val zikrObj = azkarArray.getJSONObject(j)

                azkar.add(
                    ZikrEntity(
                        categoryId = categoryId,
                        text = zikrObj.getString("text"),
                        repeat = zikrObj.optInt("repeat", 1),
                        reward = zikrObj.optString("reward", null)
                    )
                )
            }
        }
        return Pair(categories, azkar)
    }

    // ===== Duas =====
    fun mapDuas(json: String): Pair<List<DuaCategoryEntity>, List<DuaEntity>> {
        val categories = mutableListOf<DuaCategoryEntity>()
        val duas = mutableListOf<DuaEntity>()
        val root = JSONObject(json)
        val categoryArray = root.getJSONArray("categories")

        for (i in 0 until categoryArray.length()) {
            val catObj = categoryArray.getJSONObject(i)
            val catId = catObj.getString("id")

            val duasArray = catObj.getJSONArray("duas")
            val currentCategoryDuaCount = duasArray.length()

            categories.add(
                DuaCategoryEntity(
                    id = catId,
                    title = catObj.getString("name"),
                    icon = "splash",
                    count = currentCategoryDuaCount
                )
            )

            for (j in 0 until duasArray.length()) {
                val duaObj = duasArray.getJSONObject(j)
                duas.add(
                    DuaEntity(
                        categoryId = catId,
                        text = duaObj.getString("text")
                    )
                )
            }
        }
        return Pair(categories, duas)
    }

    // ===== Tasbeeh  =====
    fun mapTasbeeh(json: String): List<TasbeehEntity> {
        val result = mutableListOf<TasbeehEntity>()
        val root = JSONObject(json)
        val tasbeehArray = root.getJSONArray("tasbeeh")

        for (i in 0 until tasbeehArray.length()) {
            val obj = tasbeehArray.getJSONObject(i)
            val targetsJson = obj.getJSONArray("targets").toString()

            result.add(
                TasbeehEntity(
                    id = obj.getInt("id"),
                    slug = obj.getString("slug"),
                    text = obj.getString("text"),
                    targets = targetsJson,
                    category = obj.getString("category"),
                    virtue = obj.getString("virtue"),
                    source = obj.getString("source"),
                    priority = obj.getInt("priority"),
                    isDefault = obj.getBoolean("isDefault"),
                    currentCount = 0
                )
            )
        }
        return result
    }
    // ===== Quran =====
    fun mapQuran(jsonString: String): List<AyahEntity> {
        val ayahsList = mutableListOf<AyahEntity>()
        val rootObject = JSONObject(jsonString)

        val surahKeys = rootObject.keys()

        while (surahKeys.hasNext()) {
            val surahKey = surahKeys.next()
            val ayahsArray = rootObject.getJSONArray(surahKey)

            for (i in 0 until ayahsArray.length()) {
                val ayahJson = ayahsArray.getJSONObject(i)

                ayahsList.add(
                    AyahEntity(
                        surahId = ayahJson.getInt("chapter"),
                        number = ayahJson.getInt("verse"),
                        text = ayahJson.getString("text")
                    )
                )
            }
        }
        return ayahsList
    }
}
