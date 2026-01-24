package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.stringResource
import com.teamrocket.passengerapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trip_feedback), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onHome) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = onHome) {
                        Text(stringResource(R.string.skip))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // Hero
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuAwCrEiE5f0neHrP-jGf5m5auT7UkcmQ_d7cQPk5VaT0Nk7mKa8Bx95HEyl9AzhNV46_faSus5g0aypN2T1iOFN8_p_FNjg34s3DQUxho2WNqhgp6PIN9m5K_qJGxcodpLd6-o4-cKCfKGdo2sF8nbPsGSd4NZ9SCQyTHZF03k-FP5wo7DEINQwrp4a9s1Jhv-AraD5egfxUBb1IcubLQtlvIVH7Hmog3tmS_ZSujIsCA18gaTx9Ts2dc2jrysNQBQPi5AMLqTqtDpV"),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.arrived_at, "Majestic Stand"), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.you_arrived), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.how_was_ride, "500D"), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(stringResource(R.string.rate_experience), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rating Cards
                RatingCard(Icons.Default.DirectionsBus, stringResource(R.string.bus_condition), Color(0xFFEFF6FF), MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                RatingCard(Icons.Default.AirlineSeatReclineNormal, stringResource(R.string.driver_behavior), Color(0xFFFAF5FF), Color(0xFF9333EA))
                Spacer(modifier = Modifier.height(12.dp))
                RatingCard(Icons.Default.ConfirmationNumber, stringResource(R.string.conductor_service), Color(0xFFECFDF5), Color(0xFF059669))
                
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                
                // Safety Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.did_you_feel_safe), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SafetyButton(Icons.Default.ThumbUp, stringResource(R.string.yes), true)
                    SafetyButton(Icons.Default.ThumbDown, stringResource(R.string.no), false)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(stringResource(R.string.any_comments), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text(stringResource(R.string.comment_placeholder)) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
            
            // Submit Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background)))
                    .padding(16.dp)
            ) {
                 Button(
                     onClick = onHome,
                     modifier = Modifier.fillMaxWidth().height(56.dp),
                     shape = RoundedCornerShape(50)
                 ) {
                     Text(stringResource(R.string.submit_feedback), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                     Spacer(modifier = Modifier.width(8.dp))
                     Icon(Icons.Default.Send, contentDescription = null)
                 }
            }
        }
    }
}

@Composable
fun RatingCard(icon: ImageVector, title: String, bgColor: Color, iconColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = bgColor, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(5) {
                    Icon(
                        Icons.Default.Star, 
                        contentDescription = null, 
                        tint = if (it < 4) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.SafetyButton(icon: ImageVector, text: String, isActive: Boolean) {
    val borderColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha=0.1f) else MaterialTheme.colorScheme.surface
    val textColor = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
    
    Surface(
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(if(isActive) 2.dp else 1.dp, borderColor),
        color = bgColor,
        modifier = Modifier.weight(1f).height(50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().clickable {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = textColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}
