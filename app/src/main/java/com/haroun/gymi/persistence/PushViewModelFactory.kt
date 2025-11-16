package com.haroun.gymi.persistence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PushViewModelFactory(private val storage: ExerciseStorage) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PushViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PushViewModel(storage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
