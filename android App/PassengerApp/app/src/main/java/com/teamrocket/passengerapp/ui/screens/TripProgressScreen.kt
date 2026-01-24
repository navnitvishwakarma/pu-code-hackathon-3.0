package com.teamrocket.passengerapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import com.teamrocket.passengerapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripProgressScreen(
    busId: String,
    destination: String,
    onNavigateBack: () -> Unit,
    onEndTrip: () -> Unit,
    onEmergencyTrigger: () -> Unit // Simulate the safety alert trigger
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Bus ${busId.takeLast(3)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "To $destination",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = onEndTrip,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Color.Red),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(stringResource(R.string.end_trip), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Status
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.next_stop), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("Central Park", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.minutes_short, "8"), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                // Status Chips
                // Demo Logic: Check Gender & Female Count
                val context = LocalContext.current
                var showSafetyAlert by remember { mutableStateOf(false) }
                val userGenderState = androidx.compose.runtime.produceState(initialValue = "Male") {
                    com.teamrocket.passengerapp.utils.UserPreferences.getUserGender(context).collect { gender ->
                        value = gender ?: "Male"
                    }
                }

                // SIMULATION: If User is Female, trigger alert after 3 seconds (Mocking live data update)
                LaunchedEffect(userGenderState.value) {
                    if (userGenderState.value.equals("Female", ignoreCase = true)) {
                        delay(3000) // Simulate data fetch delay
                        // Mock: Bus female count drops to 3
                        showSafetyAlert = true 
                    }
                }

                if (showSafetyAlert) {
                    SafetyAlertScreen(
                        onDismiss = { showSafetyAlert = false },
                        onChangeBus = { /* Handle Bus Change */ },
                        onSosClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                data = android.net.Uri.parse("tel:100")
                            }
                            context.startActivity(intent)
                        },
                        onShareClick = {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, "I am currently on Bus 402. High Priority Safety Alert! Track me: https://maps.google.com/?q=22.3072,73.1812")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Trip via"))
                        },
                        warningText = "The number of female passengers has dropped below 5. We recommend sharing your live location or switching to a busier route."
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusChip(Icons.Default.Groups, "Moderate Crowd", Color(0xFFD97706), Color(0xFFFFFBEB))
                    StatusChip(Icons.Default.VerifiedUser, "Route Safe", Color(0xFF059669), Color(0xFFECFDF5))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(24.dp))

                // Timeline Header
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Icon(Icons.Default.AltRoute, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.live_journey), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.weight(1f))
                    // Hidden trigger for demo
                    TextButton(onClick = onEmergencyTrigger) {
                        Text(stringResource(R.string.simulate_empty_bus), fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Timeline
                TimelineItem(
                    title = "Shivaji Stadium",
                    subtitle = "Departed 10:15 AM",
                    status = TimelineStatus.PASSED,
                    isFirst = true
                )
                TimelineItem(
                    title = stringResource(R.string.en_route_to, "Central Park"),
                    subtitle = stringResource(R.string.arriving_in, "8m"),
                    status = TimelineStatus.CURRENT,
                    isLive = true
                )
                TimelineItem(
                    title = "Central Park",
                    subtitle = stringResource(R.string.next_stop),
                    status = TimelineStatus.NEXT
                )
                TimelineItem(
                    title = "Barakhamba Road",
                    subtitle = "Est. 10:35 AM",
                    status = TimelineStatus.FUTURE,
                    isLast = true
                )
                
                Spacer(modifier = Modifier.height(100.dp)) // Spacing for bottom bar
            }

            // Bottom Floating Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Gradient Scrim
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                            )
                        )
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val context = LocalContext.current
                    
                    Button(
                        onClick = { 
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, "I am currently on Bus 402 (Route 1A). Track my live journey here: https://maps.google.com/?q=22.3072,73.1812")
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Trip via"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Icon(Icons.Default.ShareLocation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.share_live_trip), fontWeight = FontWeight.Bold)
                    }
                    
                    // SOS Button
                    FilledIconButton(
                        onClick = { 
                            val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                data = android.net.Uri.parse("tel:100")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = CircleShape
                        // Border added manually below via modifier if needed, or use OutlinedIconButton logic
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(2.dp, Color(0xFFFECACA), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Sos, contentDescription = "SOS", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(icon: ImageVector, text: String, textColor: Color, bgColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = textColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

enum class TimelineStatus { PASSED, CURRENT, NEXT, FUTURE }

@Composable
fun TimelineItem(
    title: String,
    subtitle: String,
    status: TimelineStatus,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    isLive: Boolean = false
) {
    IntrinsicHeightRow(modifier = Modifier.fillMaxWidth()) {
        // Line Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            // Top Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(if (isFirst) Color.Transparent else if (status == TimelineStatus.CURRENT || status == TimelineStatus.PASSED) MaterialTheme.colorScheme.primary else Color.LightGray)
            )
            
            // Icon/Dot
            Box(contentAlignment = Alignment.Center) {
                when (status) {
                    TimelineStatus.PASSED -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.LightGray)
                    TimelineStatus.CURRENT -> {
                        PulseDot()
                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                    TimelineStatus.NEXT -> Box(modifier = Modifier.size(16.dp).border(4.dp, MaterialTheme.colorScheme.onSurface, CircleShape).background(MaterialTheme.colorScheme.surface, CircleShape))
                    TimelineStatus.FUTURE -> Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null, tint = Color.LightGray)
                }
            }
            
            // Bottom Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(if (isLast) Color.Transparent else if (status == TimelineStatus.PASSED) MaterialTheme.colorScheme.primary else Color.LightGray)
            )
        }
        
        // Content Column
        Column(
            modifier = Modifier
                .padding(bottom = 32.dp, start = 4.dp)
                .alpha(if (status == TimelineStatus.PASSED) 0.5f else 1f)
        ) {
            if (isLive) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.live_label), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha=0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(title, style = if (status == TimelineStatus.CURRENT) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun IntrinsicHeightRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier = modifier.height(IntrinsicSize.Min), content = content)
}

@Composable
fun Modifier.alpha(alpha: Float) = this.then(Modifier.drawLayer(alpha = alpha))
fun Modifier.drawLayer(alpha: Float) = this

@Composable
fun PulseDot() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .scale(scale)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
         Box(
            modifier = Modifier
                .size(20.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}
