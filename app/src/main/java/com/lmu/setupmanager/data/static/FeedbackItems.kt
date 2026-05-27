package com.lmu.setupmanager.data.static

import com.lmu.setupmanager.domain.model.CornerTarget
import com.lmu.setupmanager.domain.model.FeedbackCategory
import com.lmu.setupmanager.domain.model.FeedbackItem
import com.lmu.setupmanager.domain.model.ParameterDelta

val feedbackItems: List<FeedbackItem> = listOf(

    // ── BALANCE ──────────────────────────────────────────────────────────────

    FeedbackItem(
        id = "understeer_slow",
        category = FeedbackCategory.BALANCE,
        label = "Understeer in slow corners",
        description = "Front pushes wide on entry or mid-corner in tight, low-speed bends.",
        deltas = listOf(
            ParameterDelta("frontWing", baseDelta = 1f, rationale = "More front wing adds front downforce, increasing front grip"),
            ParameterDelta("antiRollBarFront", baseDelta = -1f, rationale = "Softer front ARB allows more front roll, improving contact patch"),
            ParameterDelta("antiRollBarRear", baseDelta = 1f, rationale = "Stiffer rear ARB transfers more load to rear, loosening the car"),
            ParameterDelta("diffPreload", baseDelta = -10f, rationale = "Less diff preload reduces understeer on corner entry"),
            ParameterDelta("tirePressure", cornerTarget = CornerTarget.FRONT, baseDelta = -0.3f, rationale = "Lower front pressure widens contact patch")
        )
    ),
    FeedbackItem(
        id = "understeer_mid",
        category = FeedbackCategory.BALANCE,
        label = "Understeer in mid-speed corners",
        description = "Front washes out through medium-radius sweepers (80–150 kph).",
        deltas = listOf(
            ParameterDelta("frontWing", baseDelta = 1f, rationale = "More front downforce at medium speeds"),
            ParameterDelta("camber", cornerTarget = CornerTarget.FRONT, baseDelta = -0.2f, rationale = "More negative camber improves front cornering grip"),
            ParameterDelta("antiRollBarFront", baseDelta = -1f, rationale = "Softer front ARB lets the front load up more evenly"),
            ParameterDelta("caster", baseDelta = 0.5f, rationale = "More caster increases front camber gain in corners"),
            ParameterDelta("rideHeightFront", baseDelta = -2f, rationale = "Lower front ride height increases front downforce")
        )
    ),
    FeedbackItem(
        id = "understeer_fast",
        category = FeedbackCategory.BALANCE,
        label = "Understeer in fast corners",
        description = "Car pushes wide at high speed through long, sweeping bends.",
        deltas = listOf(
            ParameterDelta("frontWing", baseDelta = 2f, rationale = "Significant front downforce gain critical at high speed"),
            ParameterDelta("rearWing", baseDelta = -1f, rationale = "Reducing rear downforce shifts aero balance forward"),
            ParameterDelta("rideHeightFront", baseDelta = -3f, rationale = "Lower front ride height improves front aero balance"),
            ParameterDelta("camber", cornerTarget = CornerTarget.FRONT, baseDelta = -0.2f, rationale = "More negative camber on front at high-speed loading"),
            ParameterDelta("toe", cornerTarget = CornerTarget.FRONT, baseDelta = -0.02f, rationale = "Slight toe-out sharpens turn-in response")
        )
    ),
    FeedbackItem(
        id = "oversteer_slow",
        category = FeedbackCategory.BALANCE,
        label = "Oversteer in slow corners",
        description = "Rear steps out on corner entry or mid-corner at low speed.",
        deltas = listOf(
            ParameterDelta("rearWing", baseDelta = 1f, rationale = "More rear downforce stabilises the rear"),
            ParameterDelta("antiRollBarRear", baseDelta = -1f, rationale = "Softer rear ARB reduces rear load transfer"),
            ParameterDelta("antiRollBarFront", baseDelta = 1f, rationale = "Stiffer front ARB adds front load transfer to balance"),
            ParameterDelta("diffCoastRamp", baseDelta = 5f, rationale = "More coast locking stabilises rear on entry under trail braking"),
            ParameterDelta("tirePressure", cornerTarget = CornerTarget.REAR, baseDelta = -0.3f, rationale = "Lower rear pressure increases rear contact patch")
        )
    ),
    FeedbackItem(
        id = "oversteer_fast",
        category = FeedbackCategory.BALANCE,
        label = "Oversteer in fast corners",
        description = "Car becomes unstable or rear slides in high-speed sweepers.",
        deltas = listOf(
            ParameterDelta("rearWing", baseDelta = 2f, rationale = "Large rear downforce increase needed for high-speed stability"),
            ParameterDelta("rideHeightRear", baseDelta = -3f, rationale = "Lower rear ride height increases rear diffuser efficiency"),
            ParameterDelta("toe", cornerTarget = CornerTarget.REAR, baseDelta = 0.04f, rationale = "Toe-in stabilises rear at speed"),
            ParameterDelta("springRate", cornerTarget = CornerTarget.REAR, baseDelta = 5f, rationale = "Stiffer rear spring reduces rear squat and instability")
        )
    ),
    FeedbackItem(
        id = "snap_oversteer",
        category = FeedbackCategory.BALANCE,
        label = "Snap oversteer on entry",
        description = "Sudden rear breakaway on corner entry, especially under trail braking.",
        deltas = listOf(
            ParameterDelta("rearWing", baseDelta = 1f, rationale = "More rear downforce to prevent snap"),
            ParameterDelta("diffCoastRamp", baseDelta = -10f, rationale = "Less coast locking lets rear rotate more progressively"),
            ParameterDelta("antiRollBarRear", baseDelta = -1f, rationale = "Softer rear ARB reduces snap tendency"),
            ParameterDelta("slowRebound", cornerTarget = CornerTarget.REAR, baseDelta = -2f, rationale = "Slower rear rebound means less abrupt weight transfer"),
            ParameterDelta("brakeBalance", baseDelta = -1f, rationale = "Slightly less front bias reduces rear loading under braking")
        )
    ),
    FeedbackItem(
        id = "exit_oversteer",
        category = FeedbackCategory.BALANCE,
        label = "Exit oversteer (power oversteer)",
        description = "Rear slides when applying throttle on corner exit.",
        deltas = listOf(
            ParameterDelta("diffPowerRamp", baseDelta = 5f, rationale = "More power ramp locks diff on acceleration, reducing spin-induced oversteer"),
            ParameterDelta("diffPreload", baseDelta = 10f, rationale = "More preload provides baseline locking to prevent exit rotation"),
            ParameterDelta("tractionControl", baseDelta = 1f, rationale = "Higher TC reduces wheel torque on wheelspin events"),
            ParameterDelta("rearWing", baseDelta = 1f, rationale = "More rear downforce stabilises rear under acceleration")
        )
    ),

    // ── STABILITY ────────────────────────────────────────────────────────────

    FeedbackItem(
        id = "straight_instability",
        category = FeedbackCategory.STABILITY,
        label = "Straight-line instability",
        description = "Car wanders or darts, needs constant steering corrections on the straight.",
        deltas = listOf(
            ParameterDelta("caster", baseDelta = 0.5f, rationale = "More caster increases self-centring and straight-line tracking"),
            ParameterDelta("toe", cornerTarget = CornerTarget.REAR, baseDelta = 0.04f, rationale = "More rear toe-in stabilises the car in a straight line"),
            ParameterDelta("toe", cornerTarget = CornerTarget.FRONT, baseDelta = 0.02f, rationale = "Slight front toe-in reduces wandering")
        )
    ),
    FeedbackItem(
        id = "kerb_instability",
        category = FeedbackCategory.STABILITY,
        label = "Kerb instability",
        description = "Car becomes unsettled or jumps when riding kerbs.",
        deltas = listOf(
            ParameterDelta("fastBump", cornerTarget = CornerTarget.ALL, baseDelta = -1f, rationale = "Softer fast bump absorbs sharp kerb inputs"),
            ParameterDelta("fastRebound", cornerTarget = CornerTarget.ALL, baseDelta = -1f, rationale = "Softer fast rebound reduces wheel skip after kerb"),
            ParameterDelta("springRate", cornerTarget = CornerTarget.ALL, baseDelta = -3f, rationale = "Slightly softer spring reduces kerb impact transmitted to chassis"),
            ParameterDelta("rideHeightFront", baseDelta = 3f, rationale = "Higher ride height reduces risk of bottoming on high kerbs")
        )
    ),
    FeedbackItem(
        id = "bumpy_ride",
        category = FeedbackCategory.STABILITY,
        label = "Car bounces / bumpy ride",
        description = "Car porpoises or bounces aggressively over rough surfaces.",
        deltas = listOf(
            ParameterDelta("slowBump", cornerTarget = CornerTarget.ALL, baseDelta = -1f, rationale = "Softer slow bump reduces body movement over rough patches"),
            ParameterDelta("fastBump", cornerTarget = CornerTarget.ALL, baseDelta = -2f, rationale = "Softer fast bump absorbs high-frequency road inputs"),
            ParameterDelta("springRate", cornerTarget = CornerTarget.ALL, baseDelta = -5f, rationale = "Softer springs allow more suspension travel over bumps"),
            ParameterDelta("tirePressure", cornerTarget = CornerTarget.ALL, baseDelta = -0.3f, rationale = "Lower tire pressure provides additional compliance")
        )
    ),
    FeedbackItem(
        id = "corner_entry_instability",
        category = FeedbackCategory.STABILITY,
        label = "Instability on corner entry",
        description = "Car feels loose or darty when first turning in before braking is complete.",
        deltas = listOf(
            ParameterDelta("diffCoastRamp", baseDelta = 5f, rationale = "More coast locking helps stabilise rear during turn-in"),
            ParameterDelta("slowBump", cornerTarget = CornerTarget.REAR, baseDelta = 1f, rationale = "Stiffer rear slow bump resists rapid weight transfer"),
            ParameterDelta("toe", cornerTarget = CornerTarget.REAR, baseDelta = 0.04f, rationale = "Rear toe-in increases directional stability on entry"),
            ParameterDelta("caster", baseDelta = 0.25f, rationale = "More caster improves front stability during initial turn-in")
        )
    ),
    FeedbackItem(
        id = "bottoming_out",
        category = FeedbackCategory.STABILITY,
        label = "Car bottoming out",
        description = "Sounds or feels of the floor hitting road, especially over crests or compressions.",
        deltas = listOf(
            ParameterDelta("rideHeightFront", baseDelta = 5f, rationale = "More front ride height provides clearance"),
            ParameterDelta("rideHeightRear", baseDelta = 5f, rationale = "More rear ride height provides clearance"),
            ParameterDelta("springRate", cornerTarget = CornerTarget.ALL, baseDelta = 5f, rationale = "Stiffer springs limit suspension travel"),
            ParameterDelta("slowBump", cornerTarget = CornerTarget.ALL, baseDelta = 1f, rationale = "More slow bump resists compression under high-speed load")
        )
    ),

    // ── BRAKING ──────────────────────────────────────────────────────────────

    FeedbackItem(
        id = "front_lockup",
        category = FeedbackCategory.BRAKING,
        label = "Front wheel lock-up",
        description = "Front wheels lock under braking before the rears.",
        deltas = listOf(
            ParameterDelta("brakeBalance", baseDelta = -1.5f, rationale = "Move brake bias rearward to reduce front braking force"),
            ParameterDelta("abs", baseDelta = 1f, rationale = "Increase ABS intervention to prevent front lock"),
            ParameterDelta("brakePressure", baseDelta = -3f, rationale = "Overall pressure reduction reduces lock-up propensity")
        )
    ),
    FeedbackItem(
        id = "rear_lockup",
        category = FeedbackCategory.BRAKING,
        label = "Rear wheel lock-up",
        description = "Rear wheels lock causing snap or spin under braking.",
        deltas = listOf(
            ParameterDelta("brakeBalance", baseDelta = 1.5f, rationale = "Move bias forward to reduce rear braking force"),
            ParameterDelta("abs", baseDelta = 1f, rationale = "More ABS intervention prevents rear lock"),
            ParameterDelta("diffCoastRamp", baseDelta = -5f, rationale = "Less coast locking reduces rear lock tendency under braking")
        )
    ),
    FeedbackItem(
        id = "brake_soft",
        category = FeedbackCategory.BRAKING,
        label = "Brakes feel soft / poor response",
        description = "Pedal feels vague or requires excessive force to stop.",
        deltas = listOf(
            ParameterDelta("brakePressure", baseDelta = 3f, rationale = "Increase master cylinder scaling for firmer pedal feel"),
            ParameterDelta("padCompound", baseDelta = -1f, rationale = "Softer compound bites harder from cold"),
            ParameterDelta("brakeDuctFront", baseDelta = -1f, rationale = "Reduce duct opening to build heat faster for better response")
        )
    ),
    FeedbackItem(
        id = "brake_fade",
        category = FeedbackCategory.BRAKING,
        label = "Brake fade",
        description = "Braking performance degrades over a stint or after heavy braking zones.",
        deltas = listOf(
            ParameterDelta("padCompound", baseDelta = 1f, rationale = "Harder compound resists thermal fade"),
            ParameterDelta("brakeDuctFront", baseDelta = 1f, rationale = "More cooling air reduces brake temperatures"),
            ParameterDelta("brakeDuctRear", baseDelta = 1f, rationale = "Rear brake cooling also benefits under sustained use"),
            ParameterDelta("brakePressure", baseDelta = -2f, rationale = "Slightly less pressure reduces heat generation per stop")
        )
    ),
    FeedbackItem(
        id = "engine_braking_harsh",
        category = FeedbackCategory.BRAKING,
        label = "Harsh engine braking / rear snap on downshifts",
        description = "Rear unsettles or locks under heavy downshifting.",
        deltas = listOf(
            ParameterDelta("diffCoastRamp", baseDelta = -10f, rationale = "Less coast locking reduces rear lock-up from engine braking"),
            ParameterDelta("engineMap", baseDelta = 1f, rationale = "Higher engine map reduces engine braking effect"),
            ParameterDelta("slowRebound", cornerTarget = CornerTarget.REAR, baseDelta = -1f, rationale = "Softer rear rebound reduces rear snap on downshifts")
        )
    ),

    // ── TRACTION ─────────────────────────────────────────────────────────────

    FeedbackItem(
        id = "wheelspin",
        category = FeedbackCategory.TRACTION,
        label = "Wheelspin on exit",
        description = "Rear wheels spin when applying throttle, especially from slow corners.",
        deltas = listOf(
            ParameterDelta("diffPowerRamp", baseDelta = 5f, rationale = "More power ramp reduces differential between driven wheels"),
            ParameterDelta("tractionControl", baseDelta = 1f, rationale = "More TC intervention cuts power on wheelspin"),
            ParameterDelta("diffPreload", baseDelta = 10f, rationale = "More preload keeps diff locked, distributing torque evenly"),
            ParameterDelta("springRate", cornerTarget = CornerTarget.REAR, baseDelta = 3f, rationale = "Stiffer rear reduces squat and weight transfer off rear tyres")
        )
    ),
    FeedbackItem(
        id = "exit_understeer",
        category = FeedbackCategory.TRACTION,
        label = "Exit understeer (power understeer)",
        description = "Car pushes wide when applying full throttle on exit.",
        deltas = listOf(
            ParameterDelta("diffPowerRamp", baseDelta = -5f, rationale = "Less power ramp lets inside rear spin, allowing rotation"),
            ParameterDelta("diffPreload", baseDelta = -10f, rationale = "Less preload reduces locking effect that causes exit push"),
            ParameterDelta("tractionControl", baseDelta = -1f, rationale = "Less TC intervention allows more natural diff behaviour"),
            ParameterDelta("antiRollBarFront", baseDelta = -1f, rationale = "Softer front ARB reduces front load transfer, improving exit traction")
        )
    ),
    FeedbackItem(
        id = "low_traction",
        category = FeedbackCategory.TRACTION,
        label = "General low traction",
        description = "Rear feels slippery all-round, hard to put down power everywhere.",
        deltas = listOf(
            ParameterDelta("rearWing", baseDelta = 1f, rationale = "More rear downforce increases rear mechanical grip"),
            ParameterDelta("tirePressure", cornerTarget = CornerTarget.REAR, baseDelta = -0.4f, rationale = "Lower rear pressure widens contact patch"),
            ParameterDelta("tractionControl", baseDelta = 1f, rationale = "TC helps manage drive out of low-grip corners"),
            ParameterDelta("diffPowerRamp", baseDelta = 5f, rationale = "More power ramp helps distribute torque when grip is limited"),
            ParameterDelta("springRate", cornerTarget = CornerTarget.REAR, baseDelta = -3f, rationale = "Softer rear spring increases contact patch under acceleration squat")
        )
    ),
    FeedbackItem(
        id = "tyre_overheating",
        category = FeedbackCategory.TRACTION,
        label = "Tyre overheating / graining",
        description = "Grip degrades rapidly mid-stint, tyres graining or blistering.",
        deltas = listOf(
            ParameterDelta("tirePressure", cornerTarget = CornerTarget.ALL, baseDelta = -0.4f, rationale = "Lower pressure distributes heat across a wider contact patch"),
            ParameterDelta("antiRollBarFront", baseDelta = -1f, rationale = "Softer front ARB reduces sliding that generates heat"),
            ParameterDelta("antiRollBarRear", baseDelta = -1f, rationale = "Softer rear ARB reduces tyre scrub"),
            ParameterDelta("camber", cornerTarget = CornerTarget.ALL, baseDelta = 0.1f, rationale = "Slightly less negative camber reduces edge overheating")
        )
    ),

    // ── AERO ─────────────────────────────────────────────────────────────────

    FeedbackItem(
        id = "nervous_at_speed",
        category = FeedbackCategory.AERO,
        label = "Car nervous / twitchy at high speed",
        description = "Car feels on edge or requires aggressive corrections at high speed.",
        deltas = listOf(
            ParameterDelta("rearWing", baseDelta = 2f, rationale = "More rear downforce is the primary fix for high-speed nervousness"),
            ParameterDelta("frontWing", baseDelta = 1f, rationale = "More front downforce balances the aero platform"),
            ParameterDelta("rideHeightRear", baseDelta = -2f, rationale = "Lower rear ride height increases rear downforce from diffuser"),
            ParameterDelta("toe", cornerTarget = CornerTarget.REAR, baseDelta = 0.04f, rationale = "More rear toe-in adds passive high-speed stability"),
            ParameterDelta("caster", baseDelta = 0.25f, rationale = "More caster increases self-centring force at speed")
        )
    ),
    FeedbackItem(
        id = "too_much_drag",
        category = FeedbackCategory.AERO,
        label = "Too much drag / low top speed",
        description = "Car hits the limiter early or loses time on straights vs. rivals.",
        deltas = listOf(
            ParameterDelta("rearWing", baseDelta = -2f, rationale = "Less rear wing is the most direct drag reduction"),
            ParameterDelta("frontWing", baseDelta = -1f, rationale = "Less front wing reduces frontal drag area"),
            ParameterDelta("rideHeightFront", baseDelta = 2f, rationale = "Higher front ride height pitches car back, reducing front aero drag")
        )
    ),
    FeedbackItem(
        id = "aero_imbalance",
        category = FeedbackCategory.AERO,
        label = "Aero imbalance at speed",
        description = "Car balanced at low speed but over/understeers as speed builds.",
        deltas = listOf(
            ParameterDelta("rideHeightFront", baseDelta = -2f, rationale = "Lower front shifts aero balance forward at high speed"),
            ParameterDelta("rideHeightRear", baseDelta = 2f, rationale = "Higher rear reduces rear downforce ratio"),
            ParameterDelta("frontWing", baseDelta = 1f, rationale = "More front wing adds front downforce to correct high-speed push")
        )
    )
)

val feedbackItemById: Map<String, FeedbackItem> = feedbackItems.associateBy { it.id }
