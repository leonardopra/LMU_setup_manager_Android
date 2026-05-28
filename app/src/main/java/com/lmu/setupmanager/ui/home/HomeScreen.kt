package com.lmu.setupmanager.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmu.setupmanager.data.static.lmgt3Cars
import com.lmu.setupmanager.domain.model.Car
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.domain.model.Track
import com.lmu.setupmanager.domain.model.TrackCharacteristic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCarSelected: (carId: String, trackId: String, conditions: Conditions) -> Unit,
    onOpenLibrary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredTracks by viewModel.filteredTracks.collectAsStateWithLifecycle()
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    // ── Main scaffold ─────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("LMU Setup Manager") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Select a car",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lmgt3Cars, key = { it.id }) { car ->
                    CarCard(
                        car = car,
                        onClick = {
                            viewModel.selectTrack("")
                            viewModel.setTrackFilter("")
                            selectedCar = car
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onOpenLibrary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Saved Setups")
            }
        }
    }

    // ── Track & Conditions bottom sheet ───────────────────────────────────────
    if (selectedCar != null) {
        val car = selectedCar!!
        ModalBottomSheet(
            onDismissRequest = { selectedCar = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(text = car.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                // Track search
                OutlinedTextField(
                    value = uiState.trackFilter,
                    onValueChange = viewModel::setTrackFilter,
                    label = { Text("Search track") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                // Track list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredTracks, key = { it.id }) { track ->
                        TrackRow(
                            track = track,
                            selected = uiState.selectedTrackId == track.id,
                            onClick = { viewModel.selectTrack(track.id) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Conditions selector
                Text(
                    text = "Conditions",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Conditions.entries.forEach { cond ->
                        FilterChip(
                            selected = uiState.selectedConditions == cond,
                            onClick = { viewModel.setConditions(cond) },
                            label = { Text(cond.name) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Actions
                Button(
                    onClick = {
                        onCarSelected(car.id, uiState.selectedTrackId, uiState.selectedConditions)
                        selectedCar = null
                    },
                    enabled = uiState.selectedTrackId.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Setup")
                }
                TextButton(
                    onClick = {
                        onCarSelected(car.id, "", Conditions.DRY)
                        selectedCar = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip")
                }
            }
        }
    }
}

// ── Car card ──────────────────────────────────────────────────────────────────

@Composable
private fun CarCard(car: Car, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = car.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${car.manufacturer} · ${car.carClass}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Track row ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackRow(track: Track, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = track.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = track.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    track.characteristics.forEach { char ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = char.label(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun TrackCharacteristic.label() = when (this) {
    TrackCharacteristic.HIGH_SPEED -> "High Speed"
    TrackCharacteristic.TECHNICAL -> "Technical"
    TrackCharacteristic.BUMPY -> "Bumpy"
    TrackCharacteristic.STREET -> "Street"
    TrackCharacteristic.BALANCED -> "Balanced"
}
