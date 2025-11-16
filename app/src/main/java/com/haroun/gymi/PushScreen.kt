package com.haroun.gymi.ui.push

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haroun.gymi.persistence.ExerciseTable
import com.haroun.gymi.persistence.PushViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushScreen(
    onBack: () -> Unit = {},
    viewModel: PushViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Añadir ejercicio:",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFeatureSettings = "smcp"
                        )
                    )
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
                    tableIndex = index,
                    table = table,
                    onAddColumn = { viewModel.addColumnToTable(index) },
                    onAddRow = { viewModel.addRowToTable(index) },
                    onCellChange = { row, col, value ->
                        viewModel.updateCell(index, row, col, value)
                    }
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
