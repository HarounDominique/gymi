package com.haroun.gymi.persistence

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ExerciseStorage(
    private val dataStore: androidx.datastore.core.DataStore<Preferences>
) {

    private val EXERCISES_KEY = stringPreferencesKey("exercise_tables")

    suspend fun saveTables(tables: List<ExerciseTableDto>) {
        dataStore.edit { prefs ->
            prefs[EXERCISES_KEY] = Json.encodeToString(tables)
        }
    }

    fun getTables(): Flow<List<ExerciseTableDto>> {
        return dataStore.data.map { prefs: Preferences ->
            val json = prefs[EXERCISES_KEY]
            if (json != null) {
                runCatching { Json.decodeFromString<List<ExerciseTableDto>>(json) }
                    .getOrDefault(emptyList())
            } else emptyList()
        }
    }
}
