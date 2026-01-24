package com.teamrocket.conductorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.teamrocket.conductorapp.ui.screens.DashboardScreen
import com.teamrocket.conductorapp.ui.screens.LoginScreen
import com.teamrocket.conductorapp.ui.screens.RouteSelectionScreen
import com.teamrocket.conductorapp.ui.theme.ConductorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConductorAppTheme {
                ConductorAppNav()
            }
        }
    }
}

enum class Screen { LOGIN, ROUTE_SELECTION, DASHBOARD, TICKET_SUCCESS, SCANNER }

@Composable
fun ConductorAppNav() {
    val context = LocalContext.current
    val prefs = remember { com.teamrocket.conductorapp.utils.UserPreferences(context) }
    
    // State
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var busId by remember { mutableStateOf("") }
    var conductorName by remember { mutableStateOf("") }
    var stops by remember { mutableStateOf<List<String>>(emptyList()) }
    var routeName by remember { mutableStateOf("") }
    
    // Ticket Data Exchange
    var ticketFare by remember { mutableIntStateOf(0) }
    var ticketPassengers by remember { mutableIntStateOf(0) }
    var ticketDest by remember { mutableStateOf("") }

    // Check Persistence on Launch
    LaunchedEffect(Unit) {
        val savedBusId = prefs.getBusId()
        if (!savedBusId.isNullOrEmpty()) {
            busId = savedBusId
            routeName = prefs.getRouteName() ?: "Unknown Route"
            stops = prefs.getStops()
            currentScreen = Screen.DASHBOARD
        }
    }

    when (currentScreen) {
        Screen.LOGIN -> LoginScreen(
            onLoginSuccess = { name ->
                conductorName = name
                currentScreen = Screen.ROUTE_SELECTION
            }
        )
        Screen.ROUTE_SELECTION -> RouteSelectionScreen(
            onBusSelected = { id, routeStops, rName ->
                busId = id
                stops = routeStops
                routeName = rName
                
                // Save Persistence
                prefs.saveBusId(id)
                prefs.saveStops(routeStops)
                prefs.saveRouteName(rName)
                
                currentScreen = Screen.DASHBOARD
            }
        )
        Screen.DASHBOARD -> DashboardScreen(
            busId = busId,
            stops = stops,
            routeName = routeName,
            onIssueTicket = { fare, passengers, dest ->
                ticketFare = fare
                ticketPassengers = passengers
                ticketDest = dest
                currentScreen = Screen.TICKET_SUCCESS
            },
            onScanClick = {
                currentScreen = Screen.SCANNER
            },
            onLogout = {
                prefs.clear()
                currentScreen = Screen.LOGIN
            }
        )
        Screen.TICKET_SUCCESS -> com.teamrocket.conductorapp.ui.screens.TicketSuccessScreen(
            fare = ticketFare,
            passengers = ticketPassengers,
            destination = ticketDest,
            onDone = {
                currentScreen = Screen.DASHBOARD
            }
        )
        Screen.SCANNER -> com.teamrocket.conductorapp.ui.screens.TicketScannerScreen(
            busId = busId,
            onNavigateBack = { currentScreen = Screen.DASHBOARD }
        )
    }
}