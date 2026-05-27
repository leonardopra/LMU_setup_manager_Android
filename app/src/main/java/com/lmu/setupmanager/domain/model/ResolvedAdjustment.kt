package com.lmu.setupmanager.domain.model

data class AdjustmentContributor(
    val feedbackId: String,
    val feedbackLabel: String,
    val scaledDelta: Float,
    val rationale: String
)

data class ResolvedAdjustment(
    val storeKey: String,
    val paramLabel: String,
    val currentValue: Float,
    val proposedValue: Float,
    val netDelta: Float,
    val contributors: List<AdjustmentContributor>,
    val wasConflicted: Boolean
)
