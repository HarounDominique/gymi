// app/src/main/java/com/haroun/gymi/persistence/PushViewModelFactory.kt
package com.haroun.gymi.persistence

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PushViewModelFactory(
    private val context: Context,
    private val fileName: String // puede ser "push_tables", "pull_tables", "legs_tables" u otro
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Obtenemos el DataStore según el fileName
        val dataStore = when (fileName.lowercase()) {
            "push_tables" -> context.pushDataStore
            "pull_tables" -> context.pullDataStore
            "legs_tables" -> context.legsDataStore
            else -> throw IllegalArgumentException("Unknown fileName for DataStore: $fileName")
        }

        val storage = ExerciseStorage(dataStore)

        @Suppress("UNCHECKED_CAST")
        return PushViewModel(storage) as T
    }
}
