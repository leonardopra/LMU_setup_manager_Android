package com.lmu.setupmanager.domain.usecase

import android.content.Context
import android.net.Uri
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.domain.model.Setup
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

/**
 * Reads a setup JSON document (as produced by [ExportSetupUseCase]) from a [Uri],
 * deserializes it with kotlinx.serialization, assigns a fresh id, and persists it.
 *
 * Throws [IOException] if the URI cannot be read and [SerializationException] if the
 * content is not a valid setup document — the caller maps these to user-facing messages.
 */
class ImportSetupUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SetupRepository
) {

    /** Mirrors the export shape; `label` maps to [Setup.name]. */
    @Serializable
    private data class ImportedSetup(
        val carId: String,
        val trackId: String,
        val conditions: Conditions,
        val label: String,
        val values: Map<String, Float>
    )

    suspend operator fun invoke(uri: Uri) {
        val raw = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes().decodeToString()
        } ?: throw IOException("Unable to open input stream for $uri")

        val imported = json.decodeFromString<ImportedSetup>(raw)
        val now = System.currentTimeMillis()
        repository.saveSetup(
            Setup(
                id = UUID.randomUUID().toString(),
                name = imported.label,
                carId = imported.carId,
                trackId = imported.trackId,
                conditions = imported.conditions,
                values = imported.values,
                notes = "",
                createdAt = now,
                updatedAt = now
            )
        )
    }

    private companion object {
        val json = Json { ignoreUnknownKeys = true }
    }
}
