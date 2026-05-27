package com.lmu.setupmanager.ui.wizard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmu.setupmanager.data.static.feedbackItemById
import com.lmu.setupmanager.data.static.feedbackItems
import com.lmu.setupmanager.domain.model.FeedbackCategory
import com.lmu.setupmanager.domain.model.ResolvedAdjustment
import com.lmu.setupmanager.domain.model.SelectedSymptom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScreen(
    carId: String,
    currentValues: Map<String, Float>,
    onAdjustmentsApplied: (Map<String, Float>) -> Unit,
    onBack: () -> Unit,
    viewModel: WizardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // One-shot: deliver results then reset the flag so this can't re-fire
    LaunchedEffect(uiState.isApplied) {
        if (uiState.isApplied) {
            onAdjustmentsApplied(
                uiState.resolvedAdjustments.associate { it.storeKey to it.proposedValue }
            )
            viewModel.consumeApplied()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.step.title()) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.step == WizardStep.SelectCategory) onBack()
                            else viewModel.back()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LinearProgressIndicator(
                progress = { uiState.step.index() / 4f },
                modifier = Modifier.fillMaxWidth()
            )
            when (uiState.step) {
                WizardStep.SelectCategory -> SelectCategoryStep(
                    onSelectCategory = viewModel::selectCategory
                )
                WizardStep.SelectSymptoms -> SelectSymptomsStep(
                    selectedCategory = uiState.selectedCategory,
                    selectedSymptoms = uiState.selectedSymptoms,
                    onToggleSymptom = viewModel::toggleSymptom,
                    onNext = viewModel::next
                )
                WizardStep.SetSeverity -> SetSeverityStep(
                    selectedSymptoms = uiState.selectedSymptoms,
                    onSetSeverity = viewModel::setSeverity,
                    onNext = viewModel::next
                )
                WizardStep.ReviewAdjustments -> ReviewAdjustmentsStep(
                    adjustments = uiState.resolvedAdjustments,
                    onApply = viewModel::next
                )
                WizardStep.Confirm -> ConfirmStep(
                    adjustmentCount = uiState.resolvedAdjustments.size,
                    onDone = viewModel::confirm
                )
            }
        }
    }
}

// ── Step 1: SelectCategory ────────────────────────────────────────────────────

@Composable
private fun SelectCategoryStep(
    onSelectCategory: (FeedbackCategory) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(FeedbackCategory.entries) { category ->
            CategoryCard(category = category, onClick = { onSelectCategory(category) })
        }
    }
}

@Composable
private fun CategoryCard(
    category: FeedbackCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = category.label,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Step 2: SelectSymptoms ────────────────────────────────────────────────────

@Composable
private fun SelectSymptomsStep(
    selectedCategory: FeedbackCategory?,
    selectedSymptoms: List<SelectedSymptom>,
    onToggleSymptom: (String) -> Unit,
    onNext: () -> Unit
) {
    if (selectedCategory == null) return

    val categoryItems = remember(selectedCategory) {
        feedbackItems.filter { it.category == selectedCategory }
    }
    val selectedIds = remember(selectedSymptoms) {
        selectedSymptoms.map { it.feedbackId }.toSet()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categoryItems, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleSymptom(item.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = item.id in selectedIds,
                        onCheckedChange = { onToggleSymptom(item.id) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        StepBottomBar {
            Button(
                onClick = onNext,
                enabled = selectedSymptoms.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }
        }
    }
}

// ── Step 3: SetSeverity ───────────────────────────────────────────────────────

@Composable
private fun SetSeverityStep(
    selectedSymptoms: List<SelectedSymptom>,
    onSetSeverity: (String, Int) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(selectedSymptoms, key = { it.feedbackId }) { symptom ->
                val item = feedbackItemById[symptom.feedbackId]
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item?.label ?: symptom.feedbackId,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    item?.description?.let { desc ->
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1 to "Mild", 2 to "Moderate", 3 to "Severe").forEach { (sev, label) ->
                            SeverityButton(
                                label = label,
                                selected = symptom.severity == sev,
                                onClick = { onSetSeverity(symptom.feedbackId, sev) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        StepBottomBar {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun SeverityButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(label)
    }
}

// ── Step 4: ReviewAdjustments ─────────────────────────────────────────────────

@Composable
private fun ReviewAdjustmentsStep(
    adjustments: List<ResolvedAdjustment>,
    onApply: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (adjustments.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No adjustments to apply.\nTry selecting different symptoms.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(adjustments, key = { it.storeKey }) { adj ->
                    AdjustmentCard(adjustment = adj)
                }
            }
        }

        StepBottomBar {
            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply")
            }
        }
    }
}

@Composable
private fun AdjustmentCard(
    adjustment: ResolvedAdjustment,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Header row (always visible, tappable to expand) ────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Param label + optional conflict badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = adjustment.paramLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (adjustment.wasConflicted) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Conflicting adjustments",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    // currentValue → proposedValue  (±delta)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${adjustment.currentValue.fmtValue()} → ${adjustment.proposedValue.fmtValue()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "(${adjustment.netDelta.fmtDelta()})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (adjustment.netDelta >= 0f) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Expandable: contributors with rationale ────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    if (adjustment.wasConflicted) {
                        Text(
                            text = "⚠ Symptoms conflict on this parameter — net result shown.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    adjustment.contributors.forEach { contributor ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = contributor.feedbackLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = contributor.scaledDelta.fmtDelta(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (contributor.scaledDelta >= 0f)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                            Text(
                                text = contributor.rationale,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Step 5: Confirm ───────────────────────────────────────────────────────────

@Composable
private fun ConfirmStep(
    adjustmentCount: Int,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "$adjustmentCount ${if (adjustmentCount == 1) "adjustment" else "adjustments"} applied to your setup.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

// ── Shared layout ─────────────────────────────────────────────────────────────

/** Divider + padded button row used by each step that has a primary action. */
@Composable
private fun StepBottomBar(content: @Composable () -> Unit) {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Surface(color = MaterialTheme.colorScheme.surface) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

private fun WizardStep.index(): Int = when (this) {
    WizardStep.SelectCategory    -> 0
    WizardStep.SelectSymptoms    -> 1
    WizardStep.SetSeverity       -> 2
    WizardStep.ReviewAdjustments -> 3
    WizardStep.Confirm           -> 4
}

private fun WizardStep.title(): String = when (this) {
    WizardStep.SelectCategory    -> "Select Category"
    WizardStep.SelectSymptoms    -> "Select Symptoms"
    WizardStep.SetSeverity       -> "Set Severity"
    WizardStep.ReviewAdjustments -> "Review Adjustments"
    WizardStep.Confirm           -> "Confirm"
}

/** Formats a Float as an integer when it has no fractional part, otherwise 2 decimal places. */
private fun Float.fmtValue(): String =
    if (this % 1f == 0f) "%.0f".format(this) else "%.2f".format(this)

/** Formats a delta with an explicit '+' sign for positive values. */
private fun Float.fmtDelta(): String =
    if (this >= 0f) "+%.2f".format(this) else "%.2f".format(this)
