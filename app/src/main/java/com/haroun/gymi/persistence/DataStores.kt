// app/src/main/java/com/haroun/gymi/persistence/DataStores.kt
package com.haroun.gymi.persistence

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

// Top-level singletons: never create DataStore dynamically en runtime
val Context.pushDataStore: DataStore<Preferences> by preferencesDataStore(name = "push_tables")
val Context.pullDataStore: DataStore<Preferences> by preferencesDataStore(name = "pull_tables")
val Context.legsDataStore: DataStore<Preferences> by preferencesDataStore(name = "legs_tables")
