package com.lmu.setupmanager.ui.setup

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lmu.setupmanager.data.static.allParameters
import com.lmu.setupmanager.data.static.categoryLabels
import com.lmu.setupmanager.data.static.categoryOrder
import com.lmu.setupmanager.domain.usecase.BuildDefaultValuesUseCase
import com.lmu.setupmanager.navigation.Screen
import com.lmu.setupmanager.ui.components.CategorySection
import com.lmu.setupmanager.ui.components.SetupSummaryCard
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    navController: NavController,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        RenameSetupDialog(
            currentName = uiState.setup.name,
            onConfirm = { newName ->
                viewModel.setName(newName)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    // One-shot: consume wizard adjustments delivered via back-stack SavedStateHandle
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<String?>("adjustments", null)
            ?.collect { json ->
                if (json != null) {
                    val adjustments: Map<String, Float> = Json.decodeFromString(json)
                    viewModel.batchUpdateValues(adjustments)
                    savedStateHandle.remove<String>("adjustments")
                }
            }
    }

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
    // One-shot: fire an Android share sheet with the exported setup JSON
    LaunchedEffect(uiState.exportJson) {
        uiState.exportJson?.let { json ->
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, json)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Export setup"))
            viewModel.consumeExportJson()
        }
    }

    val defaultValues = remember(uiState.setup.carId) {
        BuildDefaultValuesUseCase()(uiState.setup.carId)
    }

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
                    IconButton(onClick = { showRenameDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename setup")
                    }
                    IconButton(onClick = viewModel::undo, enabled = uiState.canUndo) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }
                    IconButton(onClick = viewModel::redo, enabled = uiState.canRedo) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                    }
                    IconButton(onClick = viewModel::shareSetup) {
                        Icon(Icons.Default.Share, contentDescription = "Export setup")
                    }
                    IconButton(onClick = viewModel::saveSetup) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val json = Json.encodeToString(uiState.setup.values)
                    navController.navigate(
                        Screen.Wizard.createRoute(uiState.setup.carId, json)
                    )
                }
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = "Setup Wizard")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                // ── Quick-summary widget ──────────────────────────────────────
                item(key = "summary") {
                    SetupSummaryCard(
                        setup = uiState.setup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        initiallyExpanded = false // collapsed by default to not crowd the screen
                    )
                }

                // ── Parameter categories ──────────────────────────────────────
                items(
                    items = categoryOrder,
                    key = { it }
                ) { category ->
                    val params = paramsByCategory[category] ?: return@items
                    val label = categoryLabels[category]
                        ?: category.replaceFirstChar { it.uppercase() }

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

// ── Rename dialog ─────────────────────────────────────────────────────────────

@Composable
private fun RenameSetupDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember(currentName) { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Setup") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim()) },
                enabled = name.isNotBlank()
            ) { Text("Rename") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}