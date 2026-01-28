package com.example.sakina.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.entity.SurahEntity
import com.example.sakina.data.local.database.entity.AyahEntity

@Database(
    entities = [
        CategoryEntity::class,
        ZikrEntity::class,
        SurahEntity::class,
        AyahEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun azkarDao(): AzkarDao
    abstract fun quranDao(): QuranDao
}