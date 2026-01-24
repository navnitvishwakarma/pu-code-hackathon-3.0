package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.teamrocket.passengerapp.data.api.RouteSuggestion
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.ui.res.stringResource
import com.teamrocket.passengerapp.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
    onNavigateBack: () -> Unit,
    onStartTrip: (String, String) -> Unit, // busId, destination
    onViewMap: (String) -> Unit, // New callback
    initialDestination: String? = null,
    viewModel: com.teamrocket.passengerapp.ui.viewmodels.MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val suggestions by viewModel.smartRouteSuggestions.collectAsState()
    
    // Location Logic
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context) }
    var isLocationPermissionGranted by remember { 
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isLocationPermissionGranted = isGranted
        if (isGranted) {
             try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) viewModel.updateUserLocation(location)
                }
            } catch (e: SecurityException) {}
        }
    }
    
    LaunchedEffect(Unit) {
        if (!isLocationPermissionGranted) {
             permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) viewModel.updateUserLocation(location)
                }
            } catch (e: SecurityException) {}
        }
    }

    // Trigger initial search
    LaunchedEffect(initialDestination) {
        if (!initialDestination.isNullOrEmpty()) {
             // Give some time for BusStands to load in ViewModel
             kotlinx.coroutines.delay(1000) 
             viewModel.searchRoute("Current Location", initialDestination, context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.select_route),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Journey Planner Inputs (Source & Dest)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    
                    var searchSource by remember { mutableStateOf("Current Location") }
                    var searchDest by remember { mutableStateOf(initialDestination ?: "") }

                    // Source Input
                    OutlinedTextField(
                        value = searchSource,
                        onValueChange = { searchSource = it },
                        label = { Text(stringResource(R.string.boarding_point)) },
                        leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Destination Input
                    OutlinedTextField(
                        value = searchDest,
                        onValueChange = { searchDest = it },
                        label = { Text(stringResource(R.string.destination_point)) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { 
                            viewModel.searchRoute(searchSource, searchDest, context = context)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Search Buses", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Route List
            if (suggestions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Smart Suggestion Text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Smart Suggestions", 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // View on Map Button
                        Text(
                            text = "View on Map",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .clickable { 
                                    onViewMap(initialDestination ?: "Connaught Place") 
                                }
                                .padding(8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Best Choice (First Item)
                    suggestions.firstOrNull { it.isBestChoice }?.let { best ->
                        BestChoiceCard(
                            data = best,
                            onStartTrip = { onStartTrip(best.busId, initialDestination ?: "Parul University") }
                        )
                    }

                    // Other Options Section
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.other_options), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.view_all), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }

                        // Remaining Options
                        suggestions.filter { !it.isBestChoice }.forEach { route ->
                            
                            // Female Count Badge Logic
                            // Ideally, fetch real user gender preference. For now, we fetch from context
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val userGenderState = androidx.compose.runtime.produceState(initialValue = "Male") {
                                com.teamrocket.passengerapp.utils.UserPreferences.getUserGender(context).collect { gender ->
                                    value = gender ?: "Male"
                                }
                            }
                            
                            val dynamicBadges = route.badges.map { badgeText ->
                                when(badgeText) {
                                    "Low Crowd" -> BadgeData("${route.currentPassengers} Psngrs", Color(0xFF16A34A), Color(0xFFDCFCE7), Icons.Default.Groups) 
                                    "Medium Crowd" -> BadgeData("${route.currentPassengers} Psngrs", Color(0xFFCA8A04), Color(0xFFFEF9C3), Icons.Default.Groups)
                                    "High Crowd" -> BadgeData("${route.currentPassengers} Psngrs", Color(0xFFDC2626), Color(0xFFFEE2E2), Icons.Default.Groups)
                                    "Fastest" -> BadgeData(badgeText, Color(0xFF15803D), Color(0xFFDCFCE7), Icons.Default.Bolt)
                                    "Arriving Soon" -> BadgeData(badgeText, Color(0xFFCA8A04), Color(0xFFFEF9C3), Icons.Default.Schedule)
                                    else -> BadgeData(badgeText, Color.Gray, Color.LightGray, Icons.Default.Info)
                                }
                            }.toMutableList()

                            // Add Female Badge if User is Female
                            if (userGenderState.value.equals("Female", ignoreCase = true) && route.femaleCount > 0) {
                                dynamicBadges.add(0, BadgeData("${route.femaleCount} Females", Color(0xFFEC4899), Color(0xFFFCE7F3), Icons.Default.Female)) // Pink
                            }

                            RouteOptionCard(
                                busName = "Bus ${route.busId.takeLast(3)}",
                                isVerified = true,
                                duration = route.duration,
                                leavesIn = "Leaves in ${route.eta}",
                                badges = dynamicBadges,
                                onBuyClick = { onStartTrip(route.busId, initialDestination ?: "Parul University") }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

data class BadgeData(val text: String, val textColor: Color, val bgColor: Color, val icon: ImageVector)

@Composable

fun BestChoiceCard(data: RouteSuggestion, onStartTrip: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuDeOir5C6zua4mDGgb5nOhlbgqvmBlswsjaTAeMHRhQgBH2gQRcsB5T6tHSSBdufElcbtMCkyrLlZkEbOd2f3z24hNHIuZKVzVOw4rjnDSXoTk0PAdtF8brY2ljCQbEBwSkzV4kKaWxcnrHsI-77NSS-lI9eQMN-Y8ZPUAHDknVXCTtq03wSeoMewCtfRTylXehiVUthCGML9tsiplUx36JJx3N8xIl-fz7LWx2YOwVoZcCIv0_JQrv3F2KvGxIGZ-_2LKOUyMKQB3y"),
                    contentDescription = "Route Map",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))))
                )
                // Badges
                Row(modifier = Modifier.padding(12.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.best_choice), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(14.dp)) // Green
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.fastest), style = MaterialTheme.typography.labelSmall, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Info Body
            Column(modifier = Modifier.padding(20.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Bus ${data.busId.takeLast(3)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                Text("AC", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Via Patel Nagar Main Road", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(data.duration, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("Leaves in ${data.eta}", style = MaterialTheme.typography.labelMedium, color = Color(0xFF16A34A)) // Green
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray)

                // Details Grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FeatureItem(Icons.Default.Group, "${data.currentPassengers} Passengers", if(data.crowdLevel == "Low") Color(0xFF16A34A) else Color(0xFFCA8A04))
                        FeatureItem(Icons.Default.AltRoute, "Direct Route", Color.Gray)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FeatureItem(Icons.Default.VerifiedUser, "Safety Verified", MaterialTheme.colorScheme.primary)
                        FeatureItem(Icons.Default.AirlineSeatReclineNormal, "Seats Available", Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onStartTrip,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Buy Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}



    @Composable
fun FeatureItem(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(140.dp)) { // Fixed width for alignment
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RouteOptionCard(
    busName: String,
    isVerified: Boolean,
    subtext: String? = null,
    duration: String,
    leavesIn: String,
    icon: ImageVector = Icons.Default.DirectionsBus,
    badges: List<BadgeData>,
    opacity: Float = 1f,
    onBuyClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(busName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isVerified) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text(stringResource(R.string.verified_label), style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                            } else {
                                if (subtext != null) {
                                    Text(subtext, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                } else {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                    Text(stringResource(R.string.regular_label), style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(duration, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(leavesIn, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { badge ->
                    Surface(
                        color = badge.bgColor,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, badge.textColor.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(badge.icon, contentDescription = null, tint = badge.textColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(badge.text, color = badge.textColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBuyClick,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Buy Ticket", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}




