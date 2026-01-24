package com.teamrocket.conductorapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamrocket.conductorapp.ui.components.PrimaryButton
import com.teamrocket.conductorapp.ui.theme.BrandBlue
import com.teamrocket.conductorapp.ui.theme.SuccessGreen
import com.teamrocket.conductorapp.data.api.RetrofitClient
import com.teamrocket.conductorapp.data.api.RouteInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
    onBusSelected: (String, List<String>, String) -> Unit
) {
    var selectedBus by remember { mutableStateOf("") }
    var selectedRoute by remember { mutableStateOf("") }
    var routes by remember { mutableStateOf<List<RouteInfo>>(emptyList()) }
    var buses by remember { mutableStateOf<List<String>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Fetch Routes
                val routeResponse = RetrofitClient.apiService.getRoutes()
                if (routeResponse.success) {
                    routes = routeResponse.routes
                }
                
                // Fetch Buses
                val busResponse = RetrofitClient.apiService.getBuses()
                if (busResponse.success) {
                    buses = busResponse.buses
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Start Trip", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {}) { // No back action for now
                         Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BrandBlue
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 16.dp)
            ) {
                 PrimaryButton(
                    text = "Start Duty",
                    color = SuccessGreen,
                    onClick = {
                        if (selectedBus.isNotEmpty() && selectedRoute.isNotEmpty()) {
                            val route = routes.find { "${it.routeId} (${it.name})" == selectedRoute }
                            val stops = route?.checkpoints?.mapNotNull { it.name } ?: emptyList()
                            val routeName = route?.routeId ?: "Unknown"
                            onBusSelected(selectedBus, stops, routeName)
                        } else {
                            Toast.makeText(context, "Select Bus & Route", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Today", color = Color.Gray, fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ID: 8821-C", color = Color.Gray, fontSize = 14.sp)
                }
            }
            
            // Headline
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Configure Route",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Select your vehicle and route details.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Dropdowns
            SelectionDropdown(
                label = "Bus Number",
                options = buses.ifEmpty { listOf("Loading...") },
                selectedOption = selectedBus,
                onOptionSelected = { selectedBus = it },
                icon = Icons.Default.DirectionsBus
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SelectionDropdown(
                label = "Route",
                options = routes.map { "${it.routeId} (${it.name})" },
                selectedOption = selectedRoute,
                onOptionSelected = { selectedRoute = it },
                icon = Icons.Default.AltRoute
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Confirmation Card
            if (selectedRoute.isNotEmpty()) {
                RouteConfirmationCard(routes, selectedRoute)
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Scroll buffer
        }
    }
}

@Composable
fun SelectionDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = BrandBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .border(2.dp, Color(0xFFDBDFE6), RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedOption.isEmpty()) "Select $label" else selectedOption,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedOption.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onBackground
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RouteConfirmationCard(routes: List<RouteInfo> = emptyList(), selectedRoute: String = "") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ROUTE CONFIRMED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row {
            Column(modifier = Modifier.weight(1f)) {
                val routeInfo = routes.find { "${it.routeId} (${it.name})" == selectedRoute }
                if (routeInfo != null) {
                    Text("FROM", fontSize = 10.sp, color = Color.Gray)
                    Text(routeInfo.source, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("TO", fontSize = 10.sp, color = Color.Gray)
                    Text(routeInfo.destination, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                // Placeholder for Map Map
                Text("Map Preview", modifier = Modifier.align(Alignment.Center), fontSize = 10.sp)
            }
        }
    }
}
