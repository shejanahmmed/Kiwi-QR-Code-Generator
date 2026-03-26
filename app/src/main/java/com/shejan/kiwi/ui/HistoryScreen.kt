package com.shejan.kiwi.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "History",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KiwiGreen
            )
            
            if (historyItems.isNotEmpty()) {
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
                        ) { showDeleteDialog = true }
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

        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
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
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete entire history?") },
            text = { Text("Are you sure you want to delete entire history?") },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(KiwiGreen)
                        .clickable {
                            viewModel.clearAll()
                            showDeleteDialog = false
                        }
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
                        .clickable { showDeleteDialog = false },
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

    if (showDetailsDialog && selectedItem != null) {
        val deleteInteractionSource = remember { MutableInteractionSource() }
        val isDeletePressed by deleteInteractionSource.collectIsPressedAsState()
        val deleteScale by animateFloatAsState(if (isDeletePressed) 0.95f else 1f, label = "deleteScale")

        val linkInteractionSource = remember { MutableInteractionSource() }
        val isLinkPressed by linkInteractionSource.collectIsPressedAsState()
        val linkScale by animateFloatAsState(if (isLinkPressed) 0.95f else 1f, label = "linkScale")

        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
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
                            .clickable { showDetailsDialog = false },
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
                    val qrBitmap = remember(selectedItem) {
                        QrGenerator.generate(selectedItem!!.url, 400)
                    }
                    
                     qrBitmap?.let { bit ->
                        Image(
                            bitmap = bit.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val success = FileHelper.saveToGallery(
                                        context,
                                        bit,
                                        "Kiwi_QR_${System.currentTimeMillis()}"
                                    )
                                    if (success) {
                                        Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .background(Color.White)
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = selectedItem!!.url,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        // Date Text
                        Text(
                            text = dateFormatter.format(Date(selectedItem!!.timestamp)),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        // Separator
                        Text(
                            text = "•",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )

                        // Time Text
                        Text(
                            text = timeFormatter.format(Date(selectedItem!!.timestamp)),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete Button
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = deleteScale
                                    scaleY = deleteScale
                                }
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable(
                                    interactionSource = deleteInteractionSource,
                                    indication = null
                                ) {
                                    viewModel.deleteItem(selectedItem!!)
                                    showDetailsDialog = false
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Delete",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Go to Link Button
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = linkScale
                                    scaleY = linkScale
                                }
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable(
                                    interactionSource = linkInteractionSource,
                                    indication = null
                                ) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem!!.url))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Go to Link",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                // Confirm button is now in the text slot for central placement
                Spacer(modifier = Modifier.height(1.dp))
            },
            dismissButton = null,
            containerColor = DarkGrey
        )
    }
}

@Composable
fun HistoryCard(item: HistoryItem, date: String, onDelete: () -> Unit, onClick: () -> Unit) {
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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = KiwiGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.url,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
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
