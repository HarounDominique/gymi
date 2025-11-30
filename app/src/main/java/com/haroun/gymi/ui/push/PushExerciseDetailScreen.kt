package com.haroun.gymi.ui.push

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.haroun.gymi.persistence.ExerciseViewModel
import com.haroun.gymi.persistence.push.PushViewModel
import com.haroun.gymi.persistence.pull.PullViewModel
import com.haroun.gymi.persistence.legs.LegsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushExerciseDetailScreen(
    navController: NavController,
    viewModel: ExerciseViewModel,
    tableIndex: Int
) {
    val table = viewModel.tables.getOrNull(tableIndex)

    Scaffold(
    ) { innerPadding ->
        if (table != null) {
            ExerciseExcelTable(
                table = table,
                tableIndex = tableIndex,
                onAddRow = { viewModel.addRowToTable(tableIndex) },
                onAddCellInRow = { rowIndex ->
                    // Cast seguro según el tipo de ViewModel
                    when (viewModel) {
                        is PushViewModel -> viewModel.addCellToRow(tableIndex, rowIndex)
                        is PullViewModel -> viewModel.addCellToRow(tableIndex, rowIndex)
                        is LegsViewModel -> viewModel.addCellToRow(tableIndex, rowIndex)
                    }
                },
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