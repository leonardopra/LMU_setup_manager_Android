package com.lmu.setupmanager.domain.model

enum class Conditions { DRY, WET, MIXED }

data class Setup(
    val id: String,
    val name: String,
    val carId: String,
    val trackId: String,
    val conditions: Conditions,
    val values: Map<String, Float>,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)
