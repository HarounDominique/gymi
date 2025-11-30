package com.haroun.gymi.ui.push

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
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

    @Composable
    fun RepsWeightCell(
        reps: String,
        weight: String,
        enabled: Boolean,
        onChange: (String, String) -> Unit,
        onUnlock: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier
                .width(180.dp)
                .padding(4.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { if (!enabled) onUnlock() }
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 📌 Celda REPS
                OutlinedTextField(
                    value = reps,
                    onValueChange = { if (it.all { c -> c.isDigit() }) onChange(it, weight) },
                    placeholder = {
                        Text(
                            "R",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall, // 🔹 Más pequeño
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    singleLine = true,
                    enabled = enabled,
                    maxLines = 1
                )

                // ❌ Separador
                Text(
                    "x",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodySmall // 🔹 ligeramente más pequeño
                )

                // 📌 Celda KG
                OutlinedTextField(
                    value = weight,
                    onValueChange = { if (it.all { c -> c.isDigit() }) onChange(reps, it) },
                    placeholder = {
                        Text(
                            "Kg",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall, // 🔹 Más pequeño
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    singleLine = true,
                    enabled = enabled,
                    maxLines = 1
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Header: table title + add day button
        // 🔹 Card que actúa como cabecera de sección
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = table.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f) // ocupa solo lo necesario
                )

                Button(onClick = { onAddRow(tableIndex) }) {
                    Text("Añadir día")
                }
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
                            // Separar reps y peso desde el valor almacenado
                            val parts = value.split("x")
                            val reps = parts.getOrNull(0) ?: ""
                            val weight = parts.getOrNull(1) ?: ""

                            RepsWeightCell(
                                reps = reps,
                                weight = weight,
                                enabled = !isLocked,
                                onChange = { newReps, newWeight ->
                                    val newValue = "${newReps}x${newWeight}"
                                    if (!isLocked) {
                                        onCellChange(tableIndex, r, c, newValue)
                                        table.rowDates[r] = System.currentTimeMillis()
                                    }
                                },
                                onUnlock = { if (isLocked) unlockedRows = unlockedRows + r },
                                modifier = Modifier.width(140.dp)
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