package com.haroun.gymi.persistence

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PushViewModel(private val storage: ExerciseStorage) : ViewModel() {

    // AHORA ES OBSERVABLE POR COMPOSE
    val tables = mutableStateListOf<ExerciseTable>()

    init {
        // Cargar desde DataStore
        viewModelScope.launch {
            storage.getTables().collectLatest { savedDtos ->
                tables.clear()
                savedDtos.forEach { dto ->
                    tables.add(dtoToTable(dto))
                }
            }
        }
    }

    fun addTable(name: String) {
        if (name.isBlank()) return

        val table = createEmptyExerciseTable(name.trim())

        tables.add(table)  // Compose se recompone inmediatamente
        persist()
    }

    fun addColumnToTable(index: Int) {
        if (index !in tables.indices) return

        val table = tables[index]

        // añadir una columna a cada fila
        table.data.forEach { row ->
            row.add("")
        }

        table.columns += 1
        persist()
    }

    fun updateCell(tableIndex: Int, rowIndex: Int, colIndex: Int, newValue: String) {
        if (tableIndex !in tables.indices) return
        val table = tables[tableIndex]

        if (rowIndex !in 0 until table.rows) return
        if (colIndex !in 0 until table.columns) return

        table.data[rowIndex][colIndex] = newValue
        persist()
    }

    private fun persist() {
        viewModelScope.launch {
            storage.saveTables(
                tables.map { tableToDto(it) }
            )
        }
    }
}
