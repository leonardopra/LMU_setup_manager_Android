package com.lmu.setupmanager.ui.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lmu.setupmanager.data.static.allParameters
import com.lmu.setupmanager.data.static.categoryLabels
import com.lmu.setupmanager.data.static.categoryOrder
import com.lmu.setupmanager.domain.usecase.BuildDefaultValuesUseCase
import com.lmu.setupmanager.ui.components.CategorySection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // Side effects
    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            snackbarHostState.showSnackbar("Setup saved!")
            viewModel.consumeSavedSuccessfully()
        }
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.consumeError()
        }
    }

    // Pre-compute default values for the current car (for the modified-count badge)
    val defaultValues = remember(uiState.setup.carId) {
        BuildDefaultValuesUseCase()(uiState.setup.carId)
    }

    // Group parameters by category
    val paramsByCategory = remember {
        allParameters.groupBy { it.category }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.setup.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = viewModel::undo,
                        enabled = uiState.canUndo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo"
                        )
                    }
                    IconButton(
                        onClick = viewModel::redo,
                        enabled = uiState.canRedo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo"
                        )
                    }
                    IconButton(onClick = viewModel::saveSetup) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    items = categoryOrder,
                    key = { it }
                ) { category ->
                    val params = paramsByCategory[category] ?: return@items
                    val label = categoryLabels[category] ?: category.replaceFirstChar { it.uppercase() }

                    CategorySection(
                        title = label,
                        parameters = params,
                        values = uiState.setup.values,
                        defaultValues = defaultValues,
                        onValueChange = viewModel::updateValue,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
