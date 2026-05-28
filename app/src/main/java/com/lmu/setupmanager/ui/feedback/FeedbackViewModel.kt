package com.lmu.setupmanager.ui.feedback

import androidx.lifecycle.ViewModel
import com.lmu.setupmanager.data.static.lmgt3Cars
import com.lmu.setupmanager.data.static.tracks
import com.lmu.setupmanager.domain.model.Car
import com.lmu.setupmanager.domain.model.Track
import com.lmu.setupmanager.domain.usecase.BuildDefaultValuesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class FeedbackStep {
    data object SelectCar : FeedbackStep()
    data object SelectTrack : FeedbackStep()
}

data class FeedbackUiState(
    val step: FeedbackStep = FeedbackStep.SelectCar,
    val selectedCarId: String? = null,
    val selectedTrackId: String? = null
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val buildDefaultValues: BuildDefaultValuesUseCase
) : ViewModel() {

    val cars: List<Car> = lmgt3Cars
    val trackList: List<Track> = tracks

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun selectCar(carId: String) {
        _uiState.update { it.copy(selectedCarId = carId, step = FeedbackStep.SelectTrack) }
    }

    fun selectTrack(trackId: String) {
        _uiState.update { it.copy(selectedTrackId = trackId) }
    }

    fun navigateBack() {
        _uiState.update {
            if (it.step is FeedbackStep.SelectTrack)
                it.copy(step = FeedbackStep.SelectCar, selectedTrackId = null)
            else it
        }
    }

    fun defaultValuesForSelectedCar(): Map<String, Float> {
        val carId = _uiState.value.selectedCarId ?: return emptyMap()
        return buildDefaultValues(carId)
    }
}
