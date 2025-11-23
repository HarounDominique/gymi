// app/src/main/java/com/haroun/gymi/persistence/ExerciseStorage.kt
package com.haroun.gymi.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val TABLES_KEY = stringPreferencesKey("tables_json")

/**
 * Storage thin wrapper that stores a list of ExerciseTableDto as JSON under a single preference key.
 * Constructor receives a DataStore<Preferences> singleton (one per section).
 */
class ExerciseStorage(
    private val dataStore: DataStore<Preferences>,
    private val json: Json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
) {

    fun getTables(): Flow<List<ExerciseTableDto>> {
        return dataStore.data
            .map { prefs ->
                val jsonStr = prefs[TABLES_KEY]
                if (jsonStr.isNullOrBlank()) emptyList()
                else try {
                    json.decodeFromString(jsonStr)
                } catch (t: Throwable) {
                    // fallback: clear corrupt data by returning empty list (you can log)
                    emptyList()
                }
            }
    }

    suspend fun saveTables(tables: List<ExerciseTableDto>) {
        val jsonStr = json.encodeToString(tables)
        dataStore.edit { prefs ->
            prefs[TABLES_KEY] = jsonStr
        }
    }
}
