package com.haroun.gymi.domain.mapper

import com.haroun.gymi.domain.model.ProgressPoint
import com.haroun.gymi.domain.model.SetEntry
import com.haroun.gymi.domain.model.TrainingDay
import com.haroun.gymi.persistence.ExerciseTable

private const val SET_SEPARATOR = "x"

fun parseSet(cell: String): SetEntry? {
    val parts = cell.split(SET_SEPARATOR)
    if (parts.size != 2) return null

    val reps = parts[0].toIntOrNull()
    val weight = parts[1].toFloatOrNull()

    return if (reps != null && weight != null) {
        SetEntry(reps, weight)
    } else null
}

fun ExerciseTable.toTrainingDays(): List<TrainingDay> {
    return data.mapIndexedNotNull { rowIndex, row ->
        val date = rowDates[rowIndex] ?: return@mapIndexedNotNull null

        val sets = row.mapNotNull { cell ->
            parseSet(cell)
        }

        if (sets.isEmpty()) return@mapIndexedNotNull null

        TrainingDay(
            date = date,
            sets = sets
        )
    }.sortedBy { it.date }
}

fun TrainingDay.bestSetByWeight(): SetEntry =
    sets.maxBy { it.weight }

fun List<TrainingDay>.toProgressPoints(): List<ProgressPoint> {
    return map {
        val best = it.bestSetByWeight()
        ProgressPoint(
            date = it.date,
            weight = best.weight,
            reps = best.reps
        )
    }
}
