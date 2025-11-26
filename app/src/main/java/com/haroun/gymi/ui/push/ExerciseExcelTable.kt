package com.haroun.gymi.ui.push

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haroun.gymi.persistence.ExerciseTable
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseExcelTable(
    table: ExerciseTable,
    tableIndex: Int,
    onAddRow: (Int) -> Unit,
    onAddCellInRow: (rowIndex: Int) -> Unit,
    onCellChange: (tableIndex: Int, row: Int, col: Int, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var selectedRowDate by remember { mutableStateOf<Long?>(null) }
    var selectedRowIndex by remember { mutableStateOf(-1) }

    // Estado para controlar qué filas están temporalmente desbloqueadas
    var unlockedRows by remember { mutableStateOf(setOf<Int>()) }

    // Función helper para verificar si una fila está bloqueada (más de 24h desde modificación)
    fun isRowLocked(rowIndex: Int): Boolean {
        val timestamp = table.rowDates[rowIndex] ?: return false
        val now = System.currentTimeMillis()
        val hoursPassed = (now - timestamp) / (1000 * 60 * 60)
        return hoursPassed >= 24
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header: table title + add day button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = table.title, style = MaterialTheme.typography.titleMedium)
            Button(onClick = { onAddRow(tableIndex) }) {
                Text("Añadir día")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Table body with horizontal scroll for sets
        val hState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(hState)
        ) {
            // Header row: empty cell for top-left corner + Set labels
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = "", // top-left corner empty
                    modifier = Modifier.width(80.dp)
                )
                table.data.firstOrNull()?.forEachIndexed { c, _ ->
                    Text(
                        text = "Set ${c + 1}",
                        modifier = Modifier
                            .width(140.dp)
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Rows (days)
            LazyColumn {
                items(table.data.size) { r ->
                    val rowList = table.data[r]
                    val isLocked = isRowLocked(r) && !unlockedRows.contains(r)

                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        // Day label - clickeable para ver fecha, long press para desbloquear
                        Text(
                            text = "Día ${r + 1}${if (isRowLocked(r)) " 🔒" else ""}${if (unlockedRows.contains(r)) " 🔓" else ""}",
                            modifier = Modifier
                                .width(80.dp)
                                .padding(4.dp)
                                .combinedClickable(
                                    onClick = {
                                        selectedRowIndex = r
                                        selectedRowDate = table.rowDates[r]
                                        showDateDialog = true
                                    },
                                    onLongClick = {
                                        if (isRowLocked(r)) {
                                            unlockedRows = unlockedRows + r
                                        }
                                    }
                                ),
                            style = MaterialTheme.typography.labelLarge,
                            color = when {
                                unlockedRows.contains(r) -> MaterialTheme.colorScheme.tertiary
                                isLocked -> Color.Gray
                                table.rowDates.containsKey(r) -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )

                        // Cells
                        rowList.forEachIndexed { c, value ->
                            LockableTableCell(
                                value = value,
                                onValueChange = { newValue ->
                                    if (!isLocked) {
                                        onCellChange(tableIndex, r, c, newValue)
                                        // Actualizar fecha cuando se modifica una celda
                                        if (newValue.isNotBlank()) {
                                            table.rowDates[r] = System.currentTimeMillis()
                                        }
                                    }
                                },
                                isLocked = isLocked,
                                onUnlock = {
                                    if (isRowLocked(r)) {
                                        unlockedRows = unlockedRows + r
                                    }
                                },
                                modifier = Modifier
                                    .width(140.dp)
                                    .padding(4.dp)
                            )
                        }

                        // Button to add a new cell to this row (deshabilitado si está bloqueada)
                        IconButton(
                            onClick = { if (!isLocked) onAddCellInRow(r) },
                            enabled = !isLocked
                        ) {
                            Text("+")
                        }
                    }
                }
            }
        }
    }

    // Diálogo para mostrar la fecha
    if (showDateDialog) {
        val isLockedOriginal = isRowLocked(selectedRowIndex)
        val isCurrentlyUnlocked = unlockedRows.contains(selectedRowIndex)

        AlertDialog(
            onDismissRequest = { showDateDialog = false },
            title = { Text("Día ${selectedRowIndex + 1}") },
            text = {
                Column {
                    Text(
                        text = if (selectedRowDate != null) {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            "Última modificación:\n${dateFormat.format(Date(selectedRowDate!!))}"
                        } else {
                            "Esta fila aún no ha sido modificada"
                        }
                    )
                    if (isLockedOriginal && !isCurrentlyUnlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔒 Esta fila está bloqueada (más de 24 horas)\n\nMantén pulsado el nombre del día o cualquier celda para desbloquear temporalmente.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (isCurrentlyUnlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔓 Fila desbloqueada temporalmente para edición",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDateDialog = false }) {
                    Text("Cerrar")
                }
            },
            dismissButton = if (isCurrentlyUnlocked) {
                {
                    TextButton(onClick = {
                        unlockedRows = unlockedRows - selectedRowIndex
                        showDateDialog = false
                    }) {
                        Text("Bloquear de nuevo")
                    }
                }
            } else null
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LockableTableCell(
    value: String,
    onValueChange: (String) -> Unit,
    isLocked: Boolean,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = !isLocked,
        singleLine = true,
        modifier = modifier.combinedClickable(
            onClick = { },
            onLongClick = {
                if (isLocked) {
                    onUnlock()
                }
            }
        ),
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Gray,
            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f)
        )
    )
}