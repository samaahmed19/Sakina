package com.example.sakina.data.source.mapper


import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.entity.DuaEntity
import org.json.JSONObject

object JsonMapper {

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

    fun mapDuas(json: String): List<DuaEntity> {
        val duasList = mutableListOf<DuaEntity>()

        val root = JSONObject(json)
        val categoriesArray = root.getJSONArray("categories")

        for (i in 0 until categoriesArray.length()) {
            val categoryObj = categoriesArray.getJSONObject(i)

            val categoryId = categoryObj.getInt("id")
            val categoryName = categoryObj.getString("name")

            val duasArray = categoryObj.getJSONArray("duas")

            for (j in 0 until duasArray.length()) {
                val duaObj = duasArray.getJSONObject(j)

                duasList.add(
                    DuaEntity(
                        id = duaObj.getInt("id"),
                        text = duaObj.getString("text"),
                        categoryId = categoryId,
                        categoryName = categoryName,
                        isFavorite = false
                    )
                )
            }
        }
        return duasList
    }
}
