package com.haroun.gymi.ui.push

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haroun.gymi.persistence.ExerciseTable

@Composable
fun ExerciseExcelTable(
    table: ExerciseTable,
    tableIndex: Int,
    onAddRow: (Int) -> Unit,
    onAddCellInRow: (rowIndex: Int) -> Unit,
    onCellChange: (tableIndex: Int, row: Int, col: Int, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        // Day label
                        Text(
                            text = "Día ${r + 1}",
                            modifier = Modifier
                                .width(80.dp)
                                .padding(4.dp),
                            style = MaterialTheme.typography.labelLarge
                        )

                        // Cells
                        rowList.forEachIndexed { c, value ->
                            TableCell(
                                value = value,
                                onValueChange = { newValue -> onCellChange(tableIndex, r, c, newValue) },
                                modifier = Modifier
                                    .width(140.dp)
                                    .padding(4.dp)
                            )
                        }

                        // Button to add a new cell to this row
                        IconButton(onClick = { onAddCellInRow(r) }) {
                            Text("+")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier
    )
}
