package com.teamrocket.passengerapp.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {
    private const val PREF_NAME = "language_pref"
    private const val KEY_LANGUAGE = "key_language"

    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }

    fun getSavedLanguage(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, null)
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun updateResources(context: Context, languageCode: String? = null): Context {
        val lang = languageCode ?: getSavedLanguage(context) ?: "en"
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun getLanguageCode(displayName: String): String {
        return when (displayName) {
            "Hindi" -> "hi"
            "Gujarati" -> "gu"
            else -> "en"
        }
    }
}
