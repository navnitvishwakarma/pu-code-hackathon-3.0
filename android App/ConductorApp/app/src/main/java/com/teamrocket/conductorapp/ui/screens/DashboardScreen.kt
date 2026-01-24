package com.teamrocket.conductorapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamrocket.conductorapp.data.api.CrowdRequest
import com.teamrocket.conductorapp.data.api.RetrofitClient
import com.teamrocket.conductorapp.ui.theme.BrandBlue
import com.teamrocket.conductorapp.ui.theme.WarningOrange
import kotlinx.coroutines.launch



@Composable

fun DashboardScreen(
    busId: String,
    stops: List<String>,
    routeName: String,
    onIssueTicket: (Int, Int, String) -> Unit,
    onScanClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Fallback if empty
    val safeStops = if (stops.isNotEmpty()) stops else listOf("Stop A", "Stop B")

    // Ticket State
    // var passengerCount by remember { mutableStateOf(1) } // Removed
    var bookingMales by remember { mutableStateOf(1) }
    var bookingFemales by remember { mutableStateOf(0) }
    val passengerCount = bookingMales + bookingFemales

    var selectedSource by remember { mutableStateOf(safeStops.first()) }
    var selectedDestination by remember { mutableStateOf(safeStops.last()) }
    
    // Fare Calculation (Mock: Distance based on index difference)
    val sourceIndex = safeStops.indexOf(selectedSource)
    val destIndex = safeStops.indexOf(selectedDestination)
    val distance = kotlin.math.abs(destIndex - sourceIndex) * 3 // Mock 3km per stop
    val farePerPerson = kotlin.math.max(10, distance * 2) 
    val totalFare = passengerCount * farePerPerson
    
    // Crowd State (Total Occupancy)
    var totalOccupancy by remember { mutableStateOf(0) } 
    var totalMales by remember { mutableStateOf(0) }
    var totalFemales by remember { mutableStateOf(0) }
    var crowdLevel by remember { mutableStateOf("Low") }
    
    // Auto-calculate level based on occupancy
    LaunchedEffect(totalOccupancy) {
        crowdLevel = when {
            totalOccupancy < 30 -> "Low"
            totalOccupancy < 50 -> "Medium"
            else -> "High"
        }
    }

    // INITIAL SYNC: Fetch current server state
    LaunchedEffect(busId) {
        try {
            val response = RetrofitClient.apiService.getBusDetails(busId)
            if (response.success && response.bus != null) {
                totalOccupancy = response.bus.currentPassengers
                totalMales = response.bus.maleCount
                totalFemales = response.bus.femaleCount
                crowdLevel = response.bus.crowdLevel
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
             // ... [TopBar remains same] ...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(BrandBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsBus, null, tint = BrandBlue)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Bus $busId", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Route $routeName • Moving", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Default.Settings, null, tint = Color.Gray)
                }
            }
            */
            // Settings Icon with Dropdown
                Box {
                    var showMenu by remember { mutableStateOf(false) }
                    
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Settings, null, tint = Color.Gray)
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = { 
                                showMenu = false
                                onLogout() 
                            }
                        )
                    }
                }
            }
        },

        bottomBar = {
            // Issue Ticket Bar (Updated to use new Source/Dest logic)
             Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        // 1. Update Local State
                        val newTotal = totalOccupancy + passengerCount
                        val newMales = totalMales + bookingMales
                        val newFemales = totalFemales + bookingFemales
                        
                        totalOccupancy = newTotal
                        totalMales = newMales
                        totalFemales = newFemales
                        
                        // 2. Sync with Backend
                        scope.launch {
                             try {
                                 RetrofitClient.apiService.updateCrowd(
                                     CrowdRequest(
                                         busId = busId, 
                                         passengerCount = newTotal,
                                         maleCount = newMales,
                                         femaleCount = newFemales
                                     )
                                 )
                             } catch(e: Exception) {
                                 e.printStackTrace()
                             }
                        }

                        // 3. Navigate (Pass total for now)
                        onIssueTicket(totalFare, passengerCount, selectedDestination)
                        
                        // Reset
                        bookingMales = 1 
                        bookingFemales = 0
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ISSUE TICKET", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("₹$totalFare", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, null)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F6F8)) // Light bg
                .padding(16.dp)
        ) {
            // ... [Occupancy Card remains same] ...
            // Occupancy Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("$crowdLevel Load", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("$totalOccupancy passengers on board", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text(
                            text = "$totalOccupancy/60",
                            fontWeight = FontWeight.Bold, 
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Progress Bar
                    val progress = when(crowdLevel) {
                        "Low" -> 0.25f
                        "Medium" -> 0.75f
                        else -> 0.95f
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if(crowdLevel == "High") Color.Red else if(crowdLevel == "Medium") WarningOrange else Color.Green,
                        trackColor = Color(0xFFE5E7EB),
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Route Selection UI
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Journey Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                // Scan Button moved here
                TextButton(onClick = onScanClick) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scan Ticket", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.Bold)
                }
            }
            
            // Source Dropdown
            SelectionDropdown(
                label = "Boarding From",
                options = safeStops,
                selectedOption = selectedSource,
                onOptionSelected = { selectedSource = it },
                icon = Icons.Default.DirectionsBus
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Destination Dropdown
            SelectionDropdown(
                label = "Dropping At",
                options = safeStops,
                selectedOption = selectedDestination,
                onOptionSelected = { selectedDestination = it },
                icon = Icons.Default.DirectionsBus
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Current Fare Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandBlue.copy(alpha = 0.05f))
                    .border(1.dp, BrandBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTAL FARE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("₹$totalFare", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue)
                    Text("$passengerCount x ₹$farePerPerson", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ticket Counters
            Text("Select Passengers", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            
            // Male Counter
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Male", style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (bookingMales > 0) bookingMales-- }) {
                        Icon(Icons.Filled.RemoveCircle, null, tint = Color.Gray)
                    }
                    Text("$bookingMales", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = { bookingMales++ }) {
                        Icon(Icons.Default.Add, null, tint = BrandBlue)
                    }
                }
            }

            // Female Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Female", style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (bookingFemales > 0) bookingFemales-- }) {
                        Icon(Icons.Filled.RemoveCircle, null, tint = Color.Gray)
                    }
                    Text("$bookingFemales", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = { bookingFemales++ }) {
                        Icon(Icons.Default.Add, null, tint = BrandBlue)
                    }
                }
            }
        }
    }
}
