package com.lmu.setupmanager.domain.model

enum class TrackCharacteristic(val id: String) {
    HIGH_SPEED("highSpeed"),
    TECHNICAL("technical"),
    BUMPY("bumpy"),
    STREET("street"),
    BALANCED("balanced")
}

data class Track(
    val id: String,
    val name: String,
    val country: String,
    val characteristics: List<TrackCharacteristic>,
    val layout: String // "circuit" | "street"
)
