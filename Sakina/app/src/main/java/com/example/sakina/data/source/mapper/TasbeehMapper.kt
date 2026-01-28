package com.example.sakina.data.source.mapper

import com.example.sakina.data.local.database.entity.TasbeehEntity
import com.example.sakina.domain.model.Tasbeeh
import org.json.JSONArray

object TasbeehMapper {

    fun entityToDomain(entity: TasbeehEntity): Tasbeeh {
        return Tasbeeh(
            id = entity.id,
            slug = entity.slug,
            text = entity.text,
            targets = JSONArray(entity.targets).let { arr ->
                List(arr.length()) { index -> arr.getInt(index) }
            },
            currentCount = entity.currentCount,
            category = entity.category,
            virtue = entity.virtue,
            source = entity.source,
            priority = entity.priority,
            isDefault = entity.isDefault
        )
    }
}
