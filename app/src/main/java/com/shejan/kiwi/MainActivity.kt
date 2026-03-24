package com.shejan.kiwi

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shejan.kiwi.logic.QrGenerator
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import com.shejan.kiwi.ui.theme.KiwiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KiwiTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var textInput by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Generate QR when text changes
    LaunchedEffect(textInput) {
        qrBitmap = if (textInput.isNotEmpty()) {
            QrGenerator.generate(textInput, 512)
        } else {
            null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AmoledBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
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
                    modifier = Modifier.weight(1f),
                    enabled = qrBitmap != null
                ) {
                    // TODO: Implement Download
                }
                ActionButton(
                    icon = Icons.Default.Share,
                    label = "Share",
                    modifier = Modifier.weight(1f),
                    enabled = qrBitmap != null
                ) {
                    // TODO: Implement Share
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Professional Footer
            Text(
                text = "Minimal • Modern • Professional",
                color = Color.Gray.copy(alpha = 0.3f),
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
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
            containerColor = DarkGrey,
            contentColor = KiwiGreen,
            disabledContainerColor = DarkGrey.copy(alpha = 0.5f),
            disabledContentColor = Color.Gray.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}