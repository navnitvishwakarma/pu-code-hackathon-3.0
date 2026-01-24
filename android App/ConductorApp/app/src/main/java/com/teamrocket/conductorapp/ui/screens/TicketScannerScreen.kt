package com.teamrocket.conductorapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.teamrocket.conductorapp.data.api.RetrofitClient
import com.teamrocket.conductorapp.data.api.TicketRequest
import com.teamrocket.conductorapp.ui.theme.BrandBlue
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScannerScreen(
    busId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    // State
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Ticket", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isTorchOn = !isTorchOn
                        cameraControl?.enableTorch(isTorchOn)
                    }) {
                        Icon(
                            if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.8f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        
                        cameraProviderFuture.addListener({
                            val provider = cameraProviderFuture.get()
                            cameraProvider = provider
                            
                            val preview = Preview.Builder().build()
                            val selector = CameraSelector.DEFAULT_BACK_CAMERA
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(Size(1280, 720))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            
                            imageAnalysis.setAnalyzer(
                                Executors.newSingleThreadExecutor(),
                                QRCodeAnalyzer { result ->
                                    // Handle unique scans only to avoid spamming
                                    if (scanResult == null) {
                                         verifyTicket(result, busId, scope, context) { res -> 
                                             scanResult = res 
                                         }
                                    }
                                }
                            )

                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            
                            try {
                                provider.unbindAll()
                                val camera = provider.bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )
                                cameraControl = camera.cameraControl
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Overlay Viewfinder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp), // Lift up a bit
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .border(2.dp, BrandBlue, RoundedCornerShape(16.dp))
                    )
                    
                    Text(
                        "Align QR code within frame",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 300.dp)
                    )
                }

            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Camera permission required", color = Color.White)
                }
            }

            // Result Dialog
            if (scanResult != null) {
                AlertDialog(
                    onDismissRequest = { 
                        scanResult = null 
                        // Re-enable scanning? Using scanResult == null as flag
                    },
                    icon = { 
                        Icon(
                            if (scanResult!!.isValid) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            null,
                            tint = if (scanResult!!.isValid) Color(0xFF16A34A) else Color.Red
                        ) 
                    },
                    title = { Text(if (scanResult!!.isValid) "Valid Ticket" else "Invalid Ticket") },
                    text = { Text(scanResult!!.message) },
                    confirmButton = {
                        TextButton(onClick = { scanResult = null }) {
                            Text("Scan Next")
                        }
                    },
                    containerColor = Color.White,
                    textContentColor = Color.Black,
                    titleContentColor = Color.Black
                )
            }
        }
    }
}

data class ScanResult(val isValid: Boolean, val message: String)

fun verifyTicket(
    ticketId: String, 
    busId: String, 
    scope: kotlinx.coroutines.CoroutineScope, 
    context: android.content.Context,
    onResult: (ScanResult) -> Unit
) {
    scope.launch {
        try {
            val response = RetrofitClient.apiService.verifyTicket(TicketRequest(ticketId, busId))
            onResult(ScanResult(response.valid, response.message))
        } catch (e: Exception) {
            // Keep scanning if network fails? Or show error?
             onResult(ScanResult(false, "Network Error: ${e.message}"))
        }
    }
}

class QRCodeAnalyzer(
    private val onQRCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader()

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)
        
        val source = PlanarYUVLuminanceSource(
            data,
            image.width,
            image.height,
            0,
            0,
            image.width,
            image.height,
            false
        )
        
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        
        try {
            val result = reader.decode(binaryBitmap)
            onQRCodeScanned(result.text)
        } catch (e: Exception) {
            // No QR code found
        } finally {
            image.close()
        }
    }
}
