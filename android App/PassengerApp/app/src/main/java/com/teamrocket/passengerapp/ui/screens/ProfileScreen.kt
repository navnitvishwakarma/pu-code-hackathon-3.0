package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import com.teamrocket.passengerapp.R
import com.teamrocket.passengerapp.utils.LocaleManager
import com.teamrocket.passengerapp.utils.findActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.clickable

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State for User Data
    var user by remember { mutableStateOf<com.teamrocket.passengerapp.data.api.User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var userMobile by remember { mutableStateOf("") }
    
    // Dialog States
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Fetch User Data
    LaunchedEffect(Unit) {
        com.teamrocket.passengerapp.utils.UserPreferences.getUserMobile(context).collect { mobile ->
            if (!mobile.isNullOrEmpty()) {
                userMobile = mobile
                try {
                    val response = com.teamrocket.passengerapp.data.api.RetrofitClient.apiService.getUser(mobile)
                    if (response.success && response.user != null) {
                        user = response.user
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        }
    }

    if (showEditProfileDialog && user != null) {
        EditProfileDialog(
            currentUser = user!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updatedUser ->
                scope.launch {
                    try {
                        val request = com.teamrocket.passengerapp.data.api.UserRequest(
                            name = updatedUser.name,
                            mobile = updatedUser.mobile,
                            gender = updatedUser.gender,
                            age = updatedUser.age,
                            address = updatedUser.address
                        )
                        val response = com.teamrocket.passengerapp.data.api.RetrofitClient.apiService.updateUser(userMobile, request)
                        if (response.success) {
                            user = updatedUser // Update local state
                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                            showEditProfileDialog = false
                        } else {
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                         Toast.makeText(context, "Update Failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout)) },
            text = { Text("Are you sure you want to logout? This will clear your session.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        com.teamrocket.passengerapp.utils.UserPreferences.clearUser(context)
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ... (Notifications and Emergency Dialogs remain same)
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Notifications") },
            text = { Text("Push Notifications are enabled.") },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = { Text("Emergency Contacts") },
            text = {
                Column {
                    Text("👮 Police: 100")
                    Text("🚑 Ambulance: 108")
                    Text("👩‍🚒 Fire: 101")
                    Text("📞 SOS: +91 98765 43210")
                }
            },
            confirmButton = {
                TextButton(onClick = { showEmergencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { code ->
                LocaleManager.setLocale(context, code)
                context.findActivity()?.recreate()
                showLanguageDialog = false
            }
        )
    }

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
                    text = stringResource(R.string.profile_settings),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Profile Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Box(Modifier.clickable { showEditProfileDialog = true }) {
                        Image(
                            painter = rememberAsyncImagePainter("https://lh3.googleusercontent.com/aida-public/AB6AXuB-0XC5A_YMg2mg71CBpka8g884vq21Q5m7OoZqDSKkmDO8fB6eDyzJlCkCviTGAuRgPRgHLMJKVFXkSb_W8s0o_dq9sZhjYrO014J7rLnTNY_eKWjbbnHF62KNubGarUji7H8fgAWd4GPSWddfH0yYzj1cgKLJfk_fyPErOiIWd7Fyagrk3OM75C5y1AKlMjEYMy07OY7GqO28OsDA8vIX2xS2amXYPcDpDnFVf7XP_PIXgHM9Pt8wJZ_81EJCNXVblV2eXBL00db0"),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = user?.name ?: "Guest User",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (userMobile.isNotEmpty()) "+91 $userMobile" else "Login to see details",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                     if (user?.address != null) {
                        Text(
                            text = user?.address ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings List
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Profile",
                        subtitle = "Update Name, Age, Address",
                        onClick = { showEditProfileDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Translate,
                        title = stringResource(R.string.change_language),
                        subtitle = LocaleManager.getSavedLanguage(context)?.let { 
                            when(it) {
                                "hi" -> stringResource(R.string.lang_hindi)
                                "gu" -> stringResource(R.string.lang_gujarati)
                                else -> stringResource(R.string.lang_english)
                            }
                        } ?: stringResource(R.string.lang_english),
                        onClick = { showLanguageDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.notification_settings),
                        subtitle = "Enabled",
                        onClick = { showNotificationsDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.EmergencyShare,
                        title = stringResource(R.string.emergency_contacts),
                        subtitle = stringResource(R.string.manage_sos),
                        isEmergency = true,
                        onClick = { showEmergencyDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = stringResource(R.string.app_info),
                        subtitle = null,
                        onClick = {}
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                Surface(
                    onClick = { showLogoutDialog = true },
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.logout),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.version),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_language)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageOption(stringResource(R.string.lang_english) + " (English)", "en", onLanguageSelected)
                LanguageOption(stringResource(R.string.lang_hindi) + " (Hindi)", "hi", onLanguageSelected)
                LanguageOption(stringResource(R.string.lang_gujarati) + " (Gujarati)", "gu", onLanguageSelected)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.skip))
            }
        }
    )
}

@Composable
fun LanguageOption(text: String, code: String, onSelect: (String) -> Unit) {
    Surface(
        onClick = { onSelect(code) },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    isEmergency: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = if (isEmergency) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
    val iconBgColor = if (isEmergency) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    val iconColor = if (isEmergency) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
    val borderColor = if (isEmergency) Color(0xFFFECACA) else Color.Transparent

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = if (isEmergency) androidx.compose.foundation.BorderStroke(1.dp, borderColor) else null,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper for Layout
@Composable
fun Column(
    verticalArrangement: androidx.compose.foundation.layout.Arrangement.Vertical,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    androidx.compose.foundation.layout.Column(
        verticalArrangement = verticalArrangement,
        content = content
    )
}
