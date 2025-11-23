// app/src/main/java/com/haroun/gymi/persistence/PushViewModel.kt
package com.haroun.gymi.persistence

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Domain model (ExerciseTable) assumed to be in ExerciseTable.kt with mapping helpers to/from DTO.
 * tables: MutableStateList so Compose observe insertions/changes.
 */
class PushViewModel(
    private val storage: ExerciseStorage
) : ViewModel() {

    val tables = mutableStateListOf<ExerciseTable>() // ExerciseTable is domain class with SnapshotStateList rows

    init {
        // load persisted tables and map to domain model
        storage.getTables()
            .onEach { dtoList ->
                // replace contents of tables to keep same list reference for observers
                tables.clear()
                tables.addAll(dtoList.map { it.toDomain() })
            }
            .launchIn(viewModelScope)
    }

    private fun persist() {
        // Serialize current tables to DTOs
        viewModelScope.launch {
            val dtos = tables.map { it.toDto() }
            storage.saveTables(dtos)
        }
    }

    fun addTable(newTable: ExerciseTable) {
        tables.add(newTable)
        persist()
    }

    fun removeTable(index: Int) {
        if (index in tables.indices) {
            tables.removeAt(index)
            persist()
        }
    }

    fun addColumnToTable(tableIndex: Int) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]
        val cols = table.columnCount
        // for each row, add empty cell at end (use mutable lists to be observable)
        table.data.forEach { row ->
            row.add("")
        }
        persist()
    }

    fun addCellToRow(tableIndex: Int, rowIndex: Int) {
        val table = tables.getOrNull(tableIndex) ?: return
        // Asegúrate de usar SnapshotStateList para que Compose detecte cambios
        table.data[rowIndex].add("")
        persist()
    }

    fun addRowToTable(tableIndex: Int) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]
        val cols = table.columnCount
        val newRow = mutableStateListOf<String>()
        repeat(cols) { newRow.add("") }
        // Append newRow to table.data (which must be SnapshotStateList)
        table.data.add(newRow)
        persist()
    }

    fun updateCell(tableIndex: Int, row: Int, col: Int, value: String) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]
        if (row !in table.data.indices) return
        val rowList = table.data[row]
        if (col !in rowList.indices) return
        rowList[col] = value // SnapshotStateList assignment triggers recomposition
        persist()
    }

    // helper to create a default empty table (4 rows x 3 cols) as mentioned in spec
    fun createDefaultTable(title: String = "Ejercicio"): ExerciseTable {
        val rows = mutableStateListOf<androidx.compose.runtime.snapshots.SnapshotStateList<String>>()
        repeat(4) {
            val row = mutableStateListOf<String>()
            repeat(3) { row.add("") }
            rows.add(row)
        }
        return ExerciseTable(title = title, data = rows)
    }
}
