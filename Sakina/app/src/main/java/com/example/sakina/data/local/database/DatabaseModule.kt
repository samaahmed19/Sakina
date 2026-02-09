package com.example.sakina.data.local.database

import android.content.Context
import androidx.room.Room
import com.example.sakina.data.local.database.dao.*
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
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAzkarDao(database: AppDatabase): AzkarDao = database.azkarDao()

    @Provides
    fun providePrayerDao(database: AppDatabase): PrayerDao = database.prayerDao()

    @Provides
    fun provideDuaDao(database: AppDatabase): DuaDao = database.duaDao()

    @Provides
    fun provideQuranDao(database: AppDatabase): QuranDao = database.quranDao()

    @Provides
    fun provideTafsirDao(database: AppDatabase): TafsirDao = database.tafsirDao()

    @Provides
    fun provideChecklistDao(database: AppDatabase): ChecklistDao = database.checklistDao()

    @Provides
    fun provideStreakDao(database: AppDatabase): StreakDao {
        return database.streakDao()
    }
    @Provides
    fun provideTasbeehDao(database: AppDatabase): TasbeehDao = database.tasbeehDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}