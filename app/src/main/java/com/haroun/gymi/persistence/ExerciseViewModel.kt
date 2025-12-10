package com.haroun.gymi.persistence

interface ExerciseViewModel {
    val tables: List<ExerciseTable>
    fun deleteTable(index: Int)
    fun updateCell(tableIndex: Int, row: Int, col: Int, value: String)
    fun addRowToTable(tableIndex: Int)
    fun addColumnToTable(tableIndex: Int)

    fun renameTable(index: Int, newTitle: String) {
        if (index in tables.indices) {
            tables[index].title = newTitle.take(50)
            // Si usas StateFlow o LiveData, actualiza aquí
            // _tables.value = tables.toList() // Ejemplo si usas StateFlow
        }
    }
}
