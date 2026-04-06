package com.shejan.kiwi.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    if (historyItems.isEmpty()) {
        // Empty state UI when no history exists
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBlack)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                HeaderCard()
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                HistoryHeader(
                    scale = scale,
                    interactionSource = interactionSource,
                    onDeleteAllClick = { showDeleteDialog = true }
                )
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
 * A reusable header card for the history screen.
 */
@Composable
private fun HeaderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrey)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "History",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KiwiGreen
        )
    }
}

/**
 * Placeholder UI shown when the history database is empty.
 */
@Composable
private fun EmptyHistoryPlaceholder() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "History is empty",
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
    }
}

/**
 * Header section for the history list with a "Delete All" action.
 * 
 * @param scale The current animation scale for interaction.
 * @param interactionSource Interaction source for the delete button.
 * @param onDeleteAllClick Callback when "Delete All" is pressed.
 */
@Composable
private fun HistoryHeader(
    scale: Float,
    interactionSource: MutableInteractionSource,
    onDeleteAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrey)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "History",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KiwiGreen
        )
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onDeleteAllClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Delete All",
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Dialog for confirming the deletion of all history entries.
 * 
 * @param onConfirm Callback when user confirms deletion.
 * @param onDismiss Callback when user cancels or dismisses the dialog.
 */
@Composable
private fun DeleteAllConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete entire history?") },
        text = { Text("Are you sure you want to delete entire history?") },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(KiwiGreen)
                    .clickable { onConfirm() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Sure",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        containerColor = DarkGrey,
        titleContentColor = Color.White,
        textContentColor = Color.Gray
    )
}

/**
 * Dialog for renaming a history item (setting a custom name/label).
 * 
 * @param currentName The current custom name/label to be edited.
 * @param onConfirm Callback with the new name string.
 * @param onDismiss Callback when the dialog is dismissed.
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
        title = { Text("Set Name", color = Color.White) },
        text = {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Custom Name (e.g., My Portfolio)", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KiwiGreen,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(KiwiGreen)
                    .clickable { onConfirm(textInput) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Save", color = AmoledBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = DarkGrey
    )
}

/**
 * Detailed view dialog for a selected history item.
 * Displays the QR code and provides square icon buttons for actions.
 * 
 * @param item The selected [HistoryItem].
 * @param dateFormatter Formatter for the creation date.
 * @param timeFormatter Formatter for the creation time.
 * @param onDelete Callback when the item is deleted.
 * @param onRenameRequest Callback to trigger the rename dialog.
 * @param onDismiss Callback when the dialog is dismissed.
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

    androidx.compose.runtime.LaunchedEffect(item) {
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
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF5252))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                 qrBitmap?.let { bit ->
                    Image(
                        bitmap = bit.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
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
                            .background(Color.White)
                            .padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Display Custom Name (Label) if it exists
                if (!item.label.isNullOrEmpty()) {
                    Text(
                        text = item.label,
                        color = KiwiGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Always display the actual Link Content
                Text(
                    text = item.url,
                    color = Color.White, // Restored full brightness
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = dateFormatter.format(Date(item.timestamp)),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(text = "•", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = timeFormatter.format(Date(item.timestamp)),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete Button
                    SquareIconButton(
                        iconRes = com.shejan.kiwi.R.drawable.ic_delete_action,
                        onClick = onDelete
                    )

                    // Rename Button
                    SquareIconButton(
                        iconRes = com.shejan.kiwi.R.drawable.ic_edit_action,
                        onClick = onRenameRequest
                    )

                    // Open Link Button (HTTP/HTTPS only)
                    if (item.url.startsWith("http://") || item.url.startsWith("https://")) {
                        SquareIconButton(
                            iconRes = com.shejan.kiwi.R.drawable.ic_open_action,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Spacer(modifier = Modifier.height(1.dp))
        },
        dismissButton = null,
        containerColor = DarkGrey
    )
}

/**
 * A square, icon-only button used in the history details dialog.
 * 
 * @param iconRes The drawable resource ID for the icon.
 * @param onClick Callback when the button is clicked.
 */
@Composable
private fun SquareIconButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "buttonScale")

    Box(
        modifier = Modifier
            .size(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = iconRes),
            contentDescription = null,
            tint = AmoledBlack, // Set to AmoledBlack for high contrast on white background
            modifier = Modifier.size(26.dp)
        )
    }
}

/**
 * A card representing a single history entry in the list.
 * 
 * @param item The [HistoryItem] to display.
 * @param date Formatted date string.
 * @param onDelete Callback when the delete icon is pressed.
 * @param onClick Callback when the card is clicked.
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
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isScanned) Icons.Default.QrCodeScanner else Icons.Default.Link,
                    contentDescription = null,
                    tint = KiwiGreen,
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
                        fontSize = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = date,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•", color = Color.Gray.copy(alpha = 0.3f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Type badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (isScanned) "SCANNED" else "GENERATED",
                            color = Color.Gray.copy(alpha = 0.8f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
