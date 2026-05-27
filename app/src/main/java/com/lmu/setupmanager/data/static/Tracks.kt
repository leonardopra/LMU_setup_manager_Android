package com.lmu.setupmanager.data.static

import com.lmu.setupmanager.domain.model.Track
import com.lmu.setupmanager.domain.model.TrackCharacteristic

val tracks: List<Track> = listOf(
    Track(
        id = "le-mans",
        name = "Circuit de la Sarthe",
        country = "France",
        characteristics = listOf(TrackCharacteristic.HIGH_SPEED, TrackCharacteristic.BALANCED),
        layout = "circuit"
    ),
    Track(
        id = "spa",
        name = "Circuit de Spa-Francorchamps",
        country = "Belgium",
        characteristics = listOf(
            TrackCharacteristic.HIGH_SPEED,
            TrackCharacteristic.TECHNICAL,
            TrackCharacteristic.BUMPY
        ),
        layout = "circuit"
    ),
    Track(
        id = "monza",
        name = "Autodromo Nazionale Monza",
        country = "Italy",
        characteristics = listOf(TrackCharacteristic.HIGH_SPEED),
        layout = "circuit"
    ),
    Track(
        id = "fuji",
        name = "Fuji Speedway",
        country = "Japan",
        characteristics = listOf(TrackCharacteristic.HIGH_SPEED, TrackCharacteristic.TECHNICAL),
        layout = "circuit"
    ),
    Track(
        id = "bahrain",
        name = "Bahrain International Circuit",
        country = "Bahrain",
        characteristics = listOf(TrackCharacteristic.TECHNICAL, TrackCharacteristic.BALANCED),
        layout = "circuit"
    ),
    Track(
        id = "imola",
        name = "Autodromo Enzo e Dino Ferrari",
        country = "Italy",
        characteristics = listOf(TrackCharacteristic.TECHNICAL, TrackCharacteristic.BUMPY),
        layout = "circuit"
    ),
    Track(
        id = "cota",
        name = "Circuit of the Americas",
        country = "USA",
        characteristics = listOf(TrackCharacteristic.TECHNICAL, TrackCharacteristic.BUMPY),
        layout = "circuit"
    ),
    Track(
        id = "portimao",
        name = "Autodromo Internacional do Algarve",
        country = "Portugal",
        characteristics = listOf(TrackCharacteristic.TECHNICAL, TrackCharacteristic.BUMPY),
        layout = "circuit"
    ),
    Track(
        id = "interlagos",
        name = "Autodromo Jose Carlos Pace",
        country = "Brazil",
        characteristics = listOf(TrackCharacteristic.TECHNICAL, TrackCharacteristic.BUMPY),
        layout = "circuit"
    ),
    Track(
        id = "lusail",
        name = "Lusail International Circuit",
        country = "Qatar",
        characteristics = listOf(TrackCharacteristic.HIGH_SPEED, TrackCharacteristic.TECHNICAL),
        layout = "circuit"
    )
)

val trackById: Map<String, Track> = tracks.associateBy { it.id }
