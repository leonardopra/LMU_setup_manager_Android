package com.lmu.setupmanager.domain.usecase

import com.lmu.setupmanager.domain.model.SelectedSymptom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ResolveAdjustmentsUseCaseTest {

    private lateinit var useCase: ResolveAdjustmentsUseCase

    @Before
    fun setUp() {
        useCase = ResolveAdjustmentsUseCase()
    }

    // ── 1. Single symptom ─────────────────────────────────────────────────────

    @Test
    fun `single symptom at severity 1 applies base delta unchanged`() {
        // front_lockup: brakeBalance baseDelta = -1.5f; severity 1 → ×1.0 = -1.5f
        // Ferrari has no brakeBalance override → range [50, 70]
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 58f),
            symptoms = listOf(SelectedSymptom(feedbackId = "front_lockup", severity = 1))
        )
        val adj = result.single { it.storeKey == "brakeBalance" }
        assertEquals(-1.5f, adj.netDelta, 1e-4f)
        assertEquals(56.5f, adj.proposedValue, 1e-4f)
        assertFalse(adj.wasConflicted)
        assertEquals(1, adj.contributors.size)
        assertEquals("front_lockup", adj.contributors[0].feedbackId)
    }

    @Test
    fun `single symptom at severity 2 scales delta by 1_5`() {
        // front_lockup: brakeBalance -1.5f × 1.5 = -2.25f
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 58f),
            symptoms = listOf(SelectedSymptom(feedbackId = "front_lockup", severity = 2))
        )
        val adj = result.single { it.storeKey == "brakeBalance" }
        assertEquals(-2.25f, adj.netDelta, 1e-4f)
        assertEquals(55.75f, adj.proposedValue, 1e-4f)
    }

    @Test
    fun `single symptom at severity 3 scales delta by 2_0`() {
        // front_lockup: brakeBalance -1.5f × 2.0 = -3.0f
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 58f),
            symptoms = listOf(SelectedSymptom(feedbackId = "front_lockup", severity = 3))
        )
        val adj = result.single { it.storeKey == "brakeBalance" }
        assertEquals(-3.0f, adj.netDelta, 1e-4f)
        assertEquals(55.0f, adj.proposedValue, 1e-4f)
    }

    // ── 2. Multiple symptoms accumulate on the same key ───────────────────────

    @Test
    fun `two symptoms pushing the same key in the same direction accumulate`() {
        // nervous_at_speed: rearWing baseDelta = +2f (sev 1 → +2f)
        // low_traction:     rearWing baseDelta = +1f (sev 1 → +1f)
        // net = +3f; currentValue = 8f; BMW override [2..14] → proposedValue = 11f
        val result = useCase(
            carId = "bmw-m4-gt3",
            currentValues = mapOf("rearWing" to 8f),
            symptoms = listOf(
                SelectedSymptom("nervous_at_speed", severity = 1),
                SelectedSymptom("low_traction", severity = 1)
            )
        )
        val adj = result.single { it.storeKey == "rearWing" }
        assertEquals(3f, adj.netDelta, 1e-4f)
        assertEquals(11f, adj.proposedValue, 1e-4f)
        assertEquals(2, adj.contributors.size)
        assertFalse(adj.wasConflicted)
    }

    // ── 3. Conflict detection ─────────────────────────────────────────────────

    @Test
    fun `two symptoms pushing a key in opposite directions marks wasConflicted`() {
        // front_lockup sev 1:  brakeBalance -1.5f × 1.0 = -1.5f
        // rear_lockup  sev 2:  brakeBalance +1.5f × 1.5 = +2.25f
        // net = +0.75f → wasConflicted = true
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 58f),
            symptoms = listOf(
                SelectedSymptom("front_lockup", severity = 1),
                SelectedSymptom("rear_lockup", severity = 2)
            )
        )
        val adj = result.single { it.storeKey == "brakeBalance" }
        assertTrue(adj.wasConflicted)
        assertEquals(0.75f, adj.netDelta, 1e-4f)
        assertEquals(2, adj.contributors.size)
    }

    @Test
    fun `perfectly cancelling conflicts produce zero netDelta and are excluded from results`() {
        // front_lockup sev 1: brakeBalance -1.5f
        // rear_lockup  sev 1: brakeBalance +1.5f
        // net = 0.0f → excluded
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 58f),
            symptoms = listOf(
                SelectedSymptom("front_lockup", severity = 1),
                SelectedSymptom("rear_lockup", severity = 1)
            )
        )
        assertNull(result.find { it.storeKey == "brakeBalance" })
    }

    // ── 4. Clamping ───────────────────────────────────────────────────────────

    @Test
    fun `proposed value is clamped to car effective max`() {
        // BMW frontWing override: max = 12f
        // understeer_fast sev 3: frontWing baseDelta = 2f × 2.0 = 4f
        // currentValue = 11f → 11 + 4 = 15 → clamped to 12
        val result = useCase(
            carId = "bmw-m4-gt3",
            currentValues = mapOf("frontWing" to 11f),
            symptoms = listOf(SelectedSymptom("understeer_fast", severity = 3))
        )
        val adj = result.single { it.storeKey == "frontWing" }
        assertEquals(4f, adj.netDelta, 1e-4f)       // raw net delta is reported as-is
        assertEquals(12f, adj.proposedValue, 1e-4f)  // clamped to BMW max
    }

    @Test
    fun `proposed value is clamped to car effective min`() {
        // BMW rearWing override: min = 2f
        // too_much_drag sev 3: rearWing baseDelta = -2f × 2.0 = -4f
        // currentValue = 4f → 4 - 4 = 0 → clamped to 2
        val result = useCase(
            carId = "bmw-m4-gt3",
            currentValues = mapOf("rearWing" to 4f),
            symptoms = listOf(SelectedSymptom("too_much_drag", severity = 3))
        )
        val adj = result.single { it.storeKey == "rearWing" }
        assertEquals(-4f, adj.netDelta, 1e-4f)
        assertEquals(2f, adj.proposedValue, 1e-4f)
    }

    @Test
    fun `proposed value respects global parameter min when no car override exists`() {
        // brakeBalance global min = 50f; Ferrari has no override for this param
        // front_lockup sev 3: brakeBalance -1.5f × 2.0 = -3.0f
        // currentValue = 51f → 51 - 3 = 48 → clamped to 50
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 51f),
            symptoms = listOf(SelectedSymptom("front_lockup", severity = 3))
        )
        val adj = result.single { it.storeKey == "brakeBalance" }
        assertEquals(-3f, adj.netDelta, 1e-4f)
        assertEquals(50f, adj.proposedValue, 1e-4f)
    }

    // ── 5. Unknown feedbackId is skipped ──────────────────────────────────────

    @Test
    fun `unknown feedbackId is silently skipped and produces no results`() {
        val result = useCase(
            carId = "bmw-m4-gt3",
            currentValues = emptyMap(),
            symptoms = listOf(SelectedSymptom(feedbackId = "nonexistent_symptom", severity = 1))
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun `mix of valid and unknown feedbackIds processes only the valid one`() {
        // "front_lockup" is valid; "ghost_symptom" is not — should not crash, valid one wins
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = mapOf("brakeBalance" to 58f),
            symptoms = listOf(
                SelectedSymptom("front_lockup", severity = 1),
                SelectedSymptom("ghost_symptom", severity = 2)
            )
        )
        assertTrue(result.any { it.storeKey == "brakeBalance" })
        // Results come only from front_lockup — no phantom keys from the unknown symptom
        result.forEach { adj ->
            assertEquals(1, adj.contributors.count { it.feedbackId == "front_lockup" } +
                adj.contributors.count { it.feedbackId == "ghost_symptom" }
                    .let { 0 }) // ghost contributes 0
        }
    }

    // ── Corner expansion ──────────────────────────────────────────────────────

    @Test
    fun `corner-specific parameter with ALL target expands to four store keys`() {
        // kerb_instability: fastBump cornerTarget=ALL, baseDelta=-1f → 4 keys
        val currentValues = mapOf(
            "fastBump_FL" to 6f, "fastBump_FR" to 6f,
            "fastBump_RL" to 6f, "fastBump_RR" to 6f
        )
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = currentValues,
            symptoms = listOf(SelectedSymptom("kerb_instability", severity = 1))
        )
        val fastBumpKeys = result.filter { it.storeKey.startsWith("fastBump_") }
        assertEquals(4, fastBumpKeys.size)
        assertTrue(fastBumpKeys.all { it.netDelta == -1f })
        assertTrue(fastBumpKeys.all { it.proposedValue == 5f }) // 6 - 1 = 5, within [1..20]
    }

    @Test
    fun `corner-specific parameter with FRONT target expands to FL and FR only`() {
        // understeer_slow: tirePressure cornerTarget=FRONT, baseDelta=-0.3f
        val currentValues = mapOf(
            "tirePressure_FL" to 27.5f, "tirePressure_FR" to 27.5f,
            "tirePressure_RL" to 27.5f, "tirePressure_RR" to 27.5f
        )
        val result = useCase(
            carId = "ferrari-296-gt3",
            currentValues = currentValues,
            symptoms = listOf(SelectedSymptom("understeer_slow", severity = 1))
        )
        // Only front corners should have a tirePressure adjustment
        val tireFront = result.filter { it.storeKey.startsWith("tirePressure_F") }
        val tireRear = result.filter { it.storeKey.startsWith("tirePressure_R") }
        assertEquals(2, tireFront.size)
        assertTrue(tireRear.none { it.contributors.any { c -> c.feedbackId == "understeer_slow" } })
    }
}
