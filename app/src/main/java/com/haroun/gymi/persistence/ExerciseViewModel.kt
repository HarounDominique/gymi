package com.haroun.gymi.persistence

interface ExerciseViewModel {
    val tables: List<ExerciseTable>
    fun deleteTable(index: Int)
    fun updateCell(tableIndex: Int, row: Int, col: Int, value: String)
    fun addRowToTable(tableIndex: Int)
    fun addColumnToTable(tableIndex: Int)
}
