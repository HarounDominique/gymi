package com.haroun.gymi.persistence

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.serialization.Serializable

/**
 * DTO para persistencia -> serializable por DataStore
 */
@Serializable
data class ExerciseTableDto(
    val name: String,
    val rows: List<List<String>>
)

/**
 * Modelo usado por Compose (NO serializable)
 */
data class ExerciseTable(
    val name: String,
    var rows: Int,
    var columns: Int,
    val data: SnapshotStateList<SnapshotStateList<String>>
)

/**
 * Crear tabla vacía (4x3 por defecto)
 */
fun createEmptyExerciseTable(
    name: String,
    rows: Int = 4,
    cols: Int = 3
): ExerciseTable {
    val tableData = mutableStateListOf<SnapshotStateList<String>>()

    repeat(rows) {
        val row = mutableStateListOf<String>()
        repeat(cols) { row.add("") }
        tableData.add(row)
    }

    return ExerciseTable(name, rows, cols, tableData)
}

/**
 * Convertir DTO -> modelo Compose
 */
fun dtoToTable(dto: ExerciseTableDto): ExerciseTable {
    val rows = dto.rows.size
    val cols = if (dto.rows.isNotEmpty()) dto.rows[0].size else 0

    val stateRows = mutableStateListOf<SnapshotStateList<String>>()
    dto.rows.forEach { row ->
        stateRows.add(mutableStateListOf<String>().apply { addAll(row) })
    }

    return ExerciseTable(dto.name, rows, cols, stateRows)
}

/**
 * Convertir modelo Compose -> DTO serializable
 */
fun tableToDto(table: ExerciseTable): ExerciseTableDto {
    val rows = table.data.map { it.toList() }
    return ExerciseTableDto(name = table.name, rows = rows)
}
