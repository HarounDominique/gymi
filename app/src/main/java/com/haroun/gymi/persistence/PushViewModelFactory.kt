package com.haroun.gymi.persistence

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PushViewModelFactory(
    private val context: Context,
    private val fileName: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dataStore = when (fileName) {
            "push_tables" -> context.pushDataStore
            "pull_tables" -> context.pullDataStore
            "legs_tables" -> context.legsDataStore
            else -> throw IllegalArgumentException("Unknown file")
        }

        return PushViewModel(ExerciseStorage(dataStore)) as T
    }
}
