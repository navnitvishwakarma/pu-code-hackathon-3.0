package com.teamrocket.conductorapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamrocket.conductorapp.data.api.AuthRequest
import com.teamrocket.conductorapp.data.api.RetrofitClient
import com.teamrocket.conductorapp.data.api.VerifyRequest
import com.teamrocket.conductorapp.ui.components.PrimaryButton
import com.teamrocket.conductorapp.ui.components.StyledTextField
import com.teamrocket.conductorapp.ui.theme.BrandBlue
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var mobile by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandBlue)
                .padding(top = 40.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Smart Transit",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "OFFICIAL CONDUCTOR APP",
                        color = Color.Blue.copy(alpha = 0.8f).copy(red = 0.8f, green = 0.9f, blue = 1f), // Light blue text
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (isOtpSent) "Verify Identity" else "Welcome back,\nConductor.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (isOtpSent) "Enter the PIN sent to +91 $mobile" else "Enter your credentials to begin your shift.",
                fontSize = 14.sp,
                color = Color(0xFF616F89),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            if (!isOtpSent) {
                StyledTextField(
                    value = mobile,
                    onValueChange = { if (it.length <= 10) mobile = it },
                    label = "Mobile Number",
                    placeholder = "9876543210",
                    keyboardType = KeyboardType.Phone,
                    maxLength = 10
                )
            } else {
                StyledTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 4) otp = it },
                    label = "One-Time Password",
                    placeholder = "••••",
                    keyboardType = KeyboardType.NumberPassword,
                    maxLength = 4
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Login Button
            PrimaryButton(
                text = if (isOtpSent) "Verify & Login" else "Get OTP",
                isLoading = isLoading,
                onClick = {
                    if (!isOtpSent) {
                        // Send OTP
                        if (mobile.length < 10) {
                            Toast.makeText(context, "Enter valid mobile", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.sendOtp(AuthRequest(mobile))
                                if (response.success) {
                                    Toast.makeText(context, "OTP: ${response.otp}", Toast.LENGTH_LONG).show()
                                    isOtpSent = true
                                } else {
                                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        // Verify OTP
                        if (otp.length < 4) {
                            Toast.makeText(context, "Enter 4-digit OTP", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.verifyOtp(VerifyRequest(mobile, otp))
                                if (response.success) {
                                    onLoginSuccess(response.conductor?.name ?: "Conductor")
                                } else {
                                    Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "v1.2.0 • Government of India",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9AA2B1),
                letterSpacing = 1.sp
            )
        }
    }
}
