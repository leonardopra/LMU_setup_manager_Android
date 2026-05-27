package com.lmu.setupmanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lmu.setupmanager.domain.model.DataType
import com.lmu.setupmanager.domain.model.SetupParameter

private val CORNERS = listOf("FL", "FR", "RL", "RR")

/**
 * 2×2 grid (FL | FR / RL | RR) for corner-specific parameters.
 * Includes a "Link All" button that propagates the FL value to all corners.
 */
@Composable
fun CornerGrid(
    parameter: SetupParameter,
    values: Map<String, Float>,   // expects keys: "key_FL", "key_FR", "key_RL", "key_RR"
    onValueChange: (key: String, value: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var linked by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header row: label + link toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parameter.label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            FilledTonalButton(
                onClick = {
                    linked = !linked
                    if (linked) {
                        // Sync all corners to FL value
                        val flValue = values["${parameter.key}_FL"] ?: parameter.defaultValue
                        for (corner in CORNERS) {
                            onValueChange("${parameter.key}_$corner", flValue)
                        }
                    }
                }
            ) {
                Text(if (linked) "Linked ✓" else "Link All")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2×2 grid
        val topCorners = listOf("FL", "FR")
        val bottomCorners = listOf("RL", "RR")

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(topCorners, bottomCorners).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { corner ->
                        val key = "${parameter.key}_$corner"
                        val cornerValue = values[key] ?: parameter.defaultValue

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = corner,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ParameterSlider(
                                parameter = parameter,
                                value = cornerValue,
                                onValueChange = { newValue ->
                                    if (linked) {
                                        // Propagate to all corners
                                        for (c in CORNERS) {
                                            onValueChange("${parameter.key}_$c", newValue)
                                        }
                                    } else {
                                        onValueChange(key, newValue)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun CornerGridPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            val param = SetupParameter(
                key = "tirePressure", category = "tires", label = "Tire Pressure",
                description = "Cold tire pressure", unit = "PSI", dataType = DataType.FLOAT,
                min = 24f, max = 35f, step = 0.1f, defaultValue = 27.5f, cornerSpecific = true
            )
            var values by remember {
                mutableStateOf(
                    mapOf(
                        "tirePressure_FL" to 27.5f,
                        "tirePressure_FR" to 27.5f,
                        "tirePressure_RL" to 27.5f,
                        "tirePressure_RR" to 27.5f
                    )
                )
            }
            CornerGrid(
                parameter = param,
                values = values,
                onValueChange = { key, value -> values = values + (key to value) }
            )
        }
    }
}
