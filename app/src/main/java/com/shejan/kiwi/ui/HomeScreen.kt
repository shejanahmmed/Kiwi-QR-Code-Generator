package com.shejan.kiwi.ui

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode
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

@Composable
fun HomeScreen() {
    var textInput by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    // Generate QR when text changes
    LaunchedEffect(textInput) {
        qrBitmap = if (textInput.isNotEmpty()) {
            QrGenerator.generate(textInput, 512)
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = "Kiwi",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KiwiGreen,
            modifier = Modifier.padding(top = 16.dp)
        )

        // QR Code Display Area
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(DarkGrey)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (qrBitmap == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Enter text to generate QR",
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }

        // Input Field
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Paste link here...", color = Color.Gray) },
            trailingIcon = {
                if (textInput.isEmpty()) {
                    TextButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val item = clipboard.primaryClip?.getItemAt(0)
                            val pasteData = item?.text
                            if (pasteData != null) {
                                textInput = pasteData.toString()
                            } else {
                                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = KiwiGreen)
                    ) {
                        Text("Paste", fontWeight = FontWeight.Bold)
                    }
                } else {
                    IconButton(
                        onClick = { textInput = "" },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = KiwiGreen
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KiwiGreen,
                unfocusedBorderColor = DarkGrey,
                focusedContainerColor = DarkGrey,
                unfocusedContainerColor = DarkGrey,
                cursorColor = KiwiGreen,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionButton(
                icon = Icons.Default.Download,
                label = "Save",
                modifier = Modifier.weight(1f)
            ) {
                if (qrBitmap != null) {
                    val success = FileHelper.saveToGallery(context, qrBitmap!!, "Kiwi_QR_${System.currentTimeMillis()}")
                    if (success) {
                        Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please enter a link first", Toast.LENGTH_SHORT).show()
                }
            }
            ActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                modifier = Modifier.weight(1f)
            ) {
                if (qrBitmap != null) {
                    FileHelper.shareImage(context, qrBitmap!!, "Kiwi_QR_Share")
                } else {
                    Toast.makeText(context, "Please enter a link first", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AshGrey,
            contentColor = Color.White,
            disabledContainerColor = AshGrey.copy(alpha = 0.6f),
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
