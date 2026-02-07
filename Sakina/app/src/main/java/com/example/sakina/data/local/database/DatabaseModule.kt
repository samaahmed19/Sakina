package com.example.sakina.data.local.database


import android.content.Context
import androidx.room.Room

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sakina.data.local.database.AppDatabase
import com.example.sakina.data.local.database.dao.AzkarDao
import com.example.sakina.data.local.database.dao.DuaDao
import com.example.sakina.data.local.database.dao.ChecklistDao
import com.example.sakina.data.local.database.dao.PrayerDao
import com.example.sakina.data.local.database.dao.QuranDao
import com.example.sakina.data.local.database.dao.TafsirDao
import com.example.sakina.data.local.database.dao.TasbeehDao
import com.example.sakina.data.local.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sakina_db"
        )
            // ده أهم سطر: لو غيرت أي Entity، هيمسح القديم ويعمل الجديد بدل ما يعمل Crash
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAzkarDao(
        database: AppDatabase
    ): AzkarDao = database.azkarDao()
    @Provides
    fun providePrayerDao(database: AppDatabase): PrayerDao = database.prayerDao()
    @Provides
    fun provideDuaDao(
        database: AppDatabase
    ): DuaDao = database.duaDao()
    @Provides
    fun provideQuranDao(
        database: AppDatabase
    ): QuranDao = database.quranDao()
    @Provides
    fun provideTafsirDao(database: AppDatabase): TafsirDao {
        return database.tafsirDao()
    }
    @Provides
    fun provideChecklistDao(
        database: AppDatabase
    ): ChecklistDao = database.checklistDao()
    @Provides
    fun provideTasbeehDao(
        database: AppDatabase
    ): TasbeehDao = database.tasbeehDao()

    @Provides
    fun provideUserDao(
        database: AppDatabase
    ): UserDao = database.userDao()

}




