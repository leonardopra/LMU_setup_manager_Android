package com.lmu.setupmanager.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lmu.setupmanager.data.static.carById
import com.lmu.setupmanager.data.static.tracks
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.domain.model.Setup
import kotlin.math.roundToInt

private data class SummaryGroup(
    val icon: ImageVector,
    val title: String,
    val items: List<Pair<String, String>> // label → value
)

/**
 * Collapsible card that shows a quick overview of the most important
 * setup parameters without needing to scroll through every category.
 *
 * Intended to be embedded at the top of [SetupScreen] and in setup cards.
 */
@Composable
fun SetupSummaryCard(
    setup: Setup,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = true
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    val groups = remember(setup.values) { buildSummaryGroups(setup) }
    val carName = carById[setup.carId]?.name ?: setup.carId
    val trackName = tracks.firstOrNull { it.id == setup.trackId }?.name ?: "No track"
    val conditionLabel = when (setup.conditions) {
        Conditions.DRY -> "☀ Dry"
        Conditions.WET -> "🌧 Wet"
        Conditions.MIXED -> "⛅ Mixed"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = setup.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$carName · $trackName · $conditionLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse summary" else "Expand summary"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    groups.forEachIndexed { index, group ->
                        SummaryGroupRow(group = group)
                        if (index < groups.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryGroupRow(group: SummaryGroup) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = group.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            group.items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { (label, value) ->
                        SummaryChip(
                            label = label,
                            value = value,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty slot if odd number of items in last row
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun SummaryChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

// ── Data builder ──────────────────────────────────────────────────────────────

private fun buildSummaryGroups(setup: Setup): List<SummaryGroup> {
    val v = setup.values

    fun Float?.fmt(decimals: Int = 1): String =
        if (this == null) "–" else "%.${decimals}f".format(this)

    fun Float?.fmtInt(): String =
        if (this == null) "–" else this.roundToInt().toString()

    return listOf(
        SummaryGroup(
            icon = Icons.Default.Air,
            title = "Aerodynamics",
            items = listOf(
                "Front Wing" to (v["frontWing"]?.fmtInt() ?: "–"),
                "Rear Wing" to (v["rearWing"]?.fmtInt() ?: "–"),
                "RH Front" to (v["rideHeightFront"].fmt(0) + " mm"),
                "RH Rear" to (v["rideHeightRear"].fmt(0) + " mm")
            )
        ),
        SummaryGroup(
            icon = Icons.Default.Tune,
            title = "Suspension & Balance",
            items = listOf(
                "ARB Front" to (v["antiRollBarFront"]?.fmtInt() ?: "–"),
                "ARB Rear" to (v["antiRollBarRear"]?.fmtInt() ?: "–"),
                "Caster" to (v["caster"].fmt() + "°"),
                "Diff Pre" to (v["diffPreload"]?.fmtInt() + " Nm")
            )
        ),
        SummaryGroup(
            icon = Icons.Default.Speed,
            title = "Brakes",
            items = listOf(
                "Balance" to (v["brakeBalance"].fmt() + "%"),
                "Pressure" to (v["brakePressure"]?.fmtInt() + "%")
            )
        ),
        SummaryGroup(
            icon = Icons.Default.LocalGasStation,
            title = "Fuel & Electronics",
            items = listOf(
                "Fuel" to (v["fuelLoad"].fmt(0) + " L"),
                "TC" to (v["tractionControl"]?.fmtInt() ?: "–"),
                "ABS" to (v["abs"]?.fmtInt() ?: "–"),
                "Map" to (v["engineMap"]?.fmtInt() ?: "–")
            )
        )
    )
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun SetupSummaryCardPreview() {
    MaterialTheme {
        SetupSummaryCard(
            setup = Setup(
                id = "preview",
                name = "Spa Qualifying",
                carId = "bmw-m4-gt3",
                trackId = "spa",
                conditions = Conditions.DRY,
                values = mapOf(
                    "frontWing" to 9f,
                    "rearWing" to 11f,
                    "rideHeightFront" to 63f,
                    "rideHeightRear" to 73f,
                    "antiRollBarFront" to 6f,
                    "antiRollBarRear" to 4f,
                    "caster" to 7.5f,
                    "diffPreload" to 55f,
                    "brakeBalance" to 58.5f,
                    "brakePressure" to 92f,
                    "fuelLoad" to 110f,
                    "tractionControl" to 3f,
                    "abs" to 2f,
                    "engineMap" to 1f
                ),
                notes = "",
                createdAt = 0L,
                updatedAt = 0L
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}