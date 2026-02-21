package com.sama.sakina.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sama.sakina.data.local.database.dao.AzkarDao
import com.sama.sakina.data.local.database.dao.PrayerDao
import com.sama.sakina.data.local.database.entity.CategoryEntity
import com.sama.sakina.data.local.database.entity.ZikrEntity
import com.sama.sakina.data.local.database.entity.PrayerEntity
import com.sama.sakina.data.local.database.dao.DuaDao
import com.sama.sakina.data.local.database.dao.QuranDao
import com.sama.sakina.data.local.database.entity.DuaEntity
import com.sama.sakina.data.local.database.entity.DuaCategoryEntity
import com.sama.sakina.data.local.database.entity.SurahEntity
import com.sama.sakina.data.local.database.entity.AyahEntity
import com.sama.sakina.data.local.database.dao.ChecklistDao
import com.sama.sakina.data.local.database.dao.StreakDao
import com.sama.sakina.data.local.database.entity.ChecklistEntity
import com.sama.sakina.data.local.database.entity.StreakEntity
import com.sama.sakina.data.local.database.dao.TasbeehDao
import com.sama.sakina.data.local.database.dao.UserDao
import com.sama.sakina.data.local.database.dao.TafsirDao
import com.sama.sakina.data.local.database.entity.TafsirEntity
import com.sama.sakina.data.local.database.entity.TasbeehEntity
import com.sama.sakina.data.local.database.entity.UserEntity



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
        StreakEntity::class,
        TasbeehEntity::class,
        UserEntity::class,
        TafsirEntity::class
    ],
    version = 4,
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
    abstract fun tafsirDao(): TafsirDao
    abstract fun streakDao(): StreakDao
}




