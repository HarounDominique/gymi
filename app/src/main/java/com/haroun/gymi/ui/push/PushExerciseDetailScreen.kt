package com.haroun.gymi.ui.push

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haroun.gymi.persistence.PushViewModel
import com.haroun.gymi.ui.components.SmallCapsTopAppBar

@Composable
fun PushExerciseDetailScreen(
    navController: NavController,
    viewModel: PushViewModel,
    tableIndex: Int
) {
    val table = viewModel.tables.getOrNull(tableIndex)

    Scaffold(
        topBar = {
            SmallCapsTopAppBar(title = table?.title ?: "Ejercicio")
        }
    ) { innerPadding ->
        if (table != null) {
            ExerciseExcelTable(
                table = table,
                tableIndex = tableIndex,
                onAddRow = { viewModel.addRowToTable(tableIndex) },
                onAddCellInRow = { rowIndex -> viewModel.addCellToRow(tableIndex, rowIndex) },
                onCellChange = { tIdx, row, col, value -> viewModel.updateCell(tIdx, row, col, value) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            // fallback view
            Text(
                text = "Ejercicio no encontrado",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        }
    }
}
