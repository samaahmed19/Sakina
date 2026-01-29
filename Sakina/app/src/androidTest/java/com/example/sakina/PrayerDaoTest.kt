package com.example.sakina

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.sakina.data.local.database.entity.PrayerEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PrayerDaoTest {

    private lateinit var db: PrayerTestDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PrayerTestDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun upsert_and_getByDate_works() = runBlocking {
        val dao = db.prayerDao()
        val date = "2026-01-29"

        dao.upsert(
            PrayerEntity(
                date = date,
                key = "PRAYER_FAJR",
                isCompleted = true,
                completedAt = 100L
            )
        )

        val rows = dao.getByDate(date)
        assertEquals(1, rows.size)
        assertEquals("PRAYER_FAJR", rows.first().key)
        assertEquals(true, rows.first().isCompleted)

        // update same key same date
        dao.upsert(
            PrayerEntity(
                date = date,
                key = "PRAYER_FAJR",
                isCompleted = false,
                completedAt = null
            )
        )

        val updated = dao.getByDate(date)
        assertEquals(1, updated.size)
        assertEquals(false, updated.first().isCompleted)
    }
}
