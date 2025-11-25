// app/src/main/java/com/haroun/gymi/persistence/legs/LegsViewModelFactory.kt
package com.haroun.gymi.persistence.legs

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import com.haroun.gymi.persistence.ExerciseStorage
import com.haroun.gymi.persistence.legsDataStore
import com.haroun.gymi.persistence.pullDataStore
import com.haroun.gymi.persistence.pushDataStore

class LegsViewModelFactory(
    private val context: Context,
    private val fileName: String // "legs_tables" esperado
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore: DataStore<Preferences> = when (fileName) {
            "legs_tables" -> context.legsDataStore
            "push_tables" -> context.pushDataStore
            "pull_tables" -> context.pullDataStore
            else -> throw IllegalArgumentException("Unknown fileName for DataStore: $fileName")
        }

        val storage = ExerciseStorage(dataStore)
        @Suppress("UNCHECKED_CAST")
        return LegsViewModel(storage) as T
    }
}
