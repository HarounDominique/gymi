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
import androidx.compose.ui.text.style.TextAlign
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

    fun isRowLocked(rowIndex: Int): Boolean {
        val timestamp = table.rowDates[rowIndex] ?: return false
        val now = System.currentTimeMillis()
        val hoursPassed = (now - timestamp) / (1000 * 60 * 60)
        return hoursPassed >= 24
    }

    // =======================================
    //     C E L D A    V E R T I C A L
    // =======================================
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
                .width(100.dp)
                .padding(4.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { if (!enabled) onUnlock() }
                ),
            colors = CardDefaults.cardColors(
                containerColor =
                    if (enabled) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // KG arriba
                OutlinedTextField(
                    value = weight,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^\\d*(\\.\\d*)?$"))) {
                            onChange(reps, newValue)
                        }
                    },
                    placeholder = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "Kg",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )

                // Reps abajo
                OutlinedTextField(
                    value = reps,
                    onValueChange = { newValue ->
                        if (newValue.matches(Regex("^\\d*(\\.\\d*)?$"))) {
                            onChange(newValue, weight)
                        }
                    },
                    placeholder = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "Reps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // ============================================================
        //   CABECERA DE LA TABLA (Título + Botón Añadir Día)
        // ============================================================
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
                    modifier = Modifier.weight(1f)
                )

                Button(onClick = { onAddRow(tableIndex) }) {
                    Text("Añadir día")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


        // ============================================================
        //                TABLA DE EJERCICIOS (VISTA VERTICAL)
        // ============================================================

        val hState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(hState)
        ) {

            // -------------------------
            //  CABECERA DE LOS SETS
            // -------------------------
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text("", modifier = Modifier.width(80.dp)) // hueco esquina superior izq

                table.data.firstOrNull()?.forEachIndexed { c, _ ->
                    Text(
                        text = "Set ${c + 1}",
                        modifier = Modifier
                            .width(100.dp)
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // -------------------------
            //         FILAS (DÍAS)
            // -------------------------
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(table.data.size) { r ->
                    val rowList = table.data[r]
                    val isLocked = isRowLocked(r) && !unlockedRows.contains(r)

                    // Alternancia de estilo
                    val backgroundColor =
                        if (r % 2 == 0) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface

                    val elevation =
                        if (r % 2 == 0) 4.dp else 0.dp

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .combinedClickable(
                                onClick = {
                                    selectedRowIndex = r
                                    selectedRowDate = table.rowDates[r]
                                    showDateDialog = true
                                },
                                onLongClick = {
                                    if (isLocked) unlockedRows = unlockedRows + r
                                }
                            ),
                        colors = CardDefaults.cardColors(containerColor = backgroundColor),
                        elevation = CardDefaults.cardElevation(elevation)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            // Día
                            Text(
                                text = "Día ${r + 1}" +
                                        (if (isRowLocked(r)) " 🔒" else "") +
                                        (if (unlockedRows.contains(r)) " 🔓" else ""),
                                style = MaterialTheme.typography.titleSmall,
                                color = when {
                                    unlockedRows.contains(r) -> MaterialTheme.colorScheme.tertiary
                                    isLocked -> Color.Gray
                                    table.rowDates.containsKey(r) -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )

                            // Set verticales
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowList.forEachIndexed { c, value ->
                                    val parts = value.split("x")
                                    val reps = parts.getOrNull(0) ?: ""
                                    val weight = parts.getOrNull(1) ?: ""

                                    RepsWeightCell(
                                        reps = reps,
                                        weight = weight,
                                        enabled = !isLocked,
                                        onChange = { newReps, newW ->
                                            if (!isLocked) {
                                                val newValue = "${newReps}x${newW}"
                                                onCellChange(tableIndex, r, c, newValue)
                                                table.rowDates[r] = System.currentTimeMillis()
                                            }
                                        },
                                        onUnlock = {
                                            if (isLocked) unlockedRows = unlockedRows + r
                                        }
                                    )
                                }

                                IconButton(
                                    onClick = { if (!isLocked) onAddCellInRow(r) },
                                    enabled = !isLocked
                                ) { Text("+") }
                            }
                        }
                    }
                }
            }
        }
    }


    // =======================================
    //      D I Á L O G O   D E   F E C H A
    // =======================================

    if (showDateDialog) {
        val isLockedOriginal = isRowLocked(selectedRowIndex)
        val isUnlocked = unlockedRows.contains(selectedRowIndex)

        AlertDialog(
            onDismissRequest = { showDateDialog = false },
            title = { Text("Día ${selectedRowIndex + 1}") },
            text = {
                Column {
                    Text(
                        text = selectedRowDate?.let {
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            "Última modificación:\n${dateFormat.format(Date(it))}"
                        } ?: "Esta fila aún no ha sido modificada"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isLockedOriginal && !isUnlocked) {
                        Text(
                            "🔒 Esta fila está bloqueada (más de 24h). Mantén pulsado para desbloquear.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (isUnlocked) {
                        Text(
                            "🔓 Fila desbloqueada temporalmente.",
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
            dismissButton = if (isUnlocked) {
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