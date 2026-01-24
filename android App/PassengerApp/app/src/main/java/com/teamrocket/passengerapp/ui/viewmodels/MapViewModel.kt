package com.teamrocket.passengerapp.ui.viewmodels

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.teamrocket.passengerapp.data.api.Bus
import com.teamrocket.passengerapp.data.api.RetrofitClient
import com.teamrocket.passengerapp.data.api.Video
import com.teamrocket.passengerapp.data.api.SmartRouteRequest
import com.teamrocket.passengerapp.data.api.LocationPoint
import com.teamrocket.passengerapp.data.api.RouteSuggestion
import com.teamrocket.passengerapp.data.api.SuggestionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeBuses = MutableStateFlow<List<Bus>>(emptyList())
    val activeBuses: StateFlow<List<Bus>> = _activeBuses.asStateFlow()

    private val _mapEvent = MutableStateFlow<MapEvent?>(null)
    val mapEvent: StateFlow<MapEvent?> = _mapEvent.asStateFlow()
    
    // Bus Stands Data
    private val _allBusStands = MutableStateFlow<List<com.teamrocket.passengerapp.data.api.BusStand>>(emptyList())
    val allBusStands: StateFlow<List<com.teamrocket.passengerapp.data.api.BusStand>> = _allBusStands.asStateFlow()
    
    // Filtered suggestions for Search
    private val _busStandSuggestions = MutableStateFlow<List<com.teamrocket.passengerapp.data.api.BusStand>>(emptyList())
    val busStandSuggestions: StateFlow<List<com.teamrocket.passengerapp.data.api.BusStand>> = _busStandSuggestions.asStateFlow()

    // Route Points (Polyline)
    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    init {
        startBusPolling()
        fetchBusStands()
    }

    private fun fetchBusStands() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getBusStands()
                if (response.success) {
                    _allBusStands.value = response.stands
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // User Location
    private var userLocation: LatLng? = null
    
    fun updateUserLocation(location: android.location.Location) {
        userLocation = LatLng(location.latitude, location.longitude)
    }

    // Helper to decode Google Polyline
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Filter suggestions locally
        if (query.isNotEmpty()) {
            _busStandSuggestions.value = _allBusStands.value.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
        } else {
            _busStandSuggestions.value = emptyList()
        }
    }

    fun searchLocation(context: Context) {
        val query = _searchQuery.value
        // Find exact match or first close match in our DB
        val match = _allBusStands.value.find { it.name.contains(query, ignoreCase = true) }
        
        if (match != null) {
             val destLatLng = LatLng(match.lat, match.lng)
             _mapEvent.value = MapEvent.MoveCamera(destLatLng, match.name)
             
             // Dynamic Routing: Nearest Stand -> Destination
             if (userLocation != null) {
                 val nearestStand = findNearestStand(userLocation!!)
                 if (nearestStand != null) {
                     val originLatLng = LatLng(nearestStand.lat, nearestStand.lng)
                     Toast.makeText(context, "Route: ${nearestStand.name} to ${match.name}", Toast.LENGTH_LONG).show()
                     fetchRealRoute(originLatLng, destLatLng)
                 } else {
                     Toast.makeText(context, "No nearby bus stand found", Toast.LENGTH_SHORT).show()
                 }
             } else {
                 Toast.makeText(context, "Waiting for user location...", Toast.LENGTH_SHORT).show()
             }
        } else {
             Toast.makeText(context, "Bus Stand not found in Vadodara network", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findNearestStand(current: LatLng): com.teamrocket.passengerapp.data.api.BusStand? {
        // Simple distance check
        return _allBusStands.value.minByOrNull { stand ->
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                current.latitude, current.longitude,
                stand.lat, stand.lng,
                results
            )
            results[0]
        }
    }
    
    private fun fetchRealRoute(origin: LatLng, dest: LatLng) {
        viewModelScope.launch {
            try {
                // Read API Key from Manifest (Hardcoded here for simplicity as reading from Manifest in ViewModel is tricky without Context)
                // In production, inject this or read properly.
                val apiKey = "AIzaSyD4Vxm_SiP4hFORseRBrajX3vwgqIxjZjI" 
                
                val originStr = "${origin.latitude},${origin.longitude}"
                val destStr = "${dest.latitude},${dest.longitude}"
                
                val response = com.teamrocket.passengerapp.data.api.DirectionsClient.apiService.getDirections(originStr, destStr, apiKey)
                
                if (response.routes.isNotEmpty()) {
                     val encodedPoints = response.routes[0].overview_polyline.points
                     val decodedPath = decodePolyline(encodedPoints)
                     _routePoints.value = decodedPath
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun onEventHandled() {
        _mapEvent.value = null
    }

    private fun startBusPolling() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = RetrofitClient.apiService.getBuses()
                    if (response.success) {
                        _activeBuses.value = response.buses
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(5000)
            }
        }
    }

    // Smart Route Suggestions
    private val _smartRouteSuggestions = MutableStateFlow<List<RouteSuggestion>>(emptyList())
    val smartRouteSuggestions: StateFlow<List<RouteSuggestion>> = _smartRouteSuggestions.asStateFlow()

    fun fetchSmartRoutes(origin: LatLng, dest: LatLng) {
        viewModelScope.launch {
            try {
                val requestBody = SmartRouteRequest(
                    origin = LocationPoint(origin.latitude, origin.longitude),
                    dest = LocationPoint(dest.latitude, dest.longitude)
                )
                val response = RetrofitClient.apiService.getSmartRoutes(requestBody)
                if (response.success) {
                    _smartRouteSuggestions.value = response.routes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    sealed class MapEvent {
        data class MoveCamera(val latLng: LatLng, val locationName: String) : MapEvent()
    }

    fun bookTicket(busId: String, destination: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.bookTicket(
                    com.teamrocket.passengerapp.data.api.BookTicketRequest(busId, destination)
                )
                if (response.success) {
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun searchRoute(source: String, dest: String, context: Context) {
        viewModelScope.launch {
            val startLatLng = if (source.equals("Current Location", ignoreCase = true) || source.equals("My Location", ignoreCase = true)) {
                 userLocation
            } else {
                 _allBusStands.value.find { it.name.contains(source, ignoreCase = true) }?.let { LatLng(it.lat, it.lng) }
            }

            val endLatLng = _allBusStands.value.find { it.name.contains(dest, ignoreCase = true) }?.let { LatLng(it.lat, it.lng) }

            if (startLatLng != null && endLatLng != null) {
                fetchSmartRoutes(startLatLng, endLatLng)
            } else {
                withContext(Dispatchers.Main) {
                    if (startLatLng == null) Toast.makeText(context, "Start location not found", Toast.LENGTH_SHORT).show()
                    if (endLatLng == null) Toast.makeText(context, "Destination not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
