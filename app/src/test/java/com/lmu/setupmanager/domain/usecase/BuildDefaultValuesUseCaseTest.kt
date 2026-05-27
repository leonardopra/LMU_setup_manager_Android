package com.lmu.setupmanager.domain.usecase

import com.lmu.setupmanager.data.static.allParameters
import com.lmu.setupmanager.data.static.carById
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BuildDefaultValuesUseCaseTest {

    private lateinit var useCase: BuildDefaultValuesUseCase

    @Before
    fun setUp() {
        useCase = BuildDefaultValuesUseCase()
    }

    // ── Corner expansion ───────────────────────────────────────────────────────

    @Test
    fun `corner-specific params expand to four keys`() {
        val result = useCase("bmw-m4-gt3")
        val cornerParams = allParameters.filter { it.cornerSpecific }

        for (param in cornerParams) {
            for (corner in listOf("FL", "FR", "RL", "RR")) {
                val key = "${param.key}_$corner"
                assertNotNull("Expected key $key in result", result[key])
            }
            // Base key must NOT be present
            assertTrue("Base key ${param.key} should not be in result", !result.containsKey(param.key))
        }
    }

    @Test
    fun `non-corner params produce a single key`() {
        val result = useCase("bmw-m4-gt3")
        val nonCornerParams = allParameters.filter { !it.cornerSpecific }

        for (param in nonCornerParams) {
            assertNotNull("Expected key ${param.key} in result", result[param.key])
        }
    }

    // ── Default values ─────────────────────────────────────────────────────────

    @Test
    fun `default values match parameter defaults for generic car`() {
        // Use a car id that has no overrides — Porsche has caster and rearWing overrides only
        val result = useCase("ferrari-296-gt3")

        val frontWing = allParameters.first { it.key == "frontWing" }
        assertEquals(frontWing.defaultValue, result["frontWing"])

        val tirePressure = allParameters.first { it.key == "tirePressure" }
        assertEquals(tirePressure.defaultValue, result["tirePressure_FL"])
        assertEquals(tirePressure.defaultValue, result["tirePressure_RR"])
    }

    // ── Car overrides ──────────────────────────────────────────────────────────

    @Test
    fun `BMW override applies min and max but not defaultValue`() {
        val result = useCase("bmw-m4-gt3")
        // BMW has frontWing override { min=0, max=12 } but no defaultValue override
        val frontWing = allParameters.first { it.key == "frontWing" }
        // Default value should still be the base default (7)
        assertEquals(frontWing.defaultValue, result["frontWing"])
    }

    @Test
    fun `Mercedes override applies custom defaultValue for rideHeightFront`() {
        val result = useCase("mercedes-amg-gt3-evo")
        val mercedesOverride = carById["mercedes-amg-gt3-evo"]!!
            .parameterOverrides["rideHeightFront"]!!
        val expectedDefault = mercedesOverride.defaultValue!!
        assertEquals(expectedDefault, result["rideHeightFront"])
    }

    @Test
    fun `Porsche override applies custom defaultValue for caster`() {
        val result = useCase("porsche-911-gt3-r")
        assertEquals(8.0f, result["caster"])
    }

    @Test
    fun `Ford override applies defaultValue for antiRollBarFront`() {
        val result = useCase("ford-mustang-gt3")
        assertEquals(6f, result["antiRollBarFront"])
        assertEquals(5f, result["antiRollBarRear"])
    }

    // ── Unknown car ────────────────────────────────────────────────────────────

    @Test
    fun `unknown carId falls back to base defaults`() {
        val result = useCase("unknown-car-xyz")
        val frontWing = allParameters.first { it.key == "frontWing" }
        assertEquals(frontWing.defaultValue, result["frontWing"])
    }

    // ── Completeness ───────────────────────────────────────────────────────────

    @Test
    fun `result contains all expected keys`() {
        val result = useCase("bmw-m4-gt3")
        val expectedCount = allParameters.sumOf { if (it.cornerSpecific) 4 else 1 }
        assertEquals(expectedCount, result.size)
    }
}
