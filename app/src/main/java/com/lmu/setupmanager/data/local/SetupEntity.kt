package com.lmu.setupmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lmu.setupmanager.domain.model.Conditions

@Entity(tableName = "setups")
data class SetupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val carId: String,
    val trackId: String,
    val conditions: Conditions,    // stored as name string via ConditionsConverter
    val valuesJson: String,        // Map<String, Float> serialized as JSON
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)
