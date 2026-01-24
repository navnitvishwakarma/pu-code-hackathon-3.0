package com.teamrocket.conductorapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamrocket.conductorapp.ui.theme.BrandBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketSuccessScreen(
    fare: Int,
    passengers: Int,
    destination: String,
    onDone: () -> Unit
) {
    val timestamp = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF22C55E)) // Success Green Background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Success Icon Animation Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                 Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Payment Successful", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text("Ticket Issued Successfully", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
            
            Spacer(modifier = Modifier.height(32.dp))

            // Receipt Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // QR Code Placeholder
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color.White)
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp)), // Simple border to simulate QR
                        contentAlignment = Alignment.Center
                    ) {
                         Icon(Icons.Default.QrCode, null, modifier = Modifier.size(140.dp), tint = Color.Black)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("SCAN TO VERIFY", fontSize = 12.sp, color = Color.Gray, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                    
                    Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    
                    // Details
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Destination", color = Color.Gray)
                        Text(destination, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Passengers", color = Color.Gray)
                        Text("$passengers Adults", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount Paid", color = Color.Gray)
                        Text("₹$fare", fontWeight = FontWeight.Bold, color = BrandBlue, fontSize = 18.sp)
                    }
                }
                
                // Footer (Jagged edge simulation or just color block)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Transaction ID: TXN${System.currentTimeMillis().toString().takeLast(6)}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            // Done Button
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(50.dp).width(160.dp)
            ) {
                Text("DONE", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
            }
        }
    }
}
