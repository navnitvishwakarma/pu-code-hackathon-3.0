package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.stringResource
import com.teamrocket.passengerapp.R
import com.teamrocket.passengerapp.ui.components.BottomNavBar
import com.teamrocket.passengerapp.ui.components.BusStatusCard
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.teamrocket.passengerapp.ui.components.QuickActionItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.zIndex

@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRoutes: (String?) -> Unit,
    onSafetyClick: () -> Unit,
    onNavigateToTickets: (Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userName by com.teamrocket.passengerapp.utils.UserPreferences.getUserName(context).collectAsState(initial = null)
    // var searchQuery by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") } // Moved to ViewModel
    
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val dateFormat = java.text.SimpleDateFormat("EEEE, dd MMM", java.util.Locale.getDefault())
        while(true) {
            currentDate = dateFormat.format(java.util.Date())
            kotlinx.coroutines.delay(60000) // Update every minute
        }
    }

    // Fetch User Name if missing
    val userMobile by com.teamrocket.passengerapp.utils.UserPreferences.getUserMobile(context).collectAsState(initial = null)
    LaunchedEffect(userName, userMobile) {
        if (userName == null && !userMobile.isNullOrEmpty()) {
            try {
               val response = com.teamrocket.passengerapp.data.api.RetrofitClient.apiService.getUser(userMobile!!)
               if (response.success && response.user != null) {
                   com.teamrocket.passengerapp.utils.UserPreferences.saveUser(context, userMobile!!, response.user.name)
               }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        bottomBar = { 
            BottomNavBar(
                onMapClick = onNavigateToMap,
                onProfileClick = onNavigateToProfile,
                onTicketsClick = { onNavigateToTickets(0) }
            ) 
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${stringResource(R.string.good_morning)} ${userName ?: "Traveler"}",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    // Profile Image
                    Image(
                        painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuB-0XC5A_YMg2mg71CBpka8g884vq21Q5m7OoZqDSKkmDO8fB6eDyzJlCkCviTGAuRgPRgHLMJKVFXkSb_W8s0o_dq9sZhjYrO014J7rLnTNY_eKWjbbnHF62KNubGarUji7H8fgAWd4GPSWddfH0yYzj1cgKLJfk_fyPErOiIWd7Fyagrk3OM75C5y1AKlMjEYMy07OY7GqO28OsDA8vIX2xS2amXYPcDpDnFVf7XP_PIXgHM9Pt8wJZ_81EJCNXVblV2eXBL00db0"),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Search Bar & Suggestions
            item {
                val viewModel: com.teamrocket.passengerapp.ui.viewmodels.MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val suggestions by viewModel.busStandSuggestions.collectAsState()

                Box(modifier = Modifier.zIndex(1f)) {
                    Column {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text(stringResource(R.string.where_to_go)) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        
                        // Suggestion List
                        if (suggestions.isNotEmpty()) {
                            androidx.compose.material3.Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    suggestions.take(5).forEach { stand ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onNavigateToRoutes(stand.name)
                                                    viewModel.updateSearchQuery("") // Clear
                                                }
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.DirectionsBus, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(stand.name, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha=0.2f))
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Quick Actions
            item {
                Text(
                    text = stringResource(R.string.quick_actions),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Using a Row of Columns for Grid layout simulation since LazyVerticalGrid inside ScrollableColumn is tricky
                // Or simplified manual grid
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            QuickActionItem(title = stringResource(R.string.nearby_buses), subtitle = stringResource(R.string.find_stops), icon = Icons.Default.DirectionsBus, onClick = onNavigateToMap)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            QuickActionItem(title = stringResource(R.string.best_route), subtitle = stringResource(R.string.plan_trip), icon = Icons.Default.AltRoute, onClick = { onNavigateToRoutes(null) })
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            QuickActionItem(title = stringResource(R.string.safety_emergency), subtitle = stringResource(R.string.emergency_subtitle), icon = Icons.Default.Security, onClick = onSafetyClick)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            QuickActionItem(title = stringResource(R.string.my_trip), subtitle = stringResource(R.string.tickets_passes), icon = Icons.Default.ConfirmationNumber, onClick = { onNavigateToTickets(1) }) // 1 = History Tab
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Nearest Stop
            item {
                // Determine dynamic data
                val viewModel: com.teamrocket.passengerapp.ui.viewmodels.MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val activeBuses by viewModel.activeBuses.collectAsState()
                val nearestBus = activeBuses.firstOrNull() // Simplified: Just pick first active bus for now

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${stringResource(R.string.nearest_stop)}: Live Updates",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.view_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToMap() }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                if (nearestBus != null) {
                    BusStatusCard(
                        stopName = "Tracking Live",
                        busNumber = "Bus ${nearestBus.busId}",
                        timeAway = "Now",
                        occupancyStatus = "${nearestBus.currentPassengers} Passengers",
                        onClickTrack = { 
                            onNavigateToMap() 
                            // Ideally navigate and focus on this bus
                        }
                    )
                } else {
                     // Fallback or Empty State
                     androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                     ) {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text("No Active Buses Nearby", style = MaterialTheme.typography.titleMedium)
                             Text("Check back later or view map.", style = MaterialTheme.typography.bodyMedium)
                         }
                     }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Map Promo
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Gray) // Placeholder for map image
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://placeholder.pics/svg/300"),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)))
                            .padding(24.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Text(stringResource(R.string.explore_routes), style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Text(stringResource(R.string.view_full_map), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha=0.8f))
                        }
                    }
                }
            }
        }
    }
}
