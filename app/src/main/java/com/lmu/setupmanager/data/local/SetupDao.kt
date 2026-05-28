package com.lmu.setupmanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SetupDao {

    @Query("SELECT * FROM setups ORDER BY updatedAt DESC")
    fun getAllSetups(): Flow<List<SetupEntity>>

    @Query("SELECT * FROM setups WHERE id = :id")
    suspend fun getSetupById(id: String): SetupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetup(setup: SetupEntity)

    @Update
    suspend fun updateSetup(setup: SetupEntity)

    @Delete
    suspend fun deleteSetup(setup: SetupEntity)

    @Query("DELETE FROM setups WHERE id = :id")
    suspend fun deleteSetupById(id: String)

    @Query("UPDATE setups SET name = :name, updatedAt = :updatedAt WHERE id = :id")
    suspend fun renameSetup(id: String, name: String, updatedAt: Long)
}
