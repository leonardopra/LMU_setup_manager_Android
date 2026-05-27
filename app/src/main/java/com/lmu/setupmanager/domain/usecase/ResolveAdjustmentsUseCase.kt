package com.lmu.setupmanager.domain.usecase

import com.lmu.setupmanager.data.static.carById
import com.lmu.setupmanager.data.static.feedbackItemById
import com.lmu.setupmanager.data.static.parametersByKey
import com.lmu.setupmanager.domain.model.AdjustmentContributor
import com.lmu.setupmanager.domain.model.CornerTarget
import com.lmu.setupmanager.domain.model.ResolvedAdjustment
import com.lmu.setupmanager.domain.model.SelectedSymptom
import javax.inject.Inject

class ResolveAdjustmentsUseCase @Inject constructor() {

    operator fun invoke(
        carId: String,
        currentValues: Map<String, Float>,
        symptoms: List<SelectedSymptom>
    ): List<ResolvedAdjustment> {
        val car = carById[carId]

        // Accumulate contributors per store key across all symptoms
        val contributorsPerKey = mutableMapOf<String, MutableList<AdjustmentContributor>>()

        for (symptom in symptoms) {
            val feedbackItem = feedbackItemById[symptom.feedbackId] ?: continue
            val scale = severityScale(symptom.severity)

            for (delta in feedbackItem.deltas) {
                val param = parametersByKey[delta.paramKey] ?: continue
                val scaledDelta = delta.baseDelta * scale
                val storeKeys = expandStoreKeys(delta.paramKey, delta.cornerTarget, param.cornerSpecific)
                val contributor = AdjustmentContributor(
                    feedbackId = feedbackItem.id,
                    feedbackLabel = feedbackItem.label,
                    scaledDelta = scaledDelta,
                    rationale = delta.rationale
                )
                for (storeKey in storeKeys) {
                    contributorsPerKey.getOrPut(storeKey) { mutableListOf() }.add(contributor)
                }
            }
        }

        return contributorsPerKey.mapNotNull { (storeKey, contributors) ->
            val netDelta = contributors.fold(0f) { acc, c -> acc + c.scaledDelta }
            if (netDelta == 0f) return@mapNotNull null

            val (paramKey, corner) = splitStoreKey(storeKey)
            val param = parametersByKey[paramKey] ?: return@mapNotNull null

            val override = car?.parameterOverrides?.get(paramKey)
            val effectiveMin = override?.min ?: param.min
            val effectiveMax = override?.max ?: param.max

            val currentValue = currentValues[storeKey] ?: 0f
            val proposedValue = (currentValue + netDelta).coerceIn(effectiveMin, effectiveMax)

            val wasConflicted = contributors.any { it.scaledDelta > 0f } &&
                contributors.any { it.scaledDelta < 0f }

            val paramLabel = if (corner != null) "${param.label} ($corner)" else param.label

            ResolvedAdjustment(
                storeKey = storeKey,
                paramLabel = paramLabel,
                currentValue = currentValue,
                proposedValue = proposedValue,
                netDelta = netDelta,
                contributors = contributors,
                wasConflicted = wasConflicted
            )
        }
    }

    // severity 1 → ×1.0, 2 → ×1.5, 3 → ×2.0; anything else falls back to ×1.0
    private fun severityScale(severity: Int): Float = when (severity) {
        1 -> 1.0f
        2 -> 1.5f
        3 -> 2.0f
        else -> 1.0f
    }

    /**
     * Expands a parameter key + corner target into concrete store keys.
     * Non-corner-specific parameters always collapse to the base key regardless of target.
     */
    private fun expandStoreKeys(
        paramKey: String,
        cornerTarget: CornerTarget,
        cornerSpecific: Boolean
    ): List<String> {
        if (!cornerSpecific) return listOf(paramKey)
        val corners = when (cornerTarget) {
            CornerTarget.ALL   -> listOf("FL", "FR", "RL", "RR")
            CornerTarget.FRONT -> listOf("FL", "FR")
            CornerTarget.REAR  -> listOf("RL", "RR")
            CornerTarget.FL    -> listOf("FL")
            CornerTarget.FR    -> listOf("FR")
            CornerTarget.RL    -> listOf("RL")
            CornerTarget.RR    -> listOf("RR")
        }
        return corners.map { "${paramKey}_$it" }
    }

    /**
     * Splits a store key back into (paramKey, corner?).
     * Returns corner = null for non-corner-specific keys.
     * Parameter keys never contain underscores, so the last segment is always the corner suffix.
     */
    private fun splitStoreKey(storeKey: String): Pair<String, String?> {
        val lastUnderscore = storeKey.lastIndexOf('_')
        if (lastUnderscore == -1) return storeKey to null
        val potentialCorner = storeKey.substring(lastUnderscore + 1)
        return if (potentialCorner in CORNERS) {
            storeKey.substring(0, lastUnderscore) to potentialCorner
        } else {
            storeKey to null
        }
    }

    private companion object {
        val CORNERS = setOf("FL", "FR", "RL", "RR")
    }
}
