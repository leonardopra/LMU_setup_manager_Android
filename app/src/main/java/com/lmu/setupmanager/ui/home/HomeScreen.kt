package com.lmu.setupmanager.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lmu.setupmanager.data.static.lmgt3Cars
import com.lmu.setupmanager.domain.model.Car

/**
 * Landing screen: select a car to start editing a new setup.
 * Phase 3 will add track selection and setup management here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCarSelected: (carId: String) -> Unit,
    onOpenLibrary: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("LMU Setup Manager") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Select a car",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lmgt3Cars, key = { it.id }) { car ->
                    CarCard(car = car, onClick = { onCarSelected(car.id) })
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onOpenLibrary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Saved Setups")
            }
        }
    }
}

@Composable
private fun CarCard(car: Car, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = car.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${car.manufacturer} · ${car.carClass}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("New Setup")
            }
        }
    }
}
