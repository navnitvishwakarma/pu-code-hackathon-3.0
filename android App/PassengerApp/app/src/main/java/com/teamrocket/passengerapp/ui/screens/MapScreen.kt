package com.teamrocket.passengerapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.teamrocket.passengerapp.ui.components.BusDetailsBottomSheet
import com.teamrocket.passengerapp.ui.components.MapOverlayControls
import com.teamrocket.passengerapp.ui.viewmodels.MapViewModel
import kotlinx.coroutines.flow.StateFlow

// Helper to support Vector Drawables on Maps
// Helper to support Vector Drawables on Maps
fun bitmapDescriptorFromVector(context: android.content.Context, vectorResId: Int): com.google.android.gms.maps.model.BitmapDescriptor? {
    val vectorDrawable = androidx.core.content.ContextCompat.getDrawable(context, vectorResId) ?: return null
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = android.graphics.Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        android.graphics.Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap)
}

// Helper to draw text on vector drawable
fun bitmapDescriptorFromVectorWithText(context: android.content.Context, vectorResId: Int, text: String): com.google.android.gms.maps.model.BitmapDescriptor? {
    val vectorDrawable = androidx.core.content.ContextCompat.getDrawable(context, vectorResId) ?: return null
    
    // Config
    val textSize = 35f
    val padding = 10f
    val iconScale = 1.2f
    
    // Paint for Text
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        this.textSize = textSize
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
    }

    // Measure Text
    val textBounds = android.graphics.Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)
    val textHeight = textBounds.height()
    
    // Calculate Dimensions
    val iconWidth = (vectorDrawable.intrinsicWidth * iconScale).toInt()
    val iconHeight = (vectorDrawable.intrinsicHeight * iconScale).toInt()
    
    val totalWidth = maxOf(iconWidth, textBounds.width() + 20) // Ensure enough width for text
    val totalHeight = iconHeight + textHeight + padding.toInt() + 10 // Extra padding
    
    // Create Bitmap
    val bitmap = android.graphics.Bitmap.createBitmap(
        totalWidth,
        totalHeight,
        android.graphics.Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    
    // Draw Text at the top
    val xPos = totalWidth / 2f
    val yPos = textHeight.toFloat() + 5f // Slightly down from top edge
    
    // Optional: Draw text background for better visibility?
    // val bgPaint = android.graphics.Paint().apply { color = android.graphics.Color.WHITE; style = android.graphics.Paint.Style.FILL }
    // canvas.drawRoundRect(xPos - textBounds.width()/2 - 10, yPos - textHeight, xPos + textBounds.width()/2 + 10, yPos + 10, 10f, 10f, bgPaint)

    canvas.drawText(text, xPos, yPos, paint)
    
    // Draw Icon below text
    val iconLeft = (totalWidth - iconWidth) / 2
    val iconTop = (textHeight + padding).toInt() + 5
    
    vectorDrawable.setBounds(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight)
    vectorDrawable.draw(canvas)
    
    return com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun MapScreen(
    navController: NavController,
    initialDestination: String? = null,
    viewModel: com.teamrocket.passengerapp.ui.viewmodels.MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Default to Vadodara Central Bus Station
    val startLocation = LatLng(22.3129, 73.1812)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 14f)
    }
    
    // Observer Map Events using LaunchedEffect
    val mapEvent by viewModel.mapEvent.collectAsState()
    LaunchedEffect(mapEvent) {
        val event = mapEvent
        if (event is com.teamrocket.passengerapp.ui.viewmodels.MapViewModel.MapEvent.MoveCamera) {
            cameraPositionState.animate(
                 update = CameraUpdateFactory.newLatLngZoom(event.latLng, 15f),
                 durationMs = 1000
            )
             android.widget.Toast.makeText(context, "Found: ${event.locationName}", android.widget.Toast.LENGTH_SHORT).show()
             viewModel.onEventHandled()
        }
    }
    // Auto-Search if destination provided from RouteSelection
    LaunchedEffect(initialDestination) {
        if (!initialDestination.isNullOrEmpty()) {
             viewModel.updateSearchQuery(initialDestination)
             viewModel.searchLocation(context)
        }
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }



    var isLocationPermissionGranted by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isLocationPermissionGranted = isGranted
    }
    
    // Function to move camera to user location
    fun moveToUserLocation() {
        // Removed local variable that shadowed the state
        if (isLocationPermissionGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.updateUserLocation(location) // Sync to ViewModel
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        scope.launch {
                             cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f),
                                durationMs = 1000
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {
                // Handle exception
            }
        }
    }




    // Request Permission on Start
    LaunchedEffect(Unit) {
        if (!isLocationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // If already granted, move camera immediately
            moveToUserLocation()
        }
    }

    // Effect to react to permission grant
    LaunchedEffect(isLocationPermissionGranted) {
        if (isLocationPermissionGranted) {
            moveToUserLocation()
        }
    }

    // State for selected bus (to show details)
    var selectedBus by remember { mutableStateOf<com.teamrocket.passengerapp.data.api.Bus?>(null) }
    var showFullDetails by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            
            // 1. Full Screen Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = isLocationPermissionGranted,
                    mapStyleOptions = com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle(context, com.teamrocket.passengerapp.R.raw.map_style)
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false // Disable native button to use custom one
                ),
                onMapClick = { 
                    selectedBus = null 
                    showFullDetails = false
                }
            ) {
                // User Location Marker
                // User Location Marker - Removed in favor of blue dot (isMyLocationEnabled)
                /*
                Marker(
                    state = MarkerState(position = startLocation),
                    title = "You",
                    snippet = "Your Location"
                )
                */
                
                // Live Bus Marker (Real-time from Backend)
                // Observed from ViewModel
                val activeBuses by viewModel.activeBuses.collectAsState()

                // Draw Markers for each active bus
                activeBuses.forEach { bus ->
                    Marker(
                        state = MarkerState(position = LatLng(bus.lat, bus.lng)),
                        title = "Bus ${bus.busId}",
                        snippet = "Count: ${bus.currentPassengers}",
                        icon = bitmapDescriptorFromVectorWithText(context, com.teamrocket.passengerapp.R.drawable.ic_bus, bus.currentPassengers.toString()),
                        onClick = {
                            selectedBus = bus
                            showFullDetails = false // Reset full details when selecting new bus
                            true // Consume click
                        }
                    )
                }
                
                // Draw Markers for Bus Stands
                val busStands by viewModel.allBusStands.collectAsState()
                busStands.forEach { stand ->
                    Marker(
                        state = MarkerState(position = LatLng(stand.lat, stand.lng)),
                        title = stand.name,
                        icon = bitmapDescriptorFromVector(context, com.teamrocket.passengerapp.R.drawable.ic_bus_stand_custom)
                    )
                }

                // Draw Route Polyline
                val routePoints by viewModel.routePoints.collectAsState()
                if (routePoints.isNotEmpty()) {
                    com.google.maps.android.compose.Polyline(
                        points = routePoints,
                        color = com.teamrocket.passengerapp.ui.theme.PrimaryBlue,
                        width = 12f
                    )
                }
            }

            // 2. Top Bar (Search + Suggestions)
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 24.dp) // Status bar padding
                    .align(Alignment.TopCenter)
            ) {
                 val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

                 Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     // Back Button
                     Surface(
                         shape = CircleShape, 
                         shadowElevation = 4.dp,
                         color = MaterialTheme.colorScheme.surface
                     ) {
                         IconButton(onClick = { navController.popBackStack() }) {
                             Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                         }
                     }
                     
                     Spacer(modifier = Modifier.width(12.dp))
                     
                     // Search Box
                     val searchQuery by viewModel.searchQuery.collectAsState()
                     val context = LocalContext.current
                     
                     TextField(
                         value = searchQuery,
                         onValueChange = { viewModel.updateSearchQuery(it) },
                         placeholder = { Text("Search bus stand", color = Color.Gray) },
                         leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                         keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                         keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                             onSearch = {
                                 keyboardController?.hide()
                                 viewModel.searchLocation(context)
                             }
                         ),
                         colors = TextFieldDefaults.colors(
                             focusedContainerColor = MaterialTheme.colorScheme.surface,
                             unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                             disabledContainerColor = MaterialTheme.colorScheme.surface,
                             focusedIndicatorColor = Color.Transparent,
                             unfocusedIndicatorColor = Color.Transparent,
                         ),
                         shape = RoundedCornerShape(50),
                         modifier = Modifier.weight(1f).height(56.dp)
                     )

                     Spacer(modifier = Modifier.width(12.dp))
                     
                     // Menu Button
                     Surface(
                         shape = CircleShape, 
                         shadowElevation = 4.dp,
                         color = MaterialTheme.colorScheme.surface
                     ) {
                         IconButton(onClick = { /* Menu */ }) {
                             Icon(Icons.Default.Menu, contentDescription = "Menu")
                         }
                     }
                 }
                 
                 // Suggestions List
                 val suggestions by viewModel.busStandSuggestions.collectAsState()
                 if (suggestions.isNotEmpty()) {
                     Spacer(modifier = Modifier.height(8.dp))
                     androidx.compose.foundation.lazy.LazyColumn(
                         modifier = Modifier
                             .fillMaxWidth()
                             .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                             .padding(vertical = 8.dp)
                     ) {
                         items(suggestions.size) { index ->
                             val stand = suggestions[index]
                             Row(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .clickable {
                                         viewModel.updateSearchQuery(stand.name)
                                         viewModel.searchLocation(context)
                                         keyboardController?.hide()
                                     }
                                     .padding(16.dp),
                                 verticalAlignment = Alignment.CenterVertically
                             ) {
                                  Icon(
                                      modifier = Modifier.width(24.dp).height(24.dp),
                                      imageVector = androidx.compose.material.icons.Icons.Default.Place, // Or generic pin
                                      contentDescription = null, 
                                      tint = Color.Gray
                                  )
                                  Spacer(modifier = Modifier.width(12.dp))
                                  Text(
                                      text = stand.name, 
                                      style = MaterialTheme.typography.bodyLarge
                                  )
                             }
                         }
                     }
                 }
            }

            // 3. Right Side Controls
            MapOverlayControls(
                onZoomIn = { 
                    scope.launch {
                        cameraPositionState.animate(com.google.android.gms.maps.CameraUpdateFactory.zoomIn())
                    }
                },
                onZoomOut = { 
                    scope.launch {
                        cameraPositionState.animate(com.google.android.gms.maps.CameraUpdateFactory.zoomOut())
                    }
                },
                onMyLocation = { 
                    moveToUserLocation()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )

            // 4. Bottom Sheet (Bus Details)
            AnimatedVisibility(
                visible = selectedBus != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                selectedBus?.let { bus ->
                    BusDetailsBottomSheet(
                        busNumber = "Bus ${bus.busId}",
                        towards = "Updates Live", 
                        crowdLevel = "${bus.currentPassengers} Passengers", 
                        arrivalTime = "Now",
                        onViewDetails = { 
                            showFullDetails = true 
                        }
                    )
                }
            }
            
            // 5. Gender Breakdown Dialog
             if (showFullDetails && selectedBus != null) {
                 val bus = selectedBus!!
                 com.teamrocket.passengerapp.ui.components.BusOccupancyDialog(
                     busNumber = "Bus ${bus.busId}",
                     totalPassengers = bus.currentPassengers,
                     femaleCount = bus.femaleCount,
                     onDismiss = { showFullDetails = false }
                 )
             }
        }
    }
}


