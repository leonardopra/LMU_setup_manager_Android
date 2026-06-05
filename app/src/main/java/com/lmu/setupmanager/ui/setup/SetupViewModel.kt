package com.lmu.setupmanager.ui.setup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.local.toEntity
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.domain.model.Conditions
import com.lmu.setupmanager.domain.model.Setup
import com.lmu.setupmanager.domain.usecase.BuildDefaultValuesUseCase
import com.lmu.setupmanager.domain.usecase.ExportSetupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val MAX_HISTORY = 50

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val repository: SetupRepository,
    private val buildDefaultValues: BuildDefaultValuesUseCase,
    private val exportSetup: ExportSetupUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val past = ArrayDeque<Setup>()
    private val future = ArrayDeque<Setup>()

    private val _uiState = MutableStateFlow(
        SetupUiState(setup = createNewSetup(
            savedStateHandle.get<String>("carId") ?: "bmw-m4-gt3"
        ))
    )
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("setupId")?.let { id ->
            viewModelScope.launch {
                repository.getSetupById(id)?.let { saved -> loadSetup(saved) }
            }
        }
    }

    // ── Public commands ───────────────────────────────────────────────────────

    fun initForCar(carId: String) {
        past.clear()
        future.clear()
        _uiState.value = SetupUiState(setup = createNewSetup(carId))
    }

    fun loadSetup(setup: Setup) {
        past.clear()
        future.clear()
        _uiState.value = SetupUiState(setup = setup)
    }

    fun updateValue(key: String, value: Float) {
        pushUndo()
        _uiState.update { state ->
            state.copy(
                setup = state.setup.copy(
                    values = state.setup.values + (key to value),
                    updatedAt = System.currentTimeMillis()
                ),
                canUndo = true,
                canRedo = false
            )
        }
        future.clear()
    }

    fun batchUpdateValues(updates: Map<String, Float>) {
        if (updates.isEmpty()) return
        pushUndo()
        _uiState.update { state ->
            state.copy(
                setup = state.setup.copy(
                    values = state.setup.values + updates,
                    updatedAt = System.currentTimeMillis()
                ),
                canUndo = true,
                canRedo = false
            )
        }
        future.clear()
    }

    fun setName(name: String) = setMeta { copy(name = name) }
    fun setTrackId(trackId: String) = setMeta { copy(trackId = trackId) }
    fun setConditions(conditions: Conditions) = setMeta { copy(conditions = conditions) }
    fun setNotes(notes: String) = setMeta { copy(notes = notes) }

    fun reset() {
        pushUndo()
        _uiState.update { state ->
            state.copy(
                setup = createNewSetup(state.setup.carId),
                canUndo = true,
                canRedo = false
            )
        }
        future.clear()
    }

    fun undo() {
        if (past.isEmpty()) return
        val previous = past.removeLast()
        future.addFirst(_uiState.value.setup)
        if (future.size > MAX_HISTORY) future.removeLast()
        _uiState.update { state ->
            state.copy(
                setup = previous,
                canUndo = past.isNotEmpty(),
                canRedo = true
            )
        }
    }

    fun redo() {
        if (future.isEmpty()) return
        val next = future.removeFirst()
        past.addLast(_uiState.value.setup)
        if (past.size > MAX_HISTORY) past.removeFirst()
        _uiState.update { state ->
            state.copy(
                setup = next,
                canUndo = true,
                canRedo = future.isNotEmpty()
            )
        }
    }

    fun saveSetup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching { repository.saveSetup(_uiState.value.setup) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun shareSetup() {
        val json = exportSetup(_uiState.value.setup.toEntity())
        _uiState.update { it.copy(exportJson = json) }
    }

    fun consumeExportJson() {
        _uiState.update { it.copy(exportJson = null) }
    }

    fun consumeSavedSuccessfully() {
        _uiState.update { it.copy(savedSuccessfully = false) }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun setMeta(transform: Setup.() -> Setup) {
        pushUndo()
        _uiState.update { state ->
            state.copy(
                setup = state.setup.transform().copy(updatedAt = System.currentTimeMillis()),
                canUndo = true,
                canRedo = false
            )
        }
        future.clear()
    }

    private fun pushUndo() {
        past.addLast(_uiState.value.setup)
        if (past.size > MAX_HISTORY) past.removeFirst()
    }

    private fun createNewSetup(carId: String) = Setup(
        id = UUID.randomUUID().toString(),
        name = "New Setup",
        carId = carId,
        trackId = "",
        conditions = Conditions.DRY,
        values = buildDefaultValues(carId),
        notes = "",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}
