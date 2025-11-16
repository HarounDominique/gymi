package com.haroun.gymi.persistence

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.pushDataStore by preferencesDataStore("push_tables")
val Context.pullDataStore by preferencesDataStore("pull_tables")
val Context.legsDataStore by preferencesDataStore("legs_tables")
