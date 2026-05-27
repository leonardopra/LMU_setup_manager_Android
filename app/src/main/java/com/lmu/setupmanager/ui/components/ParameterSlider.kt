package com.lmu.setupmanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lmu.setupmanager.domain.model.DataType
import com.lmu.setupmanager.domain.model.EnumOption
import com.lmu.setupmanager.domain.model.SetupParameter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParameterSlider(
    parameter: SetupParameter,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDirectInput by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parameter.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = { showDirectInput = true }) {
                Text(
                    text = formatValue(parameter, value),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        when (parameter.dataType) {
            DataType.ENUM -> {
                EnumDropdown(
                    options = parameter.enumOptions ?: emptyList(),
                    selectedValue = value.roundToInt(),
                    onSelect = { onValueChange(it.toFloat()) }
                )
            }
            else -> {
                val steps = calculateSteps(parameter)
                Slider(
                    value = value,
                    onValueChange = { raw ->
                        onValueChange(snapToStep(raw, parameter))
                    },
                    valueRange = parameter.min..parameter.max,
                    steps = steps,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showDirectInput) {
        DirectInputDialog(
            label = parameter.label,
            currentValue = value,
            min = parameter.min,
            max = parameter.max,
            unit = parameter.unit,
            onConfirm = { newVal ->
                onValueChange(newVal.coerceIn(parameter.min, parameter.max))
                showDirectInput = false
            },
            onDismiss = { showDirectInput = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnumDropdown(
    options: List<EnumOption>,
    selectedValue: Int,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.value == selectedValue }?.label ?: "-"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelect(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DirectInputDialog(
    label: String,
    currentValue: Float,
    min: Float,
    max: Float,
    unit: String,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentValue.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    isError = it.toFloatOrNull() == null
                },
                label = { Text("Value${if (unit.isNotEmpty()) " ($unit)" else ""}") },
                isError = isError,
                supportingText = if (isError) {
                    { Text("Enter a valid number between $min and $max") }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    text.toFloatOrNull()?.let { onConfirm(it) }
                },
                enabled = !isError && text.toFloatOrNull() != null
            ) { Text("Apply") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatValue(parameter: SetupParameter, value: Float): String {
    val formatted = when (parameter.dataType) {
        DataType.INT -> value.roundToInt().toString()
        DataType.ENUM -> {
            parameter.enumOptions?.firstOrNull { it.value == value.roundToInt() }?.label
                ?: value.roundToInt().toString()
        }
        DataType.FLOAT -> {
            val decimals = when {
                parameter.step < 0.01f -> 3
                parameter.step < 0.1f -> 2
                parameter.step < 1f -> 1
                else -> 0
            }
            "%.${decimals}f".format(value)
        }
    }
    return if (parameter.unit.isNotEmpty()) "$formatted ${parameter.unit}" else formatted
}

/** Returns the number of discrete steps for Compose Slider (0 = continuous). */
private fun calculateSteps(parameter: SetupParameter): Int {
    if (parameter.step <= 0f) return 0
    val range = parameter.max - parameter.min
    val count = (range / parameter.step).roundToInt()
    // Compose Slider's `steps` = number of intervals minus the endpoints, i.e. count - 1
    return if (count > 1) count - 1 else 0
}

/** Snaps a raw slider value to the nearest valid step. */
private fun snapToStep(raw: Float, parameter: SetupParameter): Float {
    if (parameter.step <= 0f) return raw
    val steps = ((raw - parameter.min) / parameter.step).roundToInt()
    return (parameter.min + steps * parameter.step).coerceIn(parameter.min, parameter.max)
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ParameterSliderFloatPreview() {
    MaterialTheme {
        var value by remember { mutableFloatStateOf(7.5f) }
        ParameterSlider(
            parameter = SetupParameter(
                key = "caster", category = "suspension", label = "Caster",
                description = "Caster angle", unit = "°", dataType = DataType.FLOAT,
                min = 3.0f, max = 12.0f, step = 0.25f, defaultValue = 7.5f,
                cornerSpecific = false
            ),
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParameterSliderEnumPreview() {
    MaterialTheme {
        var value by remember { mutableFloatStateOf(2f) }
        ParameterSlider(
            parameter = SetupParameter(
                key = "padCompound", category = "brakes", label = "Pad Compound",
                description = "Brake pad compound", unit = "", dataType = DataType.ENUM,
                min = 1f, max = 4f, step = 1f,
                enumOptions = listOf(
                    EnumOption("Soft", 1), EnumOption("Medium-Soft", 2),
                    EnumOption("Medium-Hard", 3), EnumOption("Hard", 4)
                ),
                defaultValue = 2f, cornerSpecific = false
            ),
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}
