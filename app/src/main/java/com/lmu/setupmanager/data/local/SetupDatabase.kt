package com.lmu.setupmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SetupEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ConditionsConverter::class)
abstract class SetupDatabase : RoomDatabase() {
    abstract fun setupDao(): SetupDao

    companion object {
        const val DATABASE_NAME = "lmu_setups.db"
    }
}
