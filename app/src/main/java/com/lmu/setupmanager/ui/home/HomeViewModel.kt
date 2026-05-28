package com.lmu.setupmanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.static.tracks
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.domain.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiState(
    val trackFilter: String = "",
    val selectedTrackId: String = "",
    val selectedConditions: Conditions = Conditions.DRY
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val filteredTracks: StateFlow<List<Track>> = _uiState
        .map { state ->
            if (state.trackFilter.isBlank()) tracks
            else tracks.filter { it.name.contains(state.trackFilter, ignoreCase = true) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = tracks
        )

    fun setTrackFilter(filter: String) = _uiState.update { it.copy(trackFilter = filter) }
    fun selectTrack(trackId: String) = _uiState.update { it.copy(selectedTrackId = trackId) }
    fun setConditions(conditions: Conditions) = _uiState.update { it.copy(selectedConditions = conditions) }
}
