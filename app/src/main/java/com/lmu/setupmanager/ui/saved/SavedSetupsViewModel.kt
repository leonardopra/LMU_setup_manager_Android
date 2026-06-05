package com.lmu.setupmanager.ui.saved

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.domain.model.Setup
import com.lmu.setupmanager.domain.usecase.ImportSetupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject

data class SavedSetupsUiState(
    val setups: List<Setup> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val importMessage: String? = null
)

@HiltViewModel
class SavedSetupsViewModel @Inject constructor(
    private val repository: SetupRepository,
    private val importSetupUseCase: ImportSetupUseCase
) : ViewModel() {

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _importMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SavedSetupsUiState> = combine(
        repository.getAllSetups(),
        _selectedIds,
        _importMessage
    ) { setups, selected, importMessage ->
        // Remove stale selections when a setup is deleted
        SavedSetupsUiState(
            setups = setups,
            selectedIds = selected.filter { id -> setups.any { it.id == id } }.toSet(),
            importMessage = importMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SavedSetupsUiState()
    )

    fun importSetup(uri: Uri) {
        viewModelScope.launch {
            _importMessage.value = try {
                importSetupUseCase(uri)
                "Setup imported"
            } catch (_: SerializationException) {
                "Invalid setup file"
            } catch (_: IOException) {
                "Invalid setup file"
            }
        }
    }

    fun consumeImportMessage() {
        _importMessage.value = null
    }

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