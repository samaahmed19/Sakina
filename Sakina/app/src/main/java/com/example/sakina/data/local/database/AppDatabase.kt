package com.example.sakina.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.dao.PrayerDao
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.entity.PrayerEntity

@Database(
    entities = [
        CategoryEntity::class,
        ZikrEntity::class,
        PrayerEntity::class
    ],
    version = 2, // as schema changed
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun azkarDao(): AzkarDao
    abstract fun prayerDao(): PrayerDao
}
