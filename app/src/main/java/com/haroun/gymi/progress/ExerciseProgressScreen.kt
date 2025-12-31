package com.haroun.gymi.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haroun.gymi.domain.mapper.toProgressPoints
import com.haroun.gymi.domain.mapper.toTrainingDays
import com.haroun.gymi.persistence.ExerciseTable

@Composable
fun ExerciseProgressScreen(
    table: ExerciseTable
) {
    val trainingDays = remember(table) {
        table.toTrainingDays()
    }

    val progressPoints = remember(trainingDays) {
        trainingDays.toProgressPoints()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Progreso",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        WeightProgressChart(
            points = progressPoints,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
