package com.teamrocket.conductorapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("conductor_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Bus ID
    fun saveBusId(busId: String) {
        prefs.edit().putString("bus_id", busId).apply()
    }

    fun getBusId(): String? {
        return prefs.getString("bus_id", null)
    }

    // Route ID
    fun saveRouteId(routeId: String) {
        prefs.edit().putString("route_id", routeId).apply()
    }

    fun getRouteId(): String? {
        return prefs.getString("route_id", null)
    }
    
    // Route Name
    fun saveRouteName(name: String) {
        prefs.edit().putString("route_name", name).apply()
    }

    fun getRouteName(): String? {
        return prefs.getString("route_name", null)
    }

    // Stops (List<String>)
    fun saveStops(stops: List<String>) {
        val json = gson.toJson(stops)
        prefs.edit().putString("stops", json).apply()
    }

    fun getStops(): List<String> {
        val json = prefs.getString("stops", null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
