package com.lmu.setupmanager.di

import android.content.Context
import androidx.room.Room
import com.lmu.setupmanager.data.local.SetupDao
import com.lmu.setupmanager.data.local.SetupDatabase
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.data.repository.SetupRepositoryImpl
import dagger.Binds
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
    fun provideSetupDatabase(@ApplicationContext context: Context): SetupDatabase =
        Room.databaseBuilder(
            context,
            SetupDatabase::class.java,
            SetupDatabase.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideSetupDao(database: SetupDatabase): SetupDao = database.setupDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSetupRepository(impl: SetupRepositoryImpl): SetupRepository
}
