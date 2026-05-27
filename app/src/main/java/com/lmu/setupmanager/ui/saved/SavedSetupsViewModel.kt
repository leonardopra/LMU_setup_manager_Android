package com.lmu.setupmanager.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.domain.model.Setup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedSetupsUiState(
    val setups: List<Setup> = emptyList()
)

@HiltViewModel
class SavedSetupsViewModel @Inject constructor(
    private val repository: SetupRepository
) : ViewModel() {

    val uiState: StateFlow<SavedSetupsUiState> = repository.getAllSetups()
        .map { SavedSetupsUiState(setups = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SavedSetupsUiState()
        )

    fun deleteSetup(id: String) {
        viewModelScope.launch { repository.deleteSetup(id) }
    }
}
