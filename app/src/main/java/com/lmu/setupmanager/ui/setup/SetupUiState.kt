package com.lmu.setupmanager.ui.setup

import com.lmu.setupmanager.domain.model.Setup

data class SetupUiState(
    val setup: Setup,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val error: String? = null
)
