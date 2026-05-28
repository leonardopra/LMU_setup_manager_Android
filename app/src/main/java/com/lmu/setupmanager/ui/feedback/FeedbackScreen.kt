package com.lmu.setupmanager.ui.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmu.setupmanager.domain.model.Car
import com.lmu.setupmanager.domain.model.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onNavigateToWizard: (carId: String, currentValuesJson: String) -> Unit,
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState.step) {
                            FeedbackStep.SelectCar -> "Smart Feedback"
                            FeedbackStep.SelectTrack -> "Select Track"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            when (uiState.step) {
                                FeedbackStep.SelectCar -> onBack()
                                FeedbackStep.SelectTrack -> viewModel.navigateBack()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 8.dp)

        when (uiState.step) {
            FeedbackStep.SelectCar -> CarSelectionStep(
                cars = viewModel.cars,
                onCarSelected = viewModel::selectCar,
                modifier = contentModifier
            )
            FeedbackStep.SelectTrack -> TrackSelectionStep(
                tracks = viewModel.trackList,
                onTrackSelected = { trackId ->
                    viewModel.selectTrack(trackId)
                    val carId = uiState.selectedCarId ?: return@TrackSelectionStep
                    val json = Json.encodeToString(viewModel.defaultValuesForSelectedCar())
                    onNavigateToWizard(carId, json)
                },
                modifier = contentModifier
            )
        }
    }
}

@Composable
private fun CarSelectionStep(
    cars: List<Car>,
    onCarSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Select a car", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cars, key = { it.id }) { car ->
                Card(
                    onClick = { onCarSelected(car.id) },
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(car.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${car.manufacturer} · ${car.carClass}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackSelectionStep(
    tracks: List<Track>,
    onTrackSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Select a track", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tracks, key = { it.id }) { track ->
                Card(
                    onClick = { onTrackSelected(track.id) },
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(track.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = track.country,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
