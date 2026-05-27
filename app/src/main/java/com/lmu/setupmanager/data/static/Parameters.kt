package com.lmu.setupmanager.data.static

import com.lmu.setupmanager.domain.model.DataType
import com.lmu.setupmanager.domain.model.EnumOption
import com.lmu.setupmanager.domain.model.SetupParameter

// ─── Aerodynamics ────────────────────────────────────────────────────────────

private val aerodynamicsParameters = listOf(
    SetupParameter(
        key = "frontWing", category = "aerodynamics", label = "Front Wing",
        description = "Front wing angle. Higher values increase front downforce and drag.",
        unit = "°", dataType = DataType.INT, min = 0f, max = 15f, step = 1f,
        defaultValue = 7f, cornerSpecific = false
    ),
    SetupParameter(
        key = "rearWing", category = "aerodynamics", label = "Rear Wing",
        description = "Rear wing angle. Higher values increase rear downforce and drag.",
        unit = "°", dataType = DataType.INT, min = 0f, max = 15f, step = 1f,
        defaultValue = 8f, cornerSpecific = false
    ),
    SetupParameter(
        key = "brakeDuctFront", category = "aerodynamics", label = "Brake Duct Front",
        description = "Front brake duct opening size. Higher values cool brakes more but add drag.",
        unit = "", dataType = DataType.INT, min = 1f, max = 6f, step = 1f,
        defaultValue = 3f, cornerSpecific = false
    ),
    SetupParameter(
        key = "brakeDuctRear", category = "aerodynamics", label = "Brake Duct Rear",
        description = "Rear brake duct opening size. Higher values cool brakes more but add drag.",
        unit = "", dataType = DataType.INT, min = 1f, max = 6f, step = 1f,
        defaultValue = 3f, cornerSpecific = false
    ),
    SetupParameter(
        key = "rideHeightFront", category = "aerodynamics", label = "Ride Height Front",
        description = "Front ride height. Lower increases aero efficiency but risks bottoming out.",
        unit = "mm", dataType = DataType.FLOAT, min = 50f, max = 120f, step = 1f,
        defaultValue = 65f, cornerSpecific = false
    ),
    SetupParameter(
        key = "rideHeightRear", category = "aerodynamics", label = "Ride Height Rear",
        description = "Rear ride height. Affects aero balance and diffuser efficiency.",
        unit = "mm", dataType = DataType.FLOAT, min = 55f, max = 130f, step = 1f,
        defaultValue = 75f, cornerSpecific = false
    )
)

// ─── Suspension ───────────────────────────────────────────────────────────────

private val suspensionParameters = listOf(
    SetupParameter(
        key = "springRate", category = "suspension", label = "Spring Rate",
        description = "Spring stiffness. Stiffer improves response but worsens ride over bumps.",
        unit = "N/mm", dataType = DataType.FLOAT, min = 20f, max = 120f, step = 1f,
        defaultValue = 60f, cornerSpecific = true
    ),
    SetupParameter(
        key = "camber", category = "suspension", label = "Camber",
        description = "Wheel camber angle. Negative camber improves cornering grip.",
        unit = "°", dataType = DataType.FLOAT, min = -4.0f, max = 0.5f, step = 0.1f,
        defaultValue = -2.5f, cornerSpecific = true
    ),
    SetupParameter(
        key = "toe", category = "suspension", label = "Toe",
        description = "Wheel toe angle. Toe-in increases stability, toe-out increases turn-in.",
        unit = "°", dataType = DataType.FLOAT, min = -0.5f, max = 0.5f, step = 0.02f,
        defaultValue = 0.0f, cornerSpecific = true
    ),
    SetupParameter(
        key = "caster", category = "suspension", label = "Caster",
        description = "Caster angle of the front suspension. More caster improves straight-line stability.",
        unit = "°", dataType = DataType.FLOAT, min = 3.0f, max = 12.0f, step = 0.25f,
        defaultValue = 7.5f, cornerSpecific = false
    ),
    SetupParameter(
        key = "antiRollBarFront", category = "suspension", label = "ARB Front",
        description = "Front anti-roll bar stiffness. Stiffer reduces body roll but hurts traction.",
        unit = "", dataType = DataType.INT, min = 1f, max = 10f, step = 1f,
        defaultValue = 5f, cornerSpecific = false
    ),
    SetupParameter(
        key = "antiRollBarRear", category = "suspension", label = "ARB Rear",
        description = "Rear anti-roll bar stiffness. Stiffer reduces body roll but hurts rear traction.",
        unit = "", dataType = DataType.INT, min = 1f, max = 10f, step = 1f,
        defaultValue = 4f, cornerSpecific = false
    )
)

// ─── Dampers ──────────────────────────────────────────────────────────────────

private val dampersParameters = listOf(
    SetupParameter(
        key = "slowBump", category = "dampers", label = "Slow Bump",
        description = "Slow bump damping. Controls body movement during cornering and braking.",
        unit = "", dataType = DataType.INT, min = 1f, max = 20f, step = 1f,
        defaultValue = 8f, cornerSpecific = true
    ),
    SetupParameter(
        key = "fastBump", category = "dampers", label = "Fast Bump",
        description = "Fast bump damping. Controls wheel movement over sharp bumps.",
        unit = "", dataType = DataType.INT, min = 1f, max = 20f, step = 1f,
        defaultValue = 6f, cornerSpecific = true
    ),
    SetupParameter(
        key = "slowRebound", category = "dampers", label = "Slow Rebound",
        description = "Slow rebound damping. Controls how fast the suspension extends after compression.",
        unit = "", dataType = DataType.INT, min = 1f, max = 20f, step = 1f,
        defaultValue = 10f, cornerSpecific = true
    ),
    SetupParameter(
        key = "fastRebound", category = "dampers", label = "Fast Rebound",
        description = "Fast rebound damping. Controls wheel extension after sharp bump.",
        unit = "", dataType = DataType.INT, min = 1f, max = 20f, step = 1f,
        defaultValue = 8f, cornerSpecific = true
    )
)

// ─── Brakes ───────────────────────────────────────────────────────────────────

private val brakesParameters = listOf(
    SetupParameter(
        key = "brakeBalance", category = "brakes", label = "Brake Balance",
        description = "Front/rear brake balance percentage. Higher values bias toward the front.",
        unit = "%", dataType = DataType.FLOAT, min = 50f, max = 70f, step = 0.5f,
        defaultValue = 58f, cornerSpecific = false
    ),
    SetupParameter(
        key = "brakePressure", category = "brakes", label = "Brake Pressure",
        description = "Master cylinder pressure scaling. Higher values increase overall braking force.",
        unit = "%", dataType = DataType.INT, min = 70f, max = 100f, step = 1f,
        defaultValue = 90f, cornerSpecific = false
    ),
    SetupParameter(
        key = "padCompound", category = "brakes", label = "Pad Compound",
        description = "Brake pad compound. Harder pads last longer but need more heat to work.",
        unit = "", dataType = DataType.ENUM, min = 1f, max = 4f, step = 1f,
        enumOptions = listOf(
            EnumOption("Compound 1 (Soft)", 1),
            EnumOption("Compound 2 (Medium-Soft)", 2),
            EnumOption("Compound 3 (Medium-Hard)", 3),
            EnumOption("Compound 4 (Hard)", 4)
        ),
        defaultValue = 2f, cornerSpecific = false
    )
)

// ─── Tires ────────────────────────────────────────────────────────────────────

private val tiresParameters = listOf(
    SetupParameter(
        key = "tirePressure", category = "tires", label = "Tire Pressure",
        description = "Cold tire pressure. Higher pressure increases response but reduces contact patch.",
        unit = "PSI", dataType = DataType.FLOAT, min = 24.0f, max = 35.0f, step = 0.1f,
        defaultValue = 27.5f, cornerSpecific = true
    ),
    SetupParameter(
        key = "tireCompound", category = "tires", label = "Tire Compound",
        description = "Tire compound selection.",
        unit = "", dataType = DataType.ENUM, min = 1f, max = 3f, step = 1f,
        enumOptions = listOf(
            EnumOption("Dry (Slick)", 1),
            EnumOption("Intermediate", 2),
            EnumOption("Wet", 3)
        ),
        defaultValue = 1f, cornerSpecific = false
    )
)

// ─── Differential ────────────────────────────────────────────────────────────

private val differentialParameters = listOf(
    SetupParameter(
        key = "diffPreload", category = "differential", label = "Preload",
        description = "Differential preload torque. Higher values reduce wheelspin but increase understeer.",
        unit = "Nm", dataType = DataType.INT, min = 5f, max = 150f, step = 5f,
        defaultValue = 50f, cornerSpecific = false
    ),
    SetupParameter(
        key = "diffPowerRamp", category = "differential", label = "Power Ramp",
        description = "Locking under acceleration. Higher values reduce wheelspin on exit.",
        unit = "°", dataType = DataType.INT, min = 10f, max = 80f, step = 5f,
        defaultValue = 45f, cornerSpecific = false
    ),
    SetupParameter(
        key = "diffCoastRamp", category = "differential", label = "Coast Ramp",
        description = "Locking under deceleration. Higher values increase rear stability on entry.",
        unit = "°", dataType = DataType.INT, min = 5f, max = 60f, step = 5f,
        defaultValue = 30f, cornerSpecific = false
    )
)

// ─── Transmission ────────────────────────────────────────────────────────────

private fun gearParam(num: Int, min: Float, max: Float, def: Float): SetupParameter {
    val suffix = when (num) { 1 -> "st"; 2 -> "nd"; 3 -> "rd"; else -> "th" }
    return SetupParameter(
        key = "gear$num", category = "transmission", label = "Gear $num",
        description = "${num}$suffix gear ratio. Lower = shorter gear, more acceleration; higher = longer, more top speed.",
        unit = "", dataType = DataType.FLOAT, min = min, max = max, step = 0.01f,
        defaultValue = def, cornerSpecific = false
    )
}

private val transmissionParameters = listOf(
    gearParam(1, 2.5f, 4.5f, 3.5f),
    gearParam(2, 1.8f, 3.2f, 2.5f),
    gearParam(3, 1.3f, 2.4f, 1.9f),
    gearParam(4, 1.0f, 1.9f, 1.5f),
    gearParam(5, 0.8f, 1.5f, 1.2f),
    gearParam(6, 0.65f, 1.2f, 0.95f),
    gearParam(7, 0.55f, 1.0f, 0.78f),
    SetupParameter(
        key = "finalDrive", category = "transmission", label = "Final Drive",
        description = "Final drive ratio. Multiplies all gear ratios. Lower = longer gearing overall.",
        unit = "", dataType = DataType.FLOAT, min = 2.5f, max = 5.0f, step = 0.05f,
        defaultValue = 3.7f, cornerSpecific = false
    )
)

// ─── Electronics ─────────────────────────────────────────────────────────────

private val electronicsParameters = listOf(
    SetupParameter(
        key = "tractionControl", category = "electronics", label = "Traction Control",
        description = "TC intervention level. 0 = off, higher = more intervention.",
        unit = "", dataType = DataType.INT, min = 0f, max = 10f, step = 1f,
        defaultValue = 4f, cornerSpecific = false
    ),
    SetupParameter(
        key = "abs", category = "electronics", label = "ABS",
        description = "ABS intervention level. 0 = off, higher = more intervention.",
        unit = "", dataType = DataType.INT, min = 0f, max = 10f, step = 1f,
        defaultValue = 3f, cornerSpecific = false
    ),
    SetupParameter(
        key = "engineMap", category = "electronics", label = "Engine Map",
        description = "Engine power/fuel map. Lower = more power/fuel consumption; higher = fuel saving.",
        unit = "", dataType = DataType.INT, min = 1f, max = 6f, step = 1f,
        defaultValue = 1f, cornerSpecific = false
    )
)

// ─── Fuel ─────────────────────────────────────────────────────────────────────

private val fuelParameters = listOf(
    SetupParameter(
        key = "fuelLoad", category = "fuel", label = "Fuel Load",
        description = "Fuel load in liters. More fuel = heavier car but fewer pit stops.",
        unit = "L", dataType = DataType.FLOAT, min = 5f, max = 120f, step = 1f,
        defaultValue = 60f, cornerSpecific = false
    )
)

// ─── Public API ───────────────────────────────────────────────────────────────

val allParameters: List<SetupParameter> = aerodynamicsParameters +
        suspensionParameters +
        dampersParameters +
        brakesParameters +
        tiresParameters +
        differentialParameters +
        transmissionParameters +
        electronicsParameters +
        fuelParameters

val parametersByKey: Map<String, SetupParameter> = allParameters.associateBy { it.key }

val parametersByCategory: Map<String, List<SetupParameter>> =
    allParameters.groupBy { it.category }

val categoryOrder = listOf(
    "aerodynamics", "suspension", "dampers", "brakes",
    "tires", "differential", "transmission", "electronics", "fuel"
)

val categoryLabels = mapOf(
    "aerodynamics" to "Aerodynamics",
    "suspension" to "Suspension",
    "dampers" to "Dampers",
    "brakes" to "Brakes",
    "tires" to "Tires",
    "differential" to "Differential",
    "transmission" to "Transmission",
    "electronics" to "Electronics",
    "fuel" to "Fuel"
)
