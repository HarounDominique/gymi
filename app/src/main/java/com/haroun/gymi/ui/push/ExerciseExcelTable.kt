package com.haroun.gymi.ui.push

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haroun.gymi.persistence.ExerciseTable

@Composable
fun ExerciseExcelTable(
    tableIndex: Int,
    table: ExerciseTable,
    onAddColumn: () -> Unit,
    onCellChange: (row: Int, col: Int, value: String) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            table.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // encabezado
        Row(Modifier.fillMaxWidth()) {
            Text(
                "Día",
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray)
                    .padding(8.dp)
            )
            repeat(table.columns) { set ->
                Text(
                    "Set ${set + 1}",
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

        Column {
            repeat(table.rows) { row ->
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        "Día ${row + 1}",
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray)
                            .padding(8.dp)
                    )
                    repeat(table.columns) { col ->
                        TextField(
                            value = table.data[row][col],
                            onValueChange = { value -> onCellChange(row, col, value) },
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.Gray)
                                .padding(4.dp),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
