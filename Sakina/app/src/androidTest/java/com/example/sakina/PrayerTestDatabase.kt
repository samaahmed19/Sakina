package com.example.sakina

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sakina.data.local.database.dao.PrayerDao
import com.example.sakina.data.local.database.entity.PrayerEntity

@Database(
    entities = [PrayerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PrayerTestDatabase : RoomDatabase() {
    abstract fun prayerDao(): PrayerDao
}
