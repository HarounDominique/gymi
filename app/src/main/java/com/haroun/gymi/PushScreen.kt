package com.haroun.gymi

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haroun.gymi.persistence.PushViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushScreen(
    onBack: () -> Unit = {},
    viewModel: PushViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ejercicios de Empuje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir tabla")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(viewModel.tables) { index, table ->
                ExerciseExcelTable(
                    table = table,
                    onAddColumn = { viewModel.addColumnToTable(index) }
                )
            }
        }
    }

    if (showDialog) {
        AddExerciseDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                viewModel.addTable(name)
                showDialog = false
            }
        )
    }
}


data class ExerciseTable(
    val name: String,
    val rows: Int = 20,   // número de días visibles (podría ser más, incluso “infinito” con Lazy)
    val columns: Int = 3  // sets iniciales
)

@Composable
fun ExerciseExcelTable(table: ExerciseTable, onAddColumn: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        // Título
        Text(
            text = table.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Encabezado
        Row(Modifier.fillMaxWidth()) {
            Text(
                "Día",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            )
            for (set in 1..table.columns) {
                Text(
                    "Set $set",
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                )
            }
            IconButton(onClick = onAddColumn) {
                Icon(Icons.Default.Add, contentDescription = "Añadir set")
            }
        }

        // Filas (días)
        Column {
            for (day in 1..table.rows) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        "Día $day",
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray)
                            .padding(8.dp)
                    )
                    for (set in 1..table.columns) {
                        var cell by remember { mutableStateOf("") }
                        TextField(
                            value = cell,
                            onValueChange = { cell = it },
                            singleLine = true,
                            placeholder = { Text("Peso x Reps") },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.Gray)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddExerciseDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo ejercicio") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Nombre del ejercicio") }
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onConfirm(text) }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}