// app/src/main/java/com/haroun/gymi/persistence/ExerciseTable.kt
package com.haroun.gymi.persistence

import kotlinx.serialization.Serializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf

@Serializable
data class ExerciseTableDto(
    val title: String,
    val rows: List<List<String>>,
    val rowDates: Map<Int, Long> = emptyMap() // Timestamp por fila (índice -> millis)
)

data class ExerciseTable(
    val title: String,
    val data: SnapshotStateList<SnapshotStateList<String>>,
    val rowDates: MutableMap<Int, Long> = mutableMapOf() // Timestamp por fila
) {
    val columnCount: Int
        get() = data.firstOrNull()?.size ?: 0
}

/** Mapping extensions */
fun ExerciseTable.toDto(): ExerciseTableDto {
    val rowsList = data.map { row -> row.toList() } // snapshot -> plain list
    return ExerciseTableDto(
        title = this.title,
        rows = rowsList,
        rowDates = this.rowDates.toMap()
    )
}

fun ExerciseTableDto.toDomain(): ExerciseTable {
    val rows = mutableStateListOf<SnapshotStateList<String>>()
    rows.addAll(this.rows.map { row ->
        val s = mutableStateListOf<String>()
        s.addAll(row)
        s
    })
    return ExerciseTable(
        title = this.title,
        data = rows,
        rowDates = this.rowDates.toMutableMap()
    )
}