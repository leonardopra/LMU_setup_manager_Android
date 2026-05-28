package com.lmu.setupmanager.data.local

import com.lmu.setupmanager.domain.model.Setup
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun SetupEntity.toDomain(): Setup = Setup(
    id = id,
    name = name,
    carId = carId,
    trackId = trackId,
    conditions = conditions,   // Conditions → Conditions (Room handles String↔Conditions)
    values = json.decodeFromString<Map<String, Float>>(valuesJson),
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Setup.toEntity(): SetupEntity = SetupEntity(
    id = id,
    name = name,
    carId = carId,
    trackId = trackId,
    conditions = conditions,   // Conditions → Conditions (Room handles String↔Conditions)
    valuesJson = json.encodeToString(values),
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
