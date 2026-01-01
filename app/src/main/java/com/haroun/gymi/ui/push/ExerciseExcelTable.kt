package com.haroun.gymi.ui.push

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroun.gymi.domain.model.ProgressPoint
import com.haroun.gymi.persistence.ExerciseTable
import com.haroun.gymi.progress.ExerciseProgressScreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    var unlockedRows by remember { mutableStateOf(setOf<Int>()) }

    fun isRowLocked(rowIndex: Int): Boolean {
        val timestamp = table.rowDates[rowIndex] ?: return false
        val now = System.currentTimeMillis()
        val hoursPassed = (now - timestamp) / (1000 * 60 * 60)
        return hoursPassed >= 24
    }

    // Estado para mostrar modal de progreso
    var showProgress by remember { mutableStateOf(false) }

    @Composable
    fun CenteredBasicField(
        value: String,
        placeholderText: String,
        enabled: Boolean,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var internal by remember { mutableStateOf(TextFieldValue(text = value)) }

        LaunchedEffect(value) {
            if (value != internal.text) {
                internal = TextFieldValue(value)
            }
        }

        Box(
            modifier = modifier
                .height(36.dp)
                .fillMaxWidth()
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            if (internal.text.isEmpty()) {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            BasicTextField(
                value = internal,
                onValueChange = { tfv ->
                    val newText = tfv.text
                    if (newText.matches(Regex("^\\d*(\\.\\d*)?$")) || newText.isEmpty()) {
                        internal = tfv
                        onValueChange(newText)
                    }
                },
                enabled = enabled,
                textStyle = TextStyle(
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ),
                singleLine = true
            )
        }
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
                .width(120.dp)
                .padding(6.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { if (!enabled) onUnlock() }
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (enabled) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(1.5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CenteredBasicField(
                    value = weight,
                    placeholderText = "Kg",
                    enabled = enabled,
                    onValueChange = { newWeight -> onChange(reps, newWeight) },
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                )

                CenteredBasicField(
                    value = reps,
                    placeholderText = "Reps",
                    enabled = enabled,
                    onValueChange = { newReps -> onChange(newReps, weight) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // --- HEADER CARD ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = table.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                // Botón de gráfica de progreso
                IconButton(onClick = { showProgress = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange, // icono seguro para prueba
                        contentDescription = "Ver progreso"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { onAddRow(tableIndex) }) {
                    Text("Añadir día")
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        val hState = rememberScrollState()

        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(hState)) {

            Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(text = "", modifier = Modifier.width(92.dp))
                table.data.firstOrNull()?.forEachIndexed { c, _ ->
                    Text(
                        text = "Set ${c + 1}",
                        modifier = Modifier
                            .width(128.dp)
                            .padding(6.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn {
                itemsIndexed(table.data, key = { idx, _ -> "row-$idx" }) { r, rowList ->

                    val rowBg = if (r % 2 == 0) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                    }

                    val rowCardElevation = if (r % 2 == 0) 0.dp else 2.dp

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .shadow(rowCardElevation),
                        colors = CardDefaults.cardColors(containerColor = rowBg),
                        elevation = CardDefaults.cardElevation(rowCardElevation)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Día ${r + 1}" +
                                        if (isRowLocked(r)) " 🔒" else "" +
                                                if (unlockedRows.contains(r)) " 🔓" else "",
                                modifier = Modifier
                                    .width(92.dp)
                                    .combinedClickable(
                                        onClick = {
                                            selectedRowIndex = r
                                            selectedRowDate = table.rowDates[r]
                                            showDateDialog = true
                                        },
                                        onLongClick = {
                                            if (isRowLocked(r)) unlockedRows = unlockedRows + r
                                        }
                                    )
                                    .padding(4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    unlockedRows.contains(r) -> MaterialTheme.colorScheme.tertiary
                                    isRowLocked(r) -> Color.Gray
                                    table.rowDates.containsKey(r) -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            rowList.forEachIndexed { c, value ->
                                val parts = value.split("x", limit = 2)
                                val reps = parts.getOrNull(0) ?: ""
                                val weight = parts.getOrNull(1) ?: ""

                                RepsWeightCell(
                                    reps = reps,
                                    weight = weight,
                                    enabled = !isRowLocked(r) || unlockedRows.contains(r),
                                    onChange = { newReps, newWeight ->
                                        val newVal = "${newReps}x${newWeight}"
                                        onCellChange(tableIndex, r, c, newVal)
                                        table.rowDates[r] = System.currentTimeMillis()
                                    },
                                    onUnlock = { unlockedRows = unlockedRows + r },
                                    modifier = Modifier.width(128.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                onClick = {
                                    if (!isRowLocked(r) || unlockedRows.contains(r)) {
                                        onAddCellInRow(r)
                                    }
                                },
                                enabled = !(isRowLocked(r) && !unlockedRows.contains(r))
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Modal Bottom Sheet para progreso ---
    if (showProgress) {
        ModalBottomSheet(
            onDismissRequest = { showProgress = false },
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            // Datos de prueba para ver la gráfica
            val fakePoints = listOf(
                ProgressPoint(date = System.currentTimeMillis() - 3_000_000, weight = 80f, reps = 6),
                ProgressPoint(date = System.currentTimeMillis() - 2_000_000, weight = 82.5f, reps = 6),
                ProgressPoint(date = System.currentTimeMillis() - 1_000_000, weight = 85f, reps = 5)
            )

            // Mostramos la gráfica usando la pantalla de progreso
            ExerciseProgressScreen(
                table = table.copy(data = table.data) // solo para pasar algo válido
            )
        }
    }

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
                Button(onClick = {
                    showDateDialog = false
                }) { Text("Cerrar") }
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