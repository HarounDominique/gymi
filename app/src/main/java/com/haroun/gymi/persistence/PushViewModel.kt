package com.haroun.gymi.persistence

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.haroun.gymi.ExerciseTable

class PushViewModel : ViewModel() {
    // Lista de tablas (estado observable en Compose)
    var tables = mutableStateListOf<ExerciseTable>()
        private set

    fun addTable(name: String) {
        tables.add(ExerciseTable(name))
    }

    fun addColumnToTable(index: Int) {
        val table = tables[index]
        tables[index] = table.copy(columns = table.columns + 1)
    }
}
