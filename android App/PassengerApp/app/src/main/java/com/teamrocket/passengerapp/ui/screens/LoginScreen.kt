package com.teamrocket.passengerapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.teamrocket.passengerapp.R
import com.teamrocket.passengerapp.ui.components.StandardButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = androidx.compose.ui.platform.LocalContext.current as android.app.Activity
    val authManager = remember { com.teamrocket.passengerapp.data.auth.AuthManager(activity) }
    val scope = rememberCoroutineScope()
    
    var mobileNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isOtpVerified by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            
            // Logo / Branding (Placeholder Icon for now unless we have a resource)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                 Icon(
                     imageVector = Icons.Default.Phone, // Using Phone icon as placeholder logo
                     contentDescription = "Logo",
                     tint = MaterialTheme.colorScheme.primary,
                     modifier = Modifier.size(48.dp)
                 )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.enter_mobile),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Mobile & OTP
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) mobileNumber = it },
                    label = { Text(stringResource(R.string.mobile_number)) },
                    prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    enabled = !isOtpVerified && !isLoading
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                androidx.compose.material3.Button(
                    onClick = {
                        if (mobileNumber.length == 10) {
                            isLoading = true
                            authManager.sendOtp("+91$mobileNumber", object : com.teamrocket.passengerapp.data.auth.AuthManager.AuthCallback {
                                override fun onCodeSent() {
                                    isLoading = false
                                    isOtpSent = true
                                    android.widget.Toast.makeText(context, "OTP Sent!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                override fun onVerificationCompleted() {
                                    isLoading = false
                                    isOtpVerified = true
                                    android.widget.Toast.makeText(context, "Auto-verified!", android.widget.Toast.LENGTH_SHORT).show()
                                    // Save User Session
                                    scope.launch {
                                        com.teamrocket.passengerapp.utils.UserPreferences.saveUser(context, mobileNumber)
                                    }
                                }
                                override fun onVerificationFailed(error: String) {
                                    isLoading = false
                                    android.widget.Toast.makeText(context, "Error: $error", android.widget.Toast.LENGTH_LONG).show()
                                }
                                override fun onSignInSuccess() {
                                    // Handled in verification completed
                                }
                            })
                        } else {
                            android.widget.Toast.makeText(context, "Enter valid mobile", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = mobileNumber.length == 10 && !isOtpSent && !isOtpVerified && !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    if (isLoading && !isOtpSent) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(if (isOtpSent) "Sent" else "Get OTP")
                    }
                }
            }
            
            if (isOtpSent && !isOtpVerified) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Button(
                    onClick = {
                        if (otp.length == 6) {
                            isLoading = true
                            authManager.verifyOtp(otp, object : com.teamrocket.passengerapp.data.auth.AuthManager.AuthCallback {
                                override fun onCodeSent() {}
                                override fun onVerificationCompleted() {
                                    isLoading = false
                                    isOtpVerified = true
                                    android.widget.Toast.makeText(context, "Verified!", android.widget.Toast.LENGTH_SHORT).show()
                                    // Save User Session
                                    scope.launch {
                                        com.teamrocket.passengerapp.utils.UserPreferences.saveUser(context, mobileNumber)
                                    }
                                }
                                override fun onVerificationFailed(error: String) {
                                    isLoading = false
                                    android.widget.Toast.makeText(context, "Invalid OTP: $error", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                override fun onSignInSuccess() {
                                    isLoading = false
                                    isOtpVerified = true
                                    // Save User Session
                                    scope.launch {
                                        com.teamrocket.passengerapp.utils.UserPreferences.saveUser(context, mobileNumber)
                                    }
                                }
                            })
                        } else {
                            android.widget.Toast.makeText(context, "Enter 6-digit OTP", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Verify OTP")
                    }
                }
            }

            if (isOtpVerified) {
                 Spacer(modifier = Modifier.height(8.dp))
                 Text("✅ Mobile Verified", color = androidx.compose.ui.graphics.Color(0xFF16A34A), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(0.8f))

            StandardButton(
                text = stringResource(R.string.login_btn),
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                enabled = isOtpVerified
            )

            androidx.compose.material3.TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "New User? Register here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
             Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


