package com.lmu.setupmanager.domain.model

enum class FeedbackCategory(val label: String, val description: String) {
    BALANCE("Balance", "Understeer, oversteer, and cornering behaviour"),
    STABILITY("Stability", "Straight-line, kerbs, bumps, and general composure"),
    BRAKING("Braking", "Lock-ups, fade, pedal feel, and engine braking"),
    TRACTION("Traction", "Wheelspin, exit grip, and tyre wear"),
    AERO("Aerodynamics", "High-speed feel, drag, and aero balance")
}

enum class CornerTarget { ALL, FRONT, REAR, FL, FR, RL, RR }

data class ParameterDelta(
    val paramKey: String,
    val cornerTarget: CornerTarget = CornerTarget.ALL,
    val baseDelta: Float,
    val rationale: String
)

data class FeedbackItem(
    val id: String,
    val category: FeedbackCategory,
    val label: String,
    val description: String,
    val deltas: List<ParameterDelta>
)

data class SelectedSymptom(
    val feedbackId: String,
    val severity: Int // 1, 2, or 3
)
