package com.lmu.setupmanager.data.repository

import com.lmu.setupmanager.domain.model.Setup
import kotlinx.coroutines.flow.Flow

interface SetupRepository {
    fun getAllSetups(): Flow<List<Setup>>
    suspend fun getSetupById(id: String): Setup?
    suspend fun saveSetup(setup: Setup)
    suspend fun deleteSetup(id: String)
}
