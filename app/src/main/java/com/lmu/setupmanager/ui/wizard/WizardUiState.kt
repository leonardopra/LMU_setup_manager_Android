package com.lmu.setupmanager.ui.wizard

import com.lmu.setupmanager.domain.model.FeedbackCategory
import com.lmu.setupmanager.domain.model.ResolvedAdjustment
import com.lmu.setupmanager.domain.model.SelectedSymptom

sealed class WizardStep {
    data object SelectCategory    : WizardStep()
    data object SelectSymptoms    : WizardStep()
    data object SetSeverity       : WizardStep()
    data object ReviewAdjustments : WizardStep()
    data object Confirm           : WizardStep()
}

data class WizardUiState(
    val step: WizardStep = WizardStep.SelectCategory,
    val carId: String,
    val currentValues: Map<String, Float>,
    val selectedCategory: FeedbackCategory? = null,
    val selectedSymptoms: List<SelectedSymptom> = emptyList(),
    val resolvedAdjustments: List<ResolvedAdjustment> = emptyList(),
    val isApplied: Boolean = false
)
