package com.lmu.setupmanager.ui.wizard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.lmu.setupmanager.domain.model.FeedbackCategory
import com.lmu.setupmanager.domain.model.SelectedSymptom
import com.lmu.setupmanager.domain.usecase.ResolveAdjustmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val wizardJson = Json { ignoreUnknownKeys = true }

@HiltViewModel
class WizardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resolveAdjustments: ResolveAdjustmentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        WizardUiState(
            carId = requireNotNull(savedStateHandle.get<String>("carId")) {
                "WizardViewModel requires a 'carId' navigation argument"
            },
            currentValues = wizardJson.decodeFromString(
                savedStateHandle.get<String>("currentValues") ?: "{}"
            )
        )
    )
    val uiState: StateFlow<WizardUiState> = _uiState.asStateFlow()

    // ── Public commands ───────────────────────────────────────────────────────

    /**
     * Selects a feedback category and advances to [WizardStep.SelectSymptoms].
     * Clears any previously selected symptoms since they are category-scoped.
     */
    fun selectCategory(category: FeedbackCategory) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                selectedSymptoms = emptyList(),
                step = WizardStep.SelectSymptoms
            )
        }
    }

    /**
     * Adds the symptom with severity=1 if not yet selected; removes it if already present.
     */
    fun toggleSymptom(feedbackId: String) {
        _uiState.update { state ->
            val alreadySelected = state.selectedSymptoms.any { it.feedbackId == feedbackId }
            val updated = if (alreadySelected) {
                state.selectedSymptoms.filterNot { it.feedbackId == feedbackId }
            } else {
                state.selectedSymptoms + SelectedSymptom(feedbackId, severity = 1)
            }
            state.copy(selectedSymptoms = updated)
        }
    }

    /**
     * Updates the severity (1–3) for an already-selected symptom. No-op if not found.
     */
    fun setSeverity(feedbackId: String, severity: Int) {
        _uiState.update { state ->
            state.copy(
                selectedSymptoms = state.selectedSymptoms.map { symptom ->
                    if (symptom.feedbackId == feedbackId) symptom.copy(severity = severity)
                    else symptom
                }
            )
        }
    }

    /**
     * Advances to the next wizard step.
     * - Requires at least one symptom selected when leaving [WizardStep.SelectSymptoms].
     * - When entering [WizardStep.ReviewAdjustments], the use case is run and
     *   [WizardUiState.resolvedAdjustments] is populated synchronously.
     * - No-op when already on the last step.
     */
    fun next() {
        val state = _uiState.value
        // Guard: don't advance past symptom selection with an empty list
        if (state.step == WizardStep.SelectSymptoms && state.selectedSymptoms.isEmpty()) return

        val nextStep = stepAfter(state.step) ?: return
        _uiState.update { s ->
            val resolved = if (nextStep == WizardStep.ReviewAdjustments) {
                resolveAdjustments(
                    carId = s.carId,
                    currentValues = s.currentValues,
                    symptoms = s.selectedSymptoms
                )
            } else {
                s.resolvedAdjustments
            }
            s.copy(step = nextStep, resolvedAdjustments = resolved)
        }
    }

    /**
     * Returns to the previous wizard step.
     * Clears [WizardUiState.selectedCategory] when returning to [WizardStep.SelectCategory]
     * so the category list presents fresh.
     * No-op when already on the first step.
     */
    fun back() {
        val prevStep = stepBefore(_uiState.value.step) ?: return
        _uiState.update { state ->
            state.copy(
                step = prevStep,
                selectedCategory = if (prevStep == WizardStep.SelectCategory) null
                                   else state.selectedCategory
            )
        }
    }

    /**
     * Signals that the user has confirmed the adjustments.
     * The caller (SetupScreen) should observe [WizardUiState.isApplied] and call
     * [SetupViewModel.batchUpdateValues] with the proposed values from [WizardUiState.resolvedAdjustments].
     */
    fun confirm() {
        _uiState.update { it.copy(isApplied = true) }
    }

    /** Resets the one-shot [WizardUiState.isApplied] flag after the caller has handled it. */
    fun consumeApplied() {
        _uiState.update { it.copy(isApplied = false) }
    }

    // ── Step ordering ─────────────────────────────────────────────────────────

    private fun stepAfter(step: WizardStep): WizardStep? = when (step) {
        WizardStep.SelectCategory    -> WizardStep.SelectSymptoms
        WizardStep.SelectSymptoms    -> WizardStep.SetSeverity
        WizardStep.SetSeverity       -> WizardStep.ReviewAdjustments
        WizardStep.ReviewAdjustments -> WizardStep.Confirm
        WizardStep.Confirm           -> null
    }

    private fun stepBefore(step: WizardStep): WizardStep? = when (step) {
        WizardStep.SelectCategory    -> null
        WizardStep.SelectSymptoms    -> WizardStep.SelectCategory
        WizardStep.SetSeverity       -> WizardStep.SelectSymptoms
        WizardStep.ReviewAdjustments -> WizardStep.SetSeverity
        WizardStep.Confirm           -> WizardStep.ReviewAdjustments
    }
}
