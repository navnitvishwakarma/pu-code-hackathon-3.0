package com.teamrocket.passengerapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val USER_MOBILE_KEY = stringPreferencesKey("user_mobile")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_GENDER_KEY = stringPreferencesKey("user_gender")
    
    // Create DataStore directly
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    suspend fun saveUser(context: Context, mobile: String, name: String? = null, gender: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[USER_MOBILE_KEY] = mobile
            if (name != null) {
                preferences[USER_NAME_KEY] = name
            }
            if (gender != null) {
                preferences[USER_GENDER_KEY] = gender
            }
        }
    }

    fun getUserMobile(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_MOBILE_KEY]
        }
    }

    fun getUserName(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }

    fun getUserGender(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_GENDER_KEY]
        }
    }

    suspend fun clearUser(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
