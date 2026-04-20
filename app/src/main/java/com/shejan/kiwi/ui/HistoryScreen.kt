package com.shejan.kiwi.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shejan.kiwi.logic.HistoryItem
import com.shejan.kiwi.logic.QrGenerator
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.AshGrey
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import com.shejan.kiwi.util.FileHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * The History Screen displaying a list of previously generated and scanned QR codes.
 * Allows users to view details, open links, share/save QRs, and delete entries.
 * 
 * @param viewModel The [HistoryViewModel] providing state and logic.
 */
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val historyItems by viewModel.allHistory.collectAsState(initial = emptyList())
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<HistoryItem?>(null) }
    
    val context = LocalContext.current

    if (historyItems.isEmpty()) {
        // Empty state UI when no history exists
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBlack)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 100.dp)
        ) {
            item {
                HeaderSection(showDeleteAction = false, onDeleteAllClick = {})
            }
            item {
                EmptyHistoryPlaceholder()
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBlack)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 48.dp, bottom = 100.dp)
        ) {
            item {
                HeaderSection(showDeleteAction = true, onDeleteAllClick = { showDeleteDialog = true })
            }

            items(historyItems) { item ->
                HistoryCard(
                    item = item,
                    date = dateFormatter.format(Date(item.timestamp)),
                    onDelete = { viewModel.deleteItem(item) },
                    onClick = {
                        selectedItem = item
                        showDetailsDialog = true
                    }
                )
            }
        }
    }

    // State for the renaming flow
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAllConfirmationDialog(
            onConfirm = {
                viewModel.clearAll()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showDetailsDialog && selectedItem != null) {
        HistoryDetailsDialog(
            item = selectedItem!!,
            dateFormatter = dateFormatter,
            timeFormatter = timeFormatter,
            onDelete = {
                viewModel.deleteItem(selectedItem!!)
                showDetailsDialog = false
            },
            onRenameRequest = {
                showRenameDialog = true
            },
            onDismiss = { showDetailsDialog = false }
        )
    }

    if (showRenameDialog && selectedItem != null) {
        RenameDialog(
            currentName = selectedItem!!.label ?: "",
            onConfirm = { newName ->
                val updatedItem = selectedItem!!.copy(
                    label = newName,
                    timestamp = System.currentTimeMillis()
                )
                viewModel.updateItem(selectedItem!!, newName)
                selectedItem = updatedItem // Update selectedItem to reflect changes in details dialog
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }
}

/**
 * Modern header section replacing the rigid card block, showing the title and an optional delete tool.
 */
@Composable
private fun HeaderSection(
    showDeleteAction: Boolean,
    onDeleteAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Saved",
                fontSize = 36.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
            Text(
                text = "History",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KiwiGreen
            )
        }

        if (showDeleteAction) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

            Surface(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onDeleteAllClick() },
                color = DarkGrey,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear All",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Clear All",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * Modernized Placeholder UI shown when the history database is empty.
 */
@Composable
private fun EmptyHistoryPlaceholder() {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(800)),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DarkGrey)
                    .border(2.dp, Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.2f),
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No history yet",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Scanned and saved codes will appear here.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Modernized Delete All Confirmation Dialog.
 */
@Composable
private fun DeleteAllConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete all saved items? This action cannot be undone.",
                fontSize = 14.sp,
                color = Color.LightGray
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Delete", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkGrey,
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * Modernized Dialog for renaming a history item.
 */
@Composable
private fun RenameDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var textInput by remember { mutableStateOf(currentName) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Set Custom Name", 
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ) 
        },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("e.g. WiFi Password, Portfolio", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KiwiGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textInput) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = KiwiGreen,
                    contentColor = AmoledBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkGrey,
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * Polished detailed view dialog for a selected history item.
 */
@Composable
private fun HistoryDetailsDialog(
    item: HistoryItem,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat,
    onDelete: () -> Unit,
    onRenameRequest: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var qrBitmap by remember(item) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(item) {
        qrBitmap = QrGenerator.generate(item.url, 400)
    }

    val saveQrToGallery = {
        qrBitmap?.let { bit ->
            val success = FileHelper.saveToGallery(context, bit, "Kiwi_QR_${System.currentTimeMillis()}")
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

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        shape = RoundedCornerShape(32.dp),
        containerColor = DarkGrey,
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close action & Title space
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(modifier = Modifier.size(36.dp)) // Spacer to keep title centered
                    Text(
                        text = "Scan Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp).background(AshGrey, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                qrBitmap?.let { bit ->
                    Surface(
                        modifier = Modifier.size(220.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Image(
                            bitmap = bit.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clickable {
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
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display Custom Name (Label) if it exists
                if (!item.label.isNullOrEmpty()) {
                    Text(
                        text = item.label,
                        color = KiwiGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Always display the actual Link Content
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AshGrey.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = item.url,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = dateFormatter.format(Date(item.timestamp)),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Text(text = "•", color = Color.Gray, fontSize = 13.sp)
                    Text(
                        text = timeFormatter.format(Date(item.timestamp)),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SquareIconButton(
                        iconRes = com.shejan.kiwi.R.drawable.ic_delete_action,
                        onClick = onDelete,
                        containerColor = Color(0xFFFF5252).copy(alpha = 0.1f),
                        iconTint = Color(0xFFFF5252)
                    )

                    SquareIconButton(
                        iconRes = com.shejan.kiwi.R.drawable.ic_edit_action,
                        onClick = onRenameRequest,
                        containerColor = DarkGrey,
                        iconTint = Color.White
                    )

                    if (item.url.startsWith("http://") || item.url.startsWith("https://")) {
                        SquareIconButton(
                            iconRes = com.shejan.kiwi.R.drawable.ic_open_action,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                context.startActivity(intent)
                            },
                            containerColor = KiwiGreen,
                            iconTint = AmoledBlack
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * A sleek square button used in the history details dialog.
 */
@Composable
private fun SquareIconButton(
    iconRes: Int,
    onClick: () -> Unit,
    containerColor: Color = Color.White,
    iconTint: Color = AmoledBlack
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "buttonScale")

    Box(
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Modernized history card representing a single entry.
 */
@Composable
fun HistoryCard(item: HistoryItem, date: String, onDelete: () -> Unit, onClick: () -> Unit) {
    val isScanned = item.type == "scanned"
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = DarkGrey,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon representing the entry type (Scanned vs Generated)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isScanned) Icons.Default.QrCodeScanner else Icons.Default.Link,
                    contentDescription = null,
                    tint = if (isScanned) Color.White else KiwiGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Show Label if it exists, otherwise show URL
                Text(
                    text = if (!item.label.isNullOrEmpty()) item.label else item.url,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                // If label exists, show URL as subtext underneath
                if (!item.label.isNullOrEmpty()) {
                    Text(
                        text = item.url,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = date,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", color = Color.Gray.copy(alpha = 0.3f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Type badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isScanned) Color.Gray.copy(alpha = 0.2f) else KiwiGreen.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isScanned) "SCANNED" else "GENERATED",
                            color = if (isScanned) Color.LightGray else KiwiGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
