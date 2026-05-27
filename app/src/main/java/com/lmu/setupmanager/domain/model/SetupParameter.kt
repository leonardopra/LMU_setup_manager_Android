package com.lmu.setupmanager.domain.model

enum class DataType { FLOAT, INT, ENUM }

data class EnumOption(val label: String, val value: Int)

data class SetupParameter(
    val key: String,
    val category: String,
    val label: String,
    val description: String,
    val unit: String,
    val dataType: DataType,
    val min: Float,
    val max: Float,
    val step: Float,
    val enumOptions: List<EnumOption>? = null,
    val defaultValue: Float,
    val cornerSpecific: Boolean
)
