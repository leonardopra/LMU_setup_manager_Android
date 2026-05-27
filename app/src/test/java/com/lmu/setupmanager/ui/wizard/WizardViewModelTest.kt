package com.lmu.setupmanager.ui.wizard

import androidx.lifecycle.SavedStateHandle
import com.lmu.setupmanager.domain.model.FeedbackCategory
import com.lmu.setupmanager.domain.model.SelectedSymptom
import com.lmu.setupmanager.domain.usecase.ResolveAdjustmentsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WizardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Instantiate with a real use case (pure, no I/O, uses static data)
    private fun buildViewModel(
        carId: String = "bmw-m4-gt3",
        currentValuesJson: String = "{}"
    ) = WizardViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf("carId" to carId, "currentValues" to currentValuesJson)
        ),
        resolveAdjustments = ResolveAdjustmentsUseCase()
    )

    private lateinit var viewModel: WizardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = buildViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── 1. Initial state ──────────────────────────────────────────────────────

    @Test
    fun `initial state has step SelectCategory`() {
        assertEquals(WizardStep.SelectCategory, viewModel.uiState.value.step)
    }

    @Test
    fun `initial state has no selectedCategory`() {
        assertNull(viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `initial state has empty selectedSymptoms`() {
        assertTrue(viewModel.uiState.value.selectedSymptoms.isEmpty())
    }

    @Test
    fun `initial state has empty resolvedAdjustments`() {
        assertTrue(viewModel.uiState.value.resolvedAdjustments.isEmpty())
    }

    @Test
    fun `initial state has isApplied false`() {
        assertFalse(viewModel.uiState.value.isApplied)
    }

    // ── 2. selectCategory ─────────────────────────────────────────────────────

    @Test
    fun `selectCategory moves step to SelectSymptoms`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)

        assertEquals(WizardStep.SelectSymptoms, viewModel.uiState.value.step)
    }

    @Test
    fun `selectCategory sets selectedCategory`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)

        assertEquals(FeedbackCategory.BALANCE, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `selectCategory clears any previously selected symptoms`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        assertEquals(1, viewModel.uiState.value.selectedSymptoms.size)

        viewModel.selectCategory(FeedbackCategory.STABILITY) // switch category

        assertTrue(viewModel.uiState.value.selectedSymptoms.isEmpty())
    }

    @Test
    fun `selecting a different category overwrites selectedCategory`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.selectCategory(FeedbackCategory.AERO)

        assertEquals(FeedbackCategory.AERO, viewModel.uiState.value.selectedCategory)
    }

    // ── 3. toggleSymptom ──────────────────────────────────────────────────────

    @Test
    fun `toggleSymptom adds SelectedSymptom with severity 1`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")

        val symptoms = viewModel.uiState.value.selectedSymptoms
        assertEquals(1, symptoms.size)
        assertEquals(SelectedSymptom(feedbackId = "understeer_slow", severity = 1), symptoms[0])
    }

    @Test
    fun `second toggleSymptom on the same id removes it`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        viewModel.toggleSymptom("understeer_slow")

        assertTrue(viewModel.uiState.value.selectedSymptoms.isEmpty())
    }

    @Test
    fun `toggleSymptom preserves other selected symptoms when removing one`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        viewModel.toggleSymptom("understeer_mid")

        viewModel.toggleSymptom("understeer_slow") // remove first

        val symptoms = viewModel.uiState.value.selectedSymptoms
        assertEquals(1, symptoms.size)
        assertEquals("understeer_mid", symptoms[0].feedbackId)
    }

    @Test
    fun `multiple distinct symptoms accumulate in selectedSymptoms`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        viewModel.toggleSymptom("understeer_mid")
        viewModel.toggleSymptom("oversteer_fast")

        assertEquals(3, viewModel.uiState.value.selectedSymptoms.size)
    }

    // ── 4. setSeverity ────────────────────────────────────────────────────────

    @Test
    fun `setSeverity updates severity for the targeted feedbackId`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")   // severity = 1 by default

        viewModel.setSeverity("understeer_slow", 3)

        val symptom = viewModel.uiState.value.selectedSymptoms.first()
        assertEquals(3, symptom.severity)
    }

    @Test
    fun `setSeverity does not affect other selected symptoms`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        viewModel.toggleSymptom("understeer_mid")

        viewModel.setSeverity("understeer_slow", 2)

        val state = viewModel.uiState.value
        assertEquals(2, state.selectedSymptoms.first { it.feedbackId == "understeer_slow" }.severity)
        assertEquals(1, state.selectedSymptoms.first { it.feedbackId == "understeer_mid" }.severity)
    }

    @Test
    fun `setSeverity on an unknown feedbackId is a no-op`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")

        viewModel.setSeverity("does_not_exist", 3)

        val symptom = viewModel.uiState.value.selectedSymptoms.first()
        assertEquals(1, symptom.severity) // unchanged
    }

    // ── 5. next() — no symptoms guard ────────────────────────────────────────

    @Test
    fun `next() from SelectSymptoms with no symptoms selected does not advance`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE) // → SelectSymptoms, no symptoms

        viewModel.next()

        assertEquals(WizardStep.SelectSymptoms, viewModel.uiState.value.step)
    }

    @Test
    fun `next() from SelectSymptoms with at least one symptom advances to SetSeverity`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")

        viewModel.next()

        assertEquals(WizardStep.SetSeverity, viewModel.uiState.value.step)
    }

    // ── 6. next() from SetSeverity triggers resolveAdjustments ────────────────

    @Test
    fun `next() from SetSeverity advances to ReviewAdjustments`() {
        navigateToSetSeverity()

        viewModel.next()

        assertEquals(WizardStep.ReviewAdjustments, viewModel.uiState.value.step)
    }

    @Test
    fun `next() from SetSeverity populates resolvedAdjustments via use case`() {
        navigateToSetSeverity()

        viewModel.next() // SetSeverity → ReviewAdjustments

        assertTrue(
            "resolvedAdjustments must be non-empty after resolving 'understeer_slow'",
            viewModel.uiState.value.resolvedAdjustments.isNotEmpty()
        )
    }

    @Test
    fun `resolvedAdjustments respects severity scale — severity 3 produces larger deltas than severity 1`() {
        // Same symptom, different severity — sev 3 deltas must be larger in magnitude
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        viewModel.setSeverity("understeer_slow", 1)
        viewModel.next() // → SetSeverity
        viewModel.next() // → ReviewAdjustments
        val deltaSev1 = viewModel.uiState.value.resolvedAdjustments
            .maxOf { Math.abs(it.netDelta) }

        // Reset and repeat at severity 3
        val vm3 = buildViewModel()
        vm3.selectCategory(FeedbackCategory.BALANCE)
        vm3.toggleSymptom("understeer_slow")
        vm3.setSeverity("understeer_slow", 3)
        vm3.next()
        vm3.next()
        val deltaSev3 = vm3.uiState.value.resolvedAdjustments
            .maxOf { Math.abs(it.netDelta) }

        assertTrue("sev 3 deltas ($deltaSev3) must be larger than sev 1 ($deltaSev1)", deltaSev3 > deltaSev1)
    }

    @Test
    fun `next() from ReviewAdjustments advances to Confirm without re-running use case`() {
        navigateToReviewAdjustments()
        val snapshotBefore = viewModel.uiState.value.resolvedAdjustments.toList()

        viewModel.next() // ReviewAdjustments → Confirm

        val state = viewModel.uiState.value
        assertEquals(WizardStep.Confirm, state.step)
        assertEquals(snapshotBefore, state.resolvedAdjustments)
    }

    @Test
    fun `next() from Confirm is a no-op`() {
        navigateToReviewAdjustments()
        viewModel.next() // → Confirm

        viewModel.next() // should stay at Confirm

        assertEquals(WizardStep.Confirm, viewModel.uiState.value.step)
    }

    // ── 7. confirm() ─────────────────────────────────────────────────────────

    @Test
    fun `confirm() sets isApplied to true`() {
        viewModel.confirm()

        assertTrue(viewModel.uiState.value.isApplied)
    }

    @Test
    fun `consumeApplied() resets isApplied to false`() {
        viewModel.confirm()
        viewModel.consumeApplied()

        assertFalse(viewModel.uiState.value.isApplied)
    }

    @Test
    fun `confirm() does not change the current step`() {
        navigateToReviewAdjustments()
        viewModel.next() // → Confirm
        viewModel.confirm()

        assertEquals(WizardStep.Confirm, viewModel.uiState.value.step)
    }

    // ── 8. back() ─────────────────────────────────────────────────────────────

    @Test
    fun `back() from SelectSymptoms returns to SelectCategory`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)

        viewModel.back()

        assertEquals(WizardStep.SelectCategory, viewModel.uiState.value.step)
    }

    @Test
    fun `back() from SelectSymptoms clears selectedCategory`() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)

        viewModel.back()

        assertNull(viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `back() from SelectCategory is a no-op`() {
        viewModel.back()

        assertEquals(WizardStep.SelectCategory, viewModel.uiState.value.step)
    }

    @Test
    fun `back() from SetSeverity returns to SelectSymptoms`() {
        navigateToSetSeverity()

        viewModel.back()

        assertEquals(WizardStep.SelectSymptoms, viewModel.uiState.value.step)
    }

    @Test
    fun `back() from SetSeverity preserves selectedCategory`() {
        viewModel.selectCategory(FeedbackCategory.BRAKING)
        viewModel.toggleSymptom("front_lockup")
        viewModel.next() // → SetSeverity

        viewModel.back()

        assertEquals(FeedbackCategory.BRAKING, viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun `back() from ReviewAdjustments returns to SetSeverity`() {
        navigateToReviewAdjustments()

        viewModel.back()

        assertEquals(WizardStep.SetSeverity, viewModel.uiState.value.step)
    }

    @Test
    fun `back() from Confirm returns to ReviewAdjustments`() {
        navigateToReviewAdjustments()
        viewModel.next() // → Confirm

        viewModel.back()

        assertEquals(WizardStep.ReviewAdjustments, viewModel.uiState.value.step)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Moves to SetSeverity with one BALANCE symptom selected. */
    private fun navigateToSetSeverity() {
        viewModel.selectCategory(FeedbackCategory.BALANCE)
        viewModel.toggleSymptom("understeer_slow")
        viewModel.next() // SelectSymptoms → SetSeverity
    }

    /** Moves to ReviewAdjustments via SelectCategory → SelectSymptoms → SetSeverity. */
    private fun navigateToReviewAdjustments() {
        navigateToSetSeverity()
        viewModel.next() // SetSeverity → ReviewAdjustments
    }
}
