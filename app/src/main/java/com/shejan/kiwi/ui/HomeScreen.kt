package com.shejan.kiwi.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shejan.kiwi.logic.QrGenerator
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.AshGrey
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import com.shejan.kiwi.util.FileHelper
import com.shejan.kiwi.ui.HistoryViewModel
import kotlinx.coroutines.delay

/**
 * The Home Screen of the Kiwi app.
 * Provides a user interface for entering text/links and generating QR codes in real-time.
 * 
 * @param viewModel The [HistoryViewModel] used to save generated QRs to history.
 * @param initialUrl Optional initial text to populate the input field (e.g., from a shared intent).
 */
@Composable
fun HomeScreen(
    viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    initialUrl: String? = null
) {
    var textInput by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val saveQrToGallery = {
        qrBitmap?.let {
            val success = FileHelper.saveToGallery(context, it, "Kiwi_QR_${System.currentTimeMillis()}")
            if (success) {
                Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            saveQrToGallery()
        } else {
            Toast.makeText(context, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-populate textInput if shared from another app
    LaunchedEffect(initialUrl) {
        if (initialUrl != null) {
            textInput = initialUrl
        }
    }

    // Generate QR and Save to history when text changes, with debounce
    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty()) {
            delay(800) // Wait 0.8s after user stops typing
            val newBitmap = QrGenerator.generate(textInput, 512)
            if (newBitmap != null) {
                qrBitmap = newBitmap
                viewModel.saveUrl(textInput)
            }
        } else {
            qrBitmap = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // App Title / Header (Left-aligned, modern typography)
        Text(
            text = "Create Your",
            fontSize = 36.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "QR Code",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Center Box for QR Code
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // QR Code Display Area
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = if (qrBitmap != null) 2.dp else 0.dp,
                        color = if (qrBitmap != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.Crossfade(
                    targetState = qrBitmap != null,
                    label = "qr_crossfade",
                    animationSpec = tween(500)
                ) { isQrGenerated ->
                    if (!isQrGenerated) {
                        // Placeholder UI
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Awaiting input...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // QR Code Image
                        qrBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Generated QR Code",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Input Field for URL/Text
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter link or text...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
            trailingIcon = {
                if (textInput.isEmpty()) {
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val item = clipboard.primaryClip?.getItemAt(0)
                        val pasteData = item?.text
                        if (pasteData != null) {
                            textInput = pasteData.toString()
                        } else {
                            Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(onClick = { textInput = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: Save and Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Save Button (Primary)
            ActionButton(
                icon = Icons.Default.Download,
                label = "Save",
                modifier = Modifier.weight(1f),
                isPrimary = true,
                enabled = qrBitmap != null
            ) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    
                    if (hasPermission) {
                        saveQrToGallery()
                    } else {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                } else {
                    saveQrToGallery()
                }
            }
            
            // Share Button (Secondary)
            ActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                modifier = Modifier.weight(1f),
                isPrimary = false,
                enabled = qrBitmap != null
            ) {
                qrBitmap?.let { bitmap ->
                    FileHelper.shareImage(context, bitmap, "Kiwi_QR_Share")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * A customized button for actions on the Home Screen.
 * 
 * @param icon The vector icon to display on the button.
 * @param label The text label for the button.
 * @param modifier Modifier for layout customization.
 * @param isPrimary Whether this is a primary highlighted button.
 * @param enabled Whether the button is interactive.
 * @param onClick Callback when the button is clicked.
 */
@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
            contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = if (isPrimary) ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp) else null
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
