package com.teamrocket.passengerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teamrocket.passengerapp.ui.screens.HomeScreen
import com.teamrocket.passengerapp.ui.screens.LanguageSelectionScreen
import com.teamrocket.passengerapp.ui.screens.LoginScreen
import com.teamrocket.passengerapp.ui.screens.MapScreen
import com.teamrocket.passengerapp.ui.screens.ProfileScreen
import com.teamrocket.passengerapp.ui.screens.RouteSelectionScreen
import com.teamrocket.passengerapp.ui.screens.TripProgressScreen
import com.teamrocket.passengerapp.ui.screens.SafetyAlertScreen
import com.teamrocket.passengerapp.ui.screens.FeedbackScreen
import com.teamrocket.passengerapp.ui.screens.TicketDetailScreen
import com.teamrocket.passengerapp.ui.screens.TicketScreen
import com.teamrocket.passengerapp.ui.theme.PassengerAppTheme

import android.content.Context
import com.teamrocket.passengerapp.utils.LocaleManager
import kotlinx.coroutines.flow.firstOrNull
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background


class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.updateResources(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PassengerAppTheme {
                AppNavigation()
            }
        }
    }
}



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Shared ViewModel
    // Shared MapViewModel
    val mapViewModel: com.teamrocket.passengerapp.ui.viewmodels.MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    
    // Main ViewModel for Initialization
    val mainViewModel: com.teamrocket.passengerapp.ui.viewmodels.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val isLoading by mainViewModel.isLoading.collectAsState()
    val startDestination by mainViewModel.startDestination.collectAsState()

    if (isLoading) {
       // Simple Splash Screen while checking session
       androidx.compose.foundation.layout.Box(
           modifier = androidx.compose.ui.Modifier
               .fillMaxSize()
               .background(androidx.compose.material3.MaterialTheme.colorScheme.primary),
           contentAlignment = androidx.compose.ui.Alignment.Center
       ) {
            // Optional: Add Logo here
            // androidx.compose.material3.CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
       }
    } else {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("language_selection") {
                LanguageSelectionScreen(
                    onNavigateToHome = {
                        navController.navigate("login")
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    onLoginClick = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("registration")
                    }
                )
            }
            composable("registration") {
                com.teamrocket.passengerapp.ui.screens.RegistrationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegistrationSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                var showSafetyAlert by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

                HomeScreen(
                    onNavigateToMap = {
                        navController.navigate("map")
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile")
                    },
                    onNavigateToTickets = { tabIndex ->
                        navController.navigate("tickets?tab=$tabIndex")
                    },
                    onNavigateToRoutes = { dest ->
                        if (dest != null) {
                            navController.navigate("routes?destination=$dest")
                        } else {
                            navController.navigate("routes")
                        }
                    },
                    onSafetyClick = { showSafetyAlert = true }
                )

                if (showSafetyAlert) {
                    SafetyAlertScreen(
                        onDismiss = { showSafetyAlert = false },
                        onChangeBus = {
                            showSafetyAlert = false 
                            navController.navigate("map")
                        },
                        onSosClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                data = android.net.Uri.parse("tel:100")
                            }
                            context.startActivity(intent)
                        },
                        onShareClick = {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, "Safety Alert! I am using the Passenger App. My location: https://maps.google.com/?q=22.3072,73.1812")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Location via"))
                        },
                        warningText = "Emergency Assistance. Tap below to call Police or Share Location."
                    )
                }
            }
            composable(
                route = "map?destination={destination}",
                arguments = listOf(
                    androidx.navigation.navArgument("destination") {
                        defaultValue = null
                        nullable = true
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val destination = backStackEntry.arguments?.getString("destination")
                MapScreen(
                    navController = navController, 
                    initialDestination = destination,
                    viewModel = mapViewModel // Pass shared VM
                )
            }
            
            // ... (profile, tickets, ticket_detail remain same)

            composable(
                route = "routes?destination={destination}",
                arguments = listOf(
                    androidx.navigation.navArgument("destination") {
                        defaultValue = null
                        nullable = true
                        type = androidx.navigation.NavType.StringType
                    }
                ) 
            ) { backStackEntry ->
                val destination = backStackEntry.arguments?.getString("destination")
                RouteSelectionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    initialDestination = destination,
                    onViewMap = { dest ->
                        navController.navigate("map?destination=$dest")
                    },
                    onStartTrip = { busId, dest ->
                        navController.navigate("ticket_purchase?busId=$busId&destination=$dest")
                    },
                    viewModel = mapViewModel // Pass shared VM
                )
            }
            composable(
                route = "map?destination={destination}",
                arguments = listOf(
                    androidx.navigation.navArgument("destination") {
                        defaultValue = null
                        nullable = true
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val destination = backStackEntry.arguments?.getString("destination")
                MapScreen(navController = navController, initialDestination = destination)
            }
            composable("profile") {
                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogoutClick = {
                        // Navigate back to Login and clear stack
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = "tickets?tab={tab}",
                arguments = listOf(
                    androidx.navigation.navArgument("tab") {
                        defaultValue = 0
                        type = androidx.navigation.NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val tabIndex = backStackEntry.arguments?.getInt("tab") ?: 0
                TicketScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTicketClick = { ticketId ->
                        navController.navigate("ticket_detail")
                    },
                    initialTabIndex = tabIndex
                )
            }

            composable(
                route = "routes?destination={destination}",
                arguments = listOf(
                    androidx.navigation.navArgument("destination") {
                        defaultValue = null
                        nullable = true
                        type = androidx.navigation.NavType.StringType
                    }
                ) 
            ) { backStackEntry ->
                val destination = backStackEntry.arguments?.getString("destination")
                RouteSelectionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    initialDestination = destination,
                    onViewMap = { dest ->
                        navController.navigate("map?destination=$dest")
                    },
                    onStartTrip = { busId, dest ->
                        navController.navigate("ticket_purchase?busId=$busId&destination=$dest")
                    }
                )
            }
            composable(
                route = "ticket_purchase?busId={busId}&destination={destination}",
                arguments = listOf(
                    androidx.navigation.navArgument("busId") { defaultValue = "bus001" },
                    androidx.navigation.navArgument("destination") { defaultValue = "Parul University" }
                )
            ) { backStackEntry ->
                val busId = backStackEntry.arguments?.getString("busId") ?: "bus001"
                val destination = backStackEntry.arguments?.getString("destination") ?: "Parul University"
                
                com.teamrocket.passengerapp.ui.screens.TicketPurchaseScreen(
                    onNavigateBack = { navController.popBackStack() },
                    busId = busId,
                    destination = destination,
                    onPaymentSuccess = {
                        // In real app, show success screen then trip
                        navController.navigate("ticket_detail?busId=$busId&destination=$destination") {
                            popUpTo("routes") { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = "ticket_detail?busId={busId}&destination={destination}",
                arguments = listOf(
                     androidx.navigation.navArgument("busId") { defaultValue = "bus001" },
                     androidx.navigation.navArgument("destination") { defaultValue = "Connaught Place" }
                )
            ) { backStackEntry ->
                val busId = backStackEntry.arguments?.getString("busId") ?: "bus001"
                val destination = backStackEntry.arguments?.getString("destination") ?: "Connaught Place"

                TicketDetailScreen(
                    busId = busId,
                    destination = destination,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onStartJourney = {
                         navController.navigate("trip_progress?busId=$busId&destination=$destination")
                    }
                )
            }
            composable(
                route = "trip_progress?busId={busId}&destination={destination}",
                arguments = listOf(
                    androidx.navigation.navArgument("busId") { defaultValue = "bus001" },
                    androidx.navigation.navArgument("destination") { defaultValue = "Connaught Place" }
                )
            ) { backStackEntry ->
                 // State for the Safety Alert Overlay
                var showSafetyAlert by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                
                val busId = backStackEntry.arguments?.getString("busId") ?: "bus001"
                val destination = backStackEntry.arguments?.getString("destination") ?: "Connaught Place"

                TripProgressScreen(
                    busId = busId,
                    destination = destination,
                    onNavigateBack = { navController.popBackStack() },
                    onEndTrip = { navController.navigate("feedback") },
                    onEmergencyTrigger = { showSafetyAlert = true }
                )

                // Overlay Logic
                if (showSafetyAlert) {
                    SafetyAlertScreen(
                        onDismiss = { showSafetyAlert = false },
                        onChangeBus = {
                            showSafetyAlert = false
                            navController.navigate("map") { popUpTo("home") }
                        },
                        onSosClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                data = android.net.Uri.parse("tel:100")
                            }
                            context.startActivity(intent)
                        },
                        onShareClick = {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, "I am currently on Bus 402. Safety Alert Triggered. Track me: https://maps.google.com/?q=22.3072,73.1812")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Trip via"))
                        }
                    )
                }
            }
            composable("feedback") {
                FeedbackScreen(
                    onHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
