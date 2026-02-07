package com.example.sakina.data.source.mapper

import com.example.sakina.data.local.database.entity.*
import org.json.JSONArray
import org.json.JSONObject

object JsonMapper {

    // ===== Azkar (إصلاح ظهور الأذكار) =====
    fun mapCategories(json: String): Pair<List<CategoryEntity>, List<ZikrEntity>> {
        val categories = mutableListOf<CategoryEntity>()
        val azkar = mutableListOf<ZikrEntity>()

        try {
            val root = JSONObject(json)
            val categoriesArray = root.getJSONArray("categories")

            for (i in 0 until categoriesArray.length()) {
                val categoryObj = categoriesArray.getJSONObject(i)
                val categoryId = categoryObj.optString("id", i.toString())

                categories.add(
                    CategoryEntity(
                        id = categoryId,
                        title = categoryObj.optString("title", "بدون عنوان"),
                        icon = categoryObj.optString("icon", "")
                    )
                )

                val azkarArray = categoryObj.optJSONArray("azkar") ?: JSONArray()
                for (j in 0 until azkarArray.length()) {
                    val zikrObj = azkarArray.getJSONObject(j)

                    azkar.add(
                        ZikrEntity(
                            categoryId = categoryId,
                            text = zikrObj.optString("text", ""),
                            repeat = zikrObj.optInt("repeat", 1),
                            reward = zikrObj.optString("reward", null)
                        )
                    )
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        return Pair(categories, azkar)
    }

    // ===== Duas (إصلاح جوامع الدعاء) =====
    fun mapDuas(json: String): Pair<List<DuaCategoryEntity>, List<DuaEntity>> {
        val categories = mutableListOf<DuaCategoryEntity>()
        val duas = mutableListOf<DuaEntity>()

        try {
            val root = JSONObject(json)
            val categoryArray = root.getJSONArray("categories")

            for (i in 0 until categoryArray.length()) {
                val catObj = categoryArray.getJSONObject(i)
                val catId = catObj.optString("id", i.toString())

                val duasArray = catObj.optJSONArray("duas") ?: JSONArray()
                val currentCategoryDuaCount = duasArray.length()

                categories.add(
                    DuaCategoryEntity(
                        id = catId,
                        title = catObj.optString("name", catObj.optString("title", "دعاء")),
                        icon = "splash",
                        count = currentCategoryDuaCount
                    )
                )

                for (j in 0 until duasArray.length()) {
                    val duaObj = duasArray.getJSONObject(j)
                    duas.add(
                        DuaEntity(
                            categoryId = catId,
                            text = duaObj.optString("text", "")
                        )
                    )
                }
            }
        } catch (e: Exception) { e.printStackTrace() }

        return Pair(categories, duas)
    }

    // ===== Tasbeeh (إصلاح شاشة التسبيح) =====
    fun mapTasbeeh(json: String): List<TasbeehEntity> {
        val result = mutableListOf<TasbeehEntity>()
        try {
            val root = JSONObject(json)
            val tasbeehArray = root.getJSONArray("tasbeeh")

            for (i in 0 until tasbeehArray.length()) {
                val obj = tasbeehArray.getJSONObject(i)
                val targetsJson = obj.optJSONArray("targets")?.toString() ?: "[]"

                result.add(
                    TasbeehEntity(
                        id = obj.optInt("id", i),
                        slug = obj.optString("slug", ""),
                        text = obj.optString("text", ""),
                        targets = targetsJson,
                        category = obj.optString("category", "عام"),
                        virtue = obj.optString("virtue", ""),
                        source = obj.optString("source", ""),
                        priority = obj.optInt("priority", 0),
                        isDefault = obj.optBoolean("isDefault", false),
                        currentCount = 0
                    )
                )
            }
        } catch (e: Exception) { e.printStackTrace() }
        return result
    }

    // ===== Quran =====
    fun mapQuran(jsonString: String): List<AyahEntity> {
        val ayahsList = mutableListOf<AyahEntity>()
        try {
            val rootObject = JSONObject(jsonString)
            val surahKeys = rootObject.keys()

            while (surahKeys.hasNext()) {
                val surahKey = surahKeys.next()
                val ayahsArray = rootObject.getJSONArray(surahKey)

                for (i in 0 until ayahsArray.length()) {
                    val ayahJson = ayahsArray.getJSONObject(i)
                    ayahsList.add(
                        AyahEntity(
                            surahId = ayahJson.optInt("chapter", 0),
                            number = ayahJson.optInt("verse", 0),
                            text = ayahJson.optString("text", "")
                        )
                    )
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        return ayahsList
    }
}