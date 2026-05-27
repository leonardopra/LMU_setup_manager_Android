package com.lmu.setupmanager.data.repository

import com.lmu.setupmanager.data.local.SetupDao
import com.lmu.setupmanager.data.local.toDomain
import com.lmu.setupmanager.data.local.toEntity
import com.lmu.setupmanager.domain.model.Setup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SetupRepositoryImpl @Inject constructor(
    private val dao: SetupDao
) : SetupRepository {

    override fun getAllSetups(): Flow<List<Setup>> =
        dao.getAllSetups().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSetupById(id: String): Setup? =
        dao.getSetupById(id)?.toDomain()

    override suspend fun saveSetup(setup: Setup) =
        dao.insertSetup(setup.toEntity())

    override suspend fun deleteSetup(id: String) =
        dao.deleteSetupById(id)
}
