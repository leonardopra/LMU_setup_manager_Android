package com.lmu.setupmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SetupEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SetupDatabase : RoomDatabase() {
    abstract fun setupDao(): SetupDao

    companion object {
        const val DATABASE_NAME = "lmu_setups.db"
    }
}
