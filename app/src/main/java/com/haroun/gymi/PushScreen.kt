// app/src/main/java/com/haroun/gymi/ui/push/PushScreen.kt
package com.haroun.gymi.ui.push

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haroun.gymi.persistence.ExerciseTable
import com.haroun.gymi.persistence.PushViewModel
import com.haroun.gymi.ui.components.SmallCapsTopAppBar

@Composable
fun PushScreen(
    navController: NavController,
    viewModel: PushViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SmallCapsTopAppBar(title = "Empuje")
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
        ) {
            Text("Ejercicios", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(viewModel.tables.size) { index ->
                    val t = viewModel.tables[index]
                    Button(
                        onClick = {
                            // navigate to detail with index
                            navController.navigate("push/exercise/${index.toString()}")
                        },
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
                    val newTable = viewModel.createDefaultTable(
                        title = title.ifBlank { "Ejercicio" }
                    )
                    viewModel.addTable(newTable)
                    showDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
