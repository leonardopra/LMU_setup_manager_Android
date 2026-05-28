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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

// ── UI state ──────────────────────────────────────────────────────────────────

data class SavedSetupsUiState(
    val setups: List<Setup> = emptyList(),
    val renameTargetId: String? = null,      // non-null = rename dialog open for this id
    val renameCurrentName: String = "",
    val importError: Boolean = false         // one-shot: show snackbar on bad import
)

// Internal state for mutable dialog / flag fields; kept separate so the
// repository flow drives `setups` reactively without fighting with update().
private data class DialogState(
    val renameTargetId: String? = null,
    val renameCurrentName: String = "",
    val importError: Boolean = false
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@HiltViewModel
class SavedSetupsViewModel @Inject constructor(
    private val repository: SetupRepository
) : ViewModel() {

    private val exportImportJson = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val _dialog = MutableStateFlow(DialogState())

    val uiState: StateFlow<SavedSetupsUiState> = combine(
        repository.getAllSetups(),
        _dialog
    ) { setups, dialog ->
        SavedSetupsUiState(
            setups = setups,
            renameTargetId = dialog.renameTargetId,
            renameCurrentName = dialog.renameCurrentName,
            importError = dialog.importError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SavedSetupsUiState()
    )

    // ── Existing operations ───────────────────────────────────────────────────

    fun deleteSetup(id: String) {
        viewModelScope.launch { repository.deleteSetup(id) }
    }

    // ── New operations ────────────────────────────────────────────────────────

    fun renameSetup(id: String, newName: String) {
        viewModelScope.launch { repository.renameSetup(id, newName.trim()) }
    }

    fun duplicateSetup(id: String) {
        viewModelScope.launch { repository.duplicateSetup(id) }
    }

    // ── Export / Import ───────────────────────────────────────────────────────

    /** Serializes [setup] to a pretty-free JSON string ready for sharing. */
    fun exportSetupJson(setup: Setup): String =
        exportImportJson.encodeToString(setup)

    /**
     * Parses [json] into a [Setup] with a fresh id and timestamps, then persists it.
     * Sets [SavedSetupsUiState.importError] on parse failure so the UI can show a snackbar.
     * Returns `true` on success, `false` on any error.
     */
    suspend fun importSetupFromJson(json: String): Boolean {
        return try {
            val parsed = exportImportJson.decodeFromString<Setup>(json)
            val imported = parsed.copy(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.saveSetup(imported)
            true
        } catch (e: Exception) {
            _dialog.update { it.copy(importError = true) }
            false
        }
    }

    // ── Dialog state ──────────────────────────────────────────────────────────

    fun openRenameDialog(id: String, currentName: String) {
        _dialog.update { it.copy(renameTargetId = id, renameCurrentName = currentName) }
    }

    fun closeRenameDialog() {
        _dialog.update { it.copy(renameTargetId = null, renameCurrentName = "") }
    }

    /** Resets the one-shot [SavedSetupsUiState.importError] flag after the snackbar is shown. */
    fun consumeImportError() {
        _dialog.update { it.copy(importError = false) }
    }
}
