package com.lmu.setupmanager.ui.diff

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmu.setupmanager.data.static.categoryLabels
import com.lmu.setupmanager.data.static.categoryOrder
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupDiffScreen(
    setupIdA: String,
    setupIdB: String,
    onBack: () -> Unit,
    viewModel: SetupDiffViewModel = hiltViewModel()
) {
    LaunchedEffect(setupIdA, setupIdB) {
        viewModel.load(setupIdA, setupIdB)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Comparison") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    val diffCount = uiState.rows.count { it.isDifferent }
                    FilterChip(
                        selected = uiState.showOnlyDiffs,
                        onClick = viewModel::toggleShowOnlyDiffs,
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Diffs only")
                                if (diffCount > 0) {
                                    Spacer(Modifier.width(4.dp))
                                    Badge { Text(diffCount.toString()) }
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {
                val setupA = uiState.setupA!!
                val setupB = uiState.setupB!!
                val visibleRows = if (uiState.showOnlyDiffs) {
                    uiState.rows.filter { it.isDifferent }
                } else {
                    uiState.rows
                }
                val byCategory = visibleRows.groupBy { it.category }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Header with setup names
                    item {
                        DiffHeader(nameA = setupA.name, nameB = setupB.name)
                    }

                    // Rows grouped by category
                    categoryOrder.forEach { cat ->
                        val rows = byCategory[cat] ?: return@forEach
                        if (rows.isEmpty()) return@forEach

                        item(key = "cat_$cat") {
                            CategoryHeader(
                                label = categoryLabels[cat] ?: cat.replaceFirstChar { it.uppercase() }
                            )
                        }

                        items(rows, key = { it.key }) { row ->
                            DiffRowItem(row = row)
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DiffHeader(nameA: String, nameB: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Label column
            Box(modifier = Modifier.weight(1.4f))
            // A name
            Text(
                text = nameA,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            // B name
            Text(
                text = nameB,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryHeader(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            letterSpacing = androidx.compose.ui.unit.TextUnit(
                1.5f,
                androidx.compose.ui.unit.TextUnitType.Sp
            )
        )
    }
}

@Composable
private fun DiffRowItem(row: DiffRow) {
    val highlightColor = MaterialTheme.colorScheme.tertiaryContainer
    val normalColor = MaterialTheme.colorScheme.surface

    val bgColor by animateColorAsState(
        targetValue = if (row.isDifferent) highlightColor else normalColor,
        animationSpec = tween(300),
        label = "diffRowBg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Parameter label
        Column(modifier = Modifier.weight(1.4f)) {
            Text(
                text = row.label,
                style = MaterialTheme.typography.bodySmall,
                color = if (row.isDifferent)
                    MaterialTheme.colorScheme.onTertiaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (row.unit.isNotEmpty()) {
                Text(
                    text = row.unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Value A
        val textColor = if (row.isDifferent)
            MaterialTheme.colorScheme.onTertiaryContainer
        else
            MaterialTheme.colorScheme.onSurface

        Text(
            text = row.valueA?.formatValue() ?: "–",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (row.isDifferent) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        // Arrow indicator when different
        if (row.isDifferent) {
            val delta = (row.valueB ?: 0f) - (row.valueA ?: 0f)
            Text(
                text = if (delta > 0) "▲" else "▼",
                style = MaterialTheme.typography.labelSmall,
                color = if (delta > 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.width(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        // Value B
        Text(
            text = row.valueB?.formatValue() ?: "–",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (row.isDifferent) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun Float.formatValue(): String {
    return if (this == this.roundToInt().toFloat()) {
        this.roundToInt().toString()
    } else {
        "%.2f".format(this)
    }
}