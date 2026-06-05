package com.lmu.setupmanager.domain.usecase

import com.lmu.setupmanager.data.local.SetupEntity
import com.lmu.setupmanager.domain.model.Conditions
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Serializes a [SetupEntity] to a shareable JSON string.
 *
 * Output shape: id, carId, trackId, conditions, label (the setup name), and the values map.
 * The entity's [SetupEntity.valuesJson] is decoded so the exported `values` is a real map
 * rather than an escaped JSON string.
 */
class ExportSetupUseCase @Inject constructor() {

    @Serializable
    private data class ExportedSetup(
        val id: String,
        val carId: String,
        val trackId: String,
        val conditions: Conditions,
        val label: String,
        val values: Map<String, Float>
    )

    operator fun invoke(entity: SetupEntity): String {
        val values: Map<String, Float> = json.decodeFromString(entity.valuesJson)
        val exported = ExportedSetup(
            id = entity.id,
            carId = entity.carId,
            trackId = entity.trackId,
            conditions = entity.conditions,
            label = entity.name,
            values = values
        )
        return json.encodeToString(exported)
    }

    private companion object {
        val json = Json { prettyPrint = true }
    }
}
