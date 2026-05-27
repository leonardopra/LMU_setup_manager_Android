package com.lmu.setupmanager.domain.model

data class ParameterOverride(
    val min: Float? = null,
    val max: Float? = null,
    val defaultValue: Float? = null
)

data class Car(
    val id: String,
    val name: String,
    val manufacturer: String,
    val carClass: String,
    val parameterOverrides: Map<String, ParameterOverride> = emptyMap()
)
