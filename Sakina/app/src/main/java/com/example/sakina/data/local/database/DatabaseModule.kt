package com.example.sakina.data.local.database


import android.content.Context
import androidx.room.Room
import com.example.sakina.data.local.database.AppDatabase
import com.example.sakina.data.local.database.dao.AzkarDao
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
        ).build()
    }

    @Provides
    fun provideAzkarDao(
        database: AppDatabase
    ): AzkarDao = database.azkarDao()
    fun provideQuranDao(
        database: AppDatabase
    ): QuranDao = database.quranDao()

}
