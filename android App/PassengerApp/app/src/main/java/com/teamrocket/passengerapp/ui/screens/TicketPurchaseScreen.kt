package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamrocket.passengerapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketPurchaseScreen(
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    busId: String,
    destination: String,
    viewModel: com.teamrocket.passengerapp.ui.viewmodels.MapViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var passengerCount by remember { mutableStateOf(1) }
    val baseFare = 15
    val totalFare = baseFare * passengerCount
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buy Ticket", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Route Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Bus 503 • AC Coach", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Manjalpur → Connaught Place", color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Departs in 3 mins", color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Passenger Counter
                Text("Select Passengers", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Adults (₹$baseFare)", fontSize = 16.sp)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(40.dp).clickable { if (passengerCount > 1) passengerCount-- }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Text(
                            text = "$passengerCount",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp).clickable { passengerCount++ }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Payment Method Mock
                Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CreditCard, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("UPI / Google Pay", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        RadioButton(selected = true, onClick = {})
                    }
                }
            }
            
            // Bottom Bar
            Column {
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total", color = Color.Gray)
                        Text("₹$totalFare", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    
                    Button(
                        onClick = {
                            viewModel.bookTicket(busId, destination) {
                                onPaymentSuccess()
                            }
                        },
                        modifier = Modifier.height(50.dp).width(200.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("PAY NOW", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
