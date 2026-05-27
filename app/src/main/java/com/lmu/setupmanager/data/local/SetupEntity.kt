package com.lmu.setupmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setups")
data class SetupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val carId: String,
    val trackId: String,
    val conditions: String,       // "DRY" | "WET" | "MIXED"
    val valuesJson: String,       // Map<String, Float> serialized as JSON
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)
