package com.haroun.gymi.ui.push

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haroun.gymi.persistence.ExerciseViewModel
import com.haroun.gymi.persistence.push.PushViewModel
import com.haroun.gymi.persistence.pull.PullViewModel
import com.haroun.gymi.persistence.legs.LegsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushScreen(
    navController: NavController,
    viewModel: ExerciseViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    var actionIndex by remember { mutableStateOf<Int?>(null) }
    var renameDialog by remember { mutableStateOf(false) }
    var renameTitle by remember { mutableStateOf("") }
    var renameIndex by remember { mutableStateOf<Int?>(null) }

    var deleteIndex by remember { mutableStateOf<Int?>(null) }
    var confirmDeleteDialog by remember { mutableStateOf(false) }

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
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = screenTitle.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                modifier = Modifier.height(48.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabecera
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ejercicios", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = { showDialog = true }) { Text("+") }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de ejercicios
            LazyColumn {
                items(viewModel.tables.size) { index ->
                    val t = viewModel.tables[index]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = { navController.navigate("$routeBase/exercise/$index") },
                                onLongClick = { actionIndex = index }
                            ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = t.title.ifBlank { "Ejercicio ${index + 1}" },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo nuevo ejercicio
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo ejercicio") },
            text = { TextField(value = title, onValueChange = { title = it }, label = { Text("Nombre") }) },
            confirmButton = {
                Button(onClick = {
                    val safeTitle = title.ifBlank { "Ejercicio" }
                    val truncatedTitle = safeTitle.take(50)
                    when (viewModel) {
                        is PushViewModel -> viewModel.addTable(viewModel.createDefaultTable(truncatedTitle))
                        is PullViewModel -> viewModel.addTable(viewModel.createDefaultTable(truncatedTitle))
                        is LegsViewModel -> viewModel.addTable(viewModel.createDefaultTable(truncatedTitle))
                    }
                    title = ""
                    showDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = { Button(onClick = { title = ""; showDialog = false }) { Text("Cancelar") } }
        )
    }

    // Diálogo opciones al mantener pulsado
    if (actionIndex != null) {
        AlertDialog(
            onDismissRequest = { actionIndex = null },
            title = { Text("Opciones del ejercicio") },
            text = { Text("Elige una acción para este ejercicio") },
            confirmButton = {
                Button(onClick = {
                    renameDialog = true
                    renameIndex = actionIndex
                    renameTitle = viewModel.tables[actionIndex!!].title
                    actionIndex = null
                }) { Text("Renombrar") }
            },
            dismissButton = {
                Button(onClick = {
                    deleteIndex = actionIndex
                    confirmDeleteDialog = true
                    actionIndex = null
                }) { Text("Eliminar") }
            }
        )
    }

    // Diálogo renombrar ejercicio
    if (renameDialog && renameIndex != null) {
        AlertDialog(
            onDismissRequest = { renameDialog = false; renameIndex = null },
            title = { Text("Renombrar ejercicio") },
            text = {
                TextField(
                    value = renameTitle,
                    onValueChange = { renameTitle = it },
                    label = { Text("Nombre") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    val index = renameIndex!!
                    viewModel.renameTable(index, renameTitle)
                    renameDialog = false
                    renameIndex = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                Button(onClick = {
                    renameDialog = false
                    renameIndex = null
                }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo confirmar borrado
    if (confirmDeleteDialog && deleteIndex != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteDialog = false; deleteIndex = null },
            title = { Text("Eliminar ejercicio") },
            text = { Text("¿Seguro que quieres eliminar este ejercicio? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteTable(deleteIndex!!)
                    confirmDeleteDialog = false
                    deleteIndex = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                Button(onClick = { confirmDeleteDialog = false; deleteIndex = null }) { Text("Cancelar") }
            }
        )
    }
}