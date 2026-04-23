package com.shejan.kiwi.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import com.shejan.kiwi.ui.HistoryViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * The Scanner Screen of the Kiwi app.
 * Handles camera permissions and integrates [CameraScanner] to read QR codes using Google ML Kit.
 * 
 * @param viewModel The [HistoryViewModel] used to save scanned QR data.
 */
@Composable
fun ScannerScreen(
    viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Launcher for requesting camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Request permission on first launch
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (hasCameraPermission) {
            CameraScanner(
                onQrCodeScanned = { scannedValue ->
                    // Save every scan to history
                    viewModel.saveUrl(scannedValue, "scanned")
                    // Open URLs in browser if applicable
                    if (scannedValue.startsWith("http://") || scannedValue.startsWith("https://")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedValue))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("ScannerScreen", "Error opening URL: $e")
                        }
                    }
                }
            )
        } else {
            // UI shown when camera permission is denied
            PermissionDeniedPlaceholder(onGrantClick = { permissionLauncher.launch(Manifest.permission.CAMERA) })
        }
    }
}

/**
 * A placeholder UI for when camera permission is denied.
 * @param onGrantClick Callback for the permission request button.
 */
@Composable
private fun PermissionDeniedPlaceholder(onGrantClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera Permission Required",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "We need camera access to scan QR codes.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGrantClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Grant Permission", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

/**
 * Camera preview and analysis component using CameraX and ML Kit Barcode Scanning.
 * 
 * @param onQrCodeScanned Callback triggered when a QR code is detected.
 */
@Composable
fun CameraScanner(onQrCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(cameraExecutor) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    
    // Track last scanned URL and time for debouncing repeated scans
    var lastScannedUrl by remember { mutableStateOf("") }
    var lastScanTime by remember { mutableLongStateOf(0L) }
    var isFlashOn by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val inputImage = InputImage.fromFilePath(context, uri)
                    val scanner = BarcodeScanning.getClient(
                        BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                    )
                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                barcodes.firstOrNull()?.rawValue?.let { url ->
                                    onQrCodeScanned(url)
                                }
                            } else {
                                android.widget.Toast.makeText(context, "No QR code found in image", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Log.e("ScannerScreen", "Barcode processing failed", it)
                            android.widget.Toast.makeText(context, "Failed to analyze image", android.widget.Toast.LENGTH_SHORT).show()
                        }
                } catch (e: Exception) {
                    Log.e("ScannerScreen", "Failed to load image", e)
                    android.widget.Toast.makeText(context, "Failed to load image", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                // Initialize Preview use case
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Initialize Barcode Scanning client
                val scanner = BarcodeScanning.getClient(
                    BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                )

                // Initialize Image Analysis use case
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(scanner, imageProxy) { barcodes ->
                        barcodes.firstOrNull()?.rawValue?.let { url ->
                            val currentTime = System.currentTimeMillis()
                            // Simple debounce: only trigger if it's a new URL or 2 seconds have passed
                            if (url != lastScannedUrl || (currentTime - lastScanTime > 2000)) {
                                lastScannedUrl = url
                                lastScanTime = currentTime
                                onQrCodeScanned(url)
                            }
                        }
                    }
                }

                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                    cameraControl = camera.cameraControl
                    cameraControl?.enableTorch(isFlashOn)
                } catch (e: Exception) {
                    Log.e("ScannerScreen", "Camera binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )

    // Visual overlay for the scanner UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ScannerOverlay(
            isFlashOn = isFlashOn,
            onFlashToggle = {
                isFlashOn = !isFlashOn
                cameraControl?.enableTorch(isFlashOn)
            },
            onGalleryClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }
}

/**
 * Visual overlay displaying a target frame and flash toggle button.
 * 
 * @param isFlashOn Current state of the camera torch.
 * @param onFlashToggle Callback to toggle the camera torch.
 */
@Composable
fun ScannerOverlay(isFlashOn: Boolean, onFlashToggle: () -> Unit, onGalleryClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Target Frame for scanning
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .background(Color.Transparent)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 4.dp.toPx()
                    val cornerLength = 40.dp.toPx()
                    val color = primaryColor
                    
                    // Draw corner markers
                    // Top Left
                    drawLine(color, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(cornerLength, 0f), strokeWidth)
                    drawLine(color, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(0f, cornerLength), strokeWidth)
                    
                    // Top Right
                    drawLine(color, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width - cornerLength, 0f), strokeWidth)
                    drawLine(color, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width, cornerLength), strokeWidth)
                    
                    // Bottom Left
                    drawLine(color, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(cornerLength, size.height), strokeWidth)
                    drawLine(color, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(0f, size.height - cornerLength), strokeWidth)
                    
                    // Bottom Right
                    drawLine(color, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width - cornerLength, size.height), strokeWidth)
                    drawLine(color, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height - cornerLength), strokeWidth)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Align QR code inside the frame",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                    fontSize = 14.sp
                )
            }
        }
        
        // Flash toggle button
        IconButton(
            onClick = onFlashToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Toggle Flash",
                tint = if (isFlashOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Gallery button
        IconButton(
            onClick = onGalleryClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 24.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Pick Image from Gallery",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Process a CameraX image proxy and run it through the ML Kit barcode scanner.
 * 
 * @param scanner The barcode scanner client.
 * @param imageProxy The image proxy from CameraX's analyzer.
 * @param onSuccess Callback with detected barcodes.
 */
@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onSuccess: (List<Barcode>) -> Unit
) {
    imageProxy.image?.let { image ->
        val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                onSuccess(barcodes)
            }
            .addOnFailureListener {
                Log.e("ScannerScreen", "Barcode processing failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } ?: imageProxy.close()
}
