package com.lmu.setupmanager.ui.diff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmu.setupmanager.data.repository.SetupRepository
import com.lmu.setupmanager.data.static.parametersByKey
import com.lmu.setupmanager.domain.model.Setup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

data class DiffRow(
    val key: String,
    val label: String,
    val category: String,
    val unit: String,
    val valueA: Float?,
    val valueB: Float?,
    val isDifferent: Boolean
)

data class SetupDiffUiState(
    val setupA: Setup? = null,
    val setupB: Setup? = null,
    val rows: List<DiffRow> = emptyList(),
    val showOnlyDiffs: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SetupDiffViewModel @Inject constructor(
    private val repository: SetupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupDiffUiState())
    val uiState: StateFlow<SetupDiffUiState> = _uiState.asStateFlow()

    fun load(setupIdA: String, setupIdB: String) {
        viewModelScope.launch {
            _uiState.value = SetupDiffUiState(isLoading = true)
            val a = repository.getSetupById(setupIdA)
            val b = repository.getSetupById(setupIdB)
            if (a == null || b == null) {
                _uiState.value = SetupDiffUiState(
                    isLoading = false,
                    error = "One or both setups not found"
                )
                return@launch
            }
            val rows = buildDiffRows(a, b)
            _uiState.value = SetupDiffUiState(
                setupA = a,
                setupB = b,
                rows = rows,
                isLoading = false
            )
        }
    }

    fun toggleShowOnlyDiffs() {
        _uiState.value = _uiState.value.copy(
            showOnlyDiffs = !_uiState.value.showOnlyDiffs
        )
    }

    private fun buildDiffRows(a: Setup, b: Setup): List<DiffRow> {
        val allKeys = (a.values.keys + b.values.keys).distinct()
        return allKeys.mapNotNull { key ->
            val param = parametersByKey[key] ?: return@mapNotNull null
            val valA = a.values[key]
            val valB = b.values[key]
            val different = valA != null && valB != null &&
                    abs((valA) - (valB)) > 0.0001f
            DiffRow(
                key = key,
                label = param.label,
                category = param.category,
                unit = param.unit,
                valueA = valA,
                valueB = valB,
                isDifferent = different
            )
        }.sortedWith(compareBy({ it.category }, { it.label }))
    }
}