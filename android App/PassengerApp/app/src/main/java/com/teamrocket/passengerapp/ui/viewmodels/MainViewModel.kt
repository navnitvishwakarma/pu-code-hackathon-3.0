package com.teamrocket.passengerapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.teamrocket.passengerapp.utils.LocaleManager
import com.teamrocket.passengerapp.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow("language_selection")
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        initializeSession()
    }

    private fun initializeSession() {
        viewModelScope.launch {
            try {
                // Safety timeout 3 seconds to prevent white screen hangs
                withTimeoutOrNull(3000L) {
                    val context = getApplication<Application>().applicationContext
                    
                    val savedLanguage = LocaleManager.getSavedLanguage(context)
                    val userMobile = UserPreferences.getUserMobile(context).firstOrNull()
// 
                    val dest = if (savedLanguage == null) {
                        "language_selection"
                    } else if (!userMobile.isNullOrEmpty()) {
                        "home"
                    } else {
                        "login"
                    }
                    _startDestination.value = dest
                }
            } catch (e: Exception) {
                // Fallback destination is already set to language_selection
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
