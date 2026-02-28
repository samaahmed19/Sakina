package com.sama.sakina

import com.sama.sakina.data.source.mapper.JsonMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [JsonMapper].
 * Verifies parsing of azkar, tasbeeh (and behaviour on invalid input where supported)
 * without changing production code.
 */
class JsonMapperTest {

    @Test
    fun mapCategories_validMinimalJson_returnsCategoriesAndAzkar() {
        val json = """
            {"categories":[{"id":"1","title":"أذكار الصباح","icon":"sun","azkar":[{"text":"سبحان الله","repeat":1}]}]}
        """.trimIndent()
        val (categories, azkar) = JsonMapper.mapCategories(json)
        assertTrue(categories.isNotEmpty())
        assertTrue(azkar.isNotEmpty())
        assertEquals("1", categories[0].id)
        assertEquals("أذكار الصباح", categories[0].title)
        assertEquals("سبحان الله", azkar[0].text)
    }

    @Test
    fun mapCategories_emptyCategories_returnsEmptyLists() {
        val json = """{"categories":[]}"""
        val (categories, azkar) = JsonMapper.mapCategories(json)
        assertTrue(categories.isEmpty())
        assertTrue(azkar.isEmpty())
    }

    @Test
    fun mapTasbeeh_validMinimalJson_returnsList() {
        val json = """
            {"tasbeeh":[{"id":1,"slug":"subhan","text":"سبحان الله","targets":[],"category":"عام"}]}
        """.trimIndent()
        val result = JsonMapper.mapTasbeeh(json)
        assertTrue(result.isNotEmpty())
        assertEquals(1, result[0].id)
        assertEquals("سبحان الله", result[0].text)
    }

    @Test
    fun mapTasbeeh_emptyArray_returnsEmptyList() {
        val json = """{"tasbeeh":[]}"""
        val result = JsonMapper.mapTasbeeh(json)
        assertTrue(result.isEmpty())
    }
}
