package com.lmu.setupmanager.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.domain.model.Setup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedSetupsUiState(
    val setups: List<Setup> = emptyList(),
    val selectedIds: Set<String> = emptySet()
)

@HiltViewModel
class SavedSetupsViewModel @Inject constructor(
    private val repository: SetupRepository
) : ViewModel() {

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())

    val uiState: StateFlow<SavedSetupsUiState> = combine(
        repository.getAllSetups(),
        _selectedIds
    ) { setups, selected ->
        // Remove stale selections when a setup is deleted
        SavedSetupsUiState(
            setups = setups,
            selectedIds = selected.filter { id -> setups.any { it.id == id } }.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SavedSetupsUiState()
    )

    fun deleteSetup(id: String) {
        viewModelScope.launch { repository.deleteSetup(id) }
        _selectedIds.value = _selectedIds.value - id
    }

    fun toggleSelect(id: String) {
        val current = _selectedIds.value
        _selectedIds.value = if (id in current) {
            current - id
        } else if (current.size < 2) {
            current + id
        } else {
            current // already 2 selected, ignore
        }
    }
}