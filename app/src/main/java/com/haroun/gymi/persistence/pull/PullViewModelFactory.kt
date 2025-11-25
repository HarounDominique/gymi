// app/src/main/java/com/haroun/gymi/persistence/pull/PullViewModelFactory.kt
package com.haroun.gymi.persistence.pull

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import com.haroun.gymi.persistence.ExerciseStorage
import com.haroun.gymi.persistence.legsDataStore
import com.haroun.gymi.persistence.pullDataStore
import com.haroun.gymi.persistence.pushDataStore

class PullViewModelFactory(
    private val context: Context,
    private val fileName: String // "pull_tables" esperado
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore: DataStore<Preferences> = when (fileName) {
            "pull_tables" -> context.pullDataStore
            "push_tables" -> context.pushDataStore
            "legs_tables" -> context.legsDataStore
            else -> throw IllegalArgumentException("Unknown fileName for DataStore: $fileName")
        }

        val storage = ExerciseStorage(dataStore)
        @Suppress("UNCHECKED_CAST")
        return PullViewModel(storage) as T
    }
}
