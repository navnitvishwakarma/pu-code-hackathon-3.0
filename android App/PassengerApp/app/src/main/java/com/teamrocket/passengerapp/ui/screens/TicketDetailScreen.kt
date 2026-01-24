package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teamrocket.passengerapp.ui.components.StandardButton
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TicketDetailScreen(
    busId: String,
    destination: String,
    onBackClick: () -> Unit,
    onStartJourney: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier.size(48.dp),
                    onClick = onBackClick
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
                Text(
                    text = "Ticket Details",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.primary // Background color matches theme
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // The Ticket Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().weight(1f) // Fill most height but leave room for button
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Text("Transport Corp.", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Text("e-Ticket", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Route
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FROM", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("Current Location", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) // Simplified for demo
                            Text("10:30 AM", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        // Arrow / Bus Icon
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.DirectionsBus, null, tint = MaterialTheme.colorScheme.primary)
                            Text("Bus ${busId.takeLast(3)}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }

                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text("TO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(destination, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                            Text("11:15 AM", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.End)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(32.dp))

                    // Details Grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PASSENGERS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("1 Adult", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SEAT", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("Any Available", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DATE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("Today", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TICKET ID", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("847392", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(32.dp))

                    // QR Code Area
                    var qrCodeBitmap by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<android.graphics.Bitmap?>(null) }
                    
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                         // Generate QR for Ticket ID "847392"
                         qrCodeBitmap = com.teamrocket.passengerapp.utils.QRCodeHelper.generateQRCode("847392")
                    }

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrCodeBitmap != null) {

                            androidx.compose.foundation.Image(
                                bitmap = qrCodeBitmap!!.asImageBitmap(),
                                contentDescription = "Ticket QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Placeholder while loading
                            androidx.compose.material3.CircularProgressIndicator(color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scan to Verify", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            StandardButton(
                text = "Start Live Journey",
                onClick = onStartJourney,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.DirectionsBus,
                enabled = true
            )
        }
    }
}

@Composable
fun DashedDivider() {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = Color.LightGray,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}
