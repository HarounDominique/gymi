package com.haroun.gymi.ui.push

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haroun.gymi.persistence.ExerciseViewModel
import com.haroun.gymi.persistence.push.PushViewModel
import com.haroun.gymi.persistence.pull.PullViewModel
import com.haroun.gymi.persistence.legs.LegsViewModel
import com.haroun.gymi.ui.components.SmallCapsTopAppBar

@Composable
fun PushScreen(
    navController: NavController,
    viewModel: ExerciseViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    val routeBase = when (viewModel) {
        is PushViewModel -> "push"
        is PullViewModel -> "pull"
        is LegsViewModel -> "legs"
        else -> "push"
    }

    val screenTitle = when (viewModel) {
        is PushViewModel -> "Empuje"
        is PullViewModel -> "Tirón"
        is LegsViewModel -> "Pierna"
        else -> "Ejercicios"
    }

    Scaffold(
        topBar = {
            SmallCapsTopAppBar(title = screenTitle)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 🔹 Card que actúa como cabecera de sección
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ejercicios",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = { showDialog = true }) {
                        Text("+")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(viewModel.tables.size) { index ->
                    val t = viewModel.tables[index]
                    Button(
                        onClick = { navController.navigate("$routeBase/exercise/$index") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(text = t.title.ifBlank { "Ejercicio ${index + 1}" })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo ejercicio") },
            text = {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val safeTitle = title.ifBlank { "Ejercicio" }
                    when (viewModel) {
                        is PushViewModel -> viewModel.addTable(viewModel.createDefaultTable(safeTitle))
                        is PullViewModel -> viewModel.addTable(viewModel.createDefaultTable(safeTitle))
                        is LegsViewModel -> viewModel.addTable(viewModel.createDefaultTable(safeTitle))
                    }
                    title = ""
                    showDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    title = ""
                    showDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}