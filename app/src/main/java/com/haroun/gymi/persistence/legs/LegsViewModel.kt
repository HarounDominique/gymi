package com.haroun.gymi.persistence.legs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroun.gymi.persistence.ExerciseStorage
import com.haroun.gymi.persistence.ExerciseTable
import com.haroun.gymi.persistence.ExerciseViewModel
import com.haroun.gymi.persistence.toDomain
import com.haroun.gymi.persistence.toDto
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LegsViewModel(
    private val storage: ExerciseStorage
) : ViewModel(), ExerciseViewModel {

    // Lista observable de tablas
    override val tables = mutableStateListOf<ExerciseTable>()

    init {
        // Cargar tablas desde almacenamiento al iniciar
        storage.getTables()
            .onEach { dtoList ->
                tables.clear()
                tables.addAll(dtoList.map { it.toDomain() })
            }
            .launchIn(viewModelScope)
    }

    // Persistir cambios
    private fun persist() {
        viewModelScope.launch {
            storage.saveTables(tables.map { it.toDto() })
        }
    }

    // --- ExerciseViewModel methods ---
    // Reemplaza el método updateCell en PushViewModel, PullViewModel y LegsViewModel

    override fun updateCell(tableIndex: Int, row: Int, col: Int, value: String) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]
        if (row !in table.data.indices) return
        val rowList = table.data[row]
        if (col !in rowList.indices) return
        rowList[col] = value

        // Actualizar fecha si el valor no está vacío
        if (value.isNotBlank()) {
            table.rowDates[row] = System.currentTimeMillis()
        }

        persist()
    }

    override fun addRowToTable(tableIndex: Int) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]
        val cols = 3
        val newRow = mutableStateListOf<String>()
        repeat(cols) { newRow.add("") }
        table.data.add(newRow)
        persist()
    }

    override fun addColumnToTable(tableIndex: Int) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]
        table.data.forEach { row ->
            row.add("")
        }
        persist()
    }

    // Métodos auxiliares: agregar/quitar tabla
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

    fun addCellToRow(tableIndex: Int, rowIndex: Int) {
        val table = tables.getOrNull(tableIndex) ?: return
        table.data[rowIndex].add("")
        persist()
    }

    // Crear tabla por defecto (4 filas x 3 columnas)
    fun createDefaultTable(title: String = "Ejercicio"): ExerciseTable {
        val rows = mutableStateListOf<SnapshotStateList<String>>()
        repeat(4) {
            val row = mutableStateListOf<String>()
            repeat(3) { row.add("") }
            rows.add(row)
        }
        return ExerciseTable(title = title, data = rows)
    }

    fun getTableSize(): Int = tables.size
}