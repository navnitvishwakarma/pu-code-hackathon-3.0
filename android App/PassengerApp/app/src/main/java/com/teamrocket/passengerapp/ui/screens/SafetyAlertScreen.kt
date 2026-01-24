package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SafetyAlertScreen(
    onDismiss: () -> Unit,
    onChangeBus: () -> Unit,
    onSosClick: () -> Unit,
    onShareClick: () -> Unit,
    warningText: String = "The current bus occupancy has dropped significantly. For your safety, we recommend travelling in a group or switching to a busier route."
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Full width overlay
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Warning Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFC107))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.1f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF212121))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Bus is almost empty", color = Color(0xFF212121), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Safety Alert • Low Occupancy", color = Color(0xFF212121).copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                        }
                    }

                    // Content
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = warningText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Map/Visual Placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                             // Simulating the visual from screen-7
                             Row(
                                 verticalAlignment = Alignment.CenterVertically, 
                                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                                 modifier = Modifier.background(Color.White.copy(alpha=0.5f)).padding(8.dp).clip(RoundedCornerShape(8.dp))
                             ) {
                                  Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color.Blue)
                                  Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray)
                                  Icon(Icons.Default.Groups, contentDescription = null, tint = Color.Green)
                             }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Recommendation Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.background,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(40.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Switch to Bus 502", fontWeight = FontWeight.Bold)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                                Text("High Crowd Density", fontSize = 12.sp, color = Color(0xFF16A34A))
                                            }
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                                Text("Arriving at next stop in 2 mins", fontSize = 12.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Actions
                        Button(
                            onClick = onChangeBus,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.TransferWithinAStation, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Change to Bus 502")
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("I feel safe, continue trip", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Safety Action Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp), 
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onShareClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.ShareLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Loc", fontSize = 12.sp)
                            }

                            Button(
                                onClick = onSosClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Icon(Icons.Default.Sos, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Emergency", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            // Safety Tip Footer
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Outlined.Security, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(16.dp))
                     Spacer(modifier = Modifier.width(8.dp))
                     Text("Your location is being shared with trusted contacts", color = Color.White.copy(alpha=0.8f), fontSize = 12.sp)
                 }
            }
        }
    }
}
