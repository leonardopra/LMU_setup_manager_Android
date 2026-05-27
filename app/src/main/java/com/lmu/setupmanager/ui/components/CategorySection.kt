package com.lmu.setupmanager.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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

/**
 * Collapsible section for a parameter category.
 * Shows a badge with the number of parameters modified from default.
 */
@Composable
fun CategorySection(
    title: String,
    parameters: List<SetupParameter>,
    values: Map<String, Float>,
    defaultValues: Map<String, Float>,
    onValueChange: (key: String, value: Float) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val modifiedCount = countModified(parameters, values, defaultValues)

    Column(modifier = modifier.fillMaxWidth()) {
        // Section header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (modifiedCount > 0) {
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text(
                                text = modifiedCount.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                parameters.forEach { param ->
                    if (param.cornerSpecific) {
                        CornerGrid(
                            parameter = param,
                            values = values,
                            onValueChange = onValueChange
                        )
                    } else {
                        ParameterSlider(
                            parameter = param,
                            value = values[param.key] ?: param.defaultValue,
                            onValueChange = { onValueChange(param.key, it) }
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

private fun countModified(
    parameters: List<SetupParameter>,
    values: Map<String, Float>,
    defaultValues: Map<String, Float>
): Int = parameters.count { param ->
    if (param.cornerSpecific) {
        listOf("FL", "FR", "RL", "RR").any { corner ->
            val key = "${param.key}_$corner"
            values[key] != (defaultValues[key] ?: param.defaultValue)
        }
    } else {
        values[param.key] != (defaultValues[param.key] ?: param.defaultValue)
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun CategorySectionPreview() {
    MaterialTheme {
        val params = listOf(
            SetupParameter(
                key = "frontWing", category = "aerodynamics", label = "Front Wing",
                description = "Front wing angle", unit = "°", dataType = DataType.INT,
                min = 0f, max = 15f, step = 1f, defaultValue = 7f, cornerSpecific = false
            ),
            SetupParameter(
                key = "rearWing", category = "aerodynamics", label = "Rear Wing",
                description = "Rear wing angle", unit = "°", dataType = DataType.INT,
                min = 0f, max = 15f, step = 1f, defaultValue = 8f, cornerSpecific = false
            )
        )
        val values = mapOf("frontWing" to 9f, "rearWing" to 8f)
        val defaults = mapOf("frontWing" to 7f, "rearWing" to 8f)

        CategorySection(
            title = "Aerodynamics",
            parameters = params,
            values = values,
            defaultValues = defaults,
            onValueChange = { _, _ -> }
        )
    }
}
