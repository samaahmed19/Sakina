package com.example.sakina.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.dao.PrayerDao
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.entity.PrayerEntity
import com.example.sakina.data.local.database.dao.DuaDao
import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.entity.DuaEntity
import com.example.sakina.data.local.database.entity.SurahEntity
import com.example.sakina.data.local.database.entity.AyahEntity
import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.entity.ChecklistEntity
import com.example.sakina.data.local.database.dao.TasbeehDao
import com.example.sakina.data.local.database.dao.UserDao
import com.example.sakina.data.local.database.entity.DuaCategoryEntity
import com.example.sakina.data.local.database.entity.TasbeehEntity
import com.example.sakina.data.local.database.entity.UserEntity


@Database(
    entities = [
        CategoryEntity::class,
        ZikrEntity::class,
        PrayerEntity::class,
        DuaEntity::class,
        DuaCategoryEntity::class,
        SurahEntity::class,
        AyahEntity::class,
        ChecklistEntity::class,
        TasbeehEntity::class,
        UserEntity::class
    ],
    version = 3, // أضفنا UserEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun azkarDao(): AzkarDao
    abstract fun prayerDao(): PrayerDao

    abstract fun duaDao(): DuaDao

    abstract fun quranDao(): QuranDao

    abstract fun checklistDao(): ChecklistDao

    abstract fun tasbeehDao(): TasbeehDao

    abstract fun userDao(): UserDao
}




