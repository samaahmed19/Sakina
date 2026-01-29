package com.example.sakina.data.local.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.entity.CategoryEntity
import com.example.sakina.data.local.database.entity.ZikrEntity
import com.example.sakina.data.local.database.dao.DuaDao
import com.example.sakina.data.local.database.entity.DuaEntity

@Database(
    entities = [
        CategoryEntity::class,
        ZikrEntity::class,
        DuaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun azkarDao(): AzkarDao

    abstract fun duaDao(): DuaDao
}
