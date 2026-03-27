package com.shejan.kiwi.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.Shader
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.shejan.kiwi.R
import com.shejan.kiwi.ui.theme.AshGrey
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen

// --- Data Models & Constants ---

data class SettingsItemData(
    val title: String,
    val icon: ImageVector
)

val supportItems = listOf(
    SettingsItemData("Rate Us", Icons.Default.Star),
    SettingsItemData("Share App", Icons.Default.Share),
    SettingsItemData("Privacy Policy", Icons.Default.PrivacyTip)
)

val aboutItems = listOf(
    SettingsItemData("About Developer", Icons.Default.Person),
    SettingsItemData("Version Info", Icons.Default.Info)
)

// --- Helper Composables ---

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = KiwiGreen,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(item: SettingsItemData, onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AshGrey)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SocialIconButton(iconRes: Int, label: String, useTint: Boolean = true, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AshGrey),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = label,
                tint = if (useTint) Color.White else Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// --- Dialog Functions ---

@Composable
fun VersionDialog(onDismiss: () -> Unit) {
    val view = LocalView.current
    SideEffect {
        (view.parent as? DialogWindowProvider)?.window
            ?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clip(RoundedCornerShape(28.dp))
            .background(DarkGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Kiwi QR Generator",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Version 1.0.0",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider(color = Color.White.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your application is up to date",
                color = KiwiGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Build: stable-v1.0.0.1",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp
            )
        }

        // Close Button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DeveloperProfileCard(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    SideEffect {
        (view.parent as? DialogWindowProvider)?.window
            ?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(28.dp))
            .background(DarkGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text("Farjan Ahmmed", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("(Shejan)", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Social Links Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialIconButton(
                    iconRes = R.drawable.ic_email,
                    label = "Email",
                    useTint = true,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:farjan.swe@gmail.com")
                        }
                        context.startActivity(intent)
                    }
                )
                SocialIconButton(
                    iconRes = R.drawable.ic_linkedin,
                    label = "LinkedIn",
                    useTint = true,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/farjan-ahmmed/")))
                    }
                )
                SocialIconButton(
                    iconRes = R.drawable.ic_github,
                    label = "GitHub",
                    useTint = true,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shejanahmmed")))
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialIconButton(
                    iconRes = R.drawable.ic_facebook,
                    label = "Facebook",
                    useTint = true,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/beingshejan/")))
                    }
                )
                SocialIconButton(
                    iconRes = R.drawable.ic_instagram,
                    label = "Instagram",
                    useTint = true,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/iamshejan/")))
                    }
                )
            }
        }

        // Close Button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// --- Main Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var showVersionDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    val isDialogShowing = showVersionDialog || showDeveloperDialog
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isDialogShowing) 0.6f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    if (showVersionDialog) {
        Dialog(
            onDismissRequest = { showVersionDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            VersionDialog(onDismiss = { showVersionDialog = false })
        }
    }

    if (showDeveloperDialog) {
        Dialog(
            onDismissRequest = { showDeveloperDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            DeveloperProfileCard(onDismiss = { showDeveloperDialog = false })
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            // Scrollable header card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkGrey)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KiwiGreen
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("Support")
            }
            items(supportItems) { item ->
                SettingsItem(item, onClick = {
                    when (item.title) {
                        "Privacy Policy" -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.farjan.me/KIWIPrivecyPolicy/"))
                            context.startActivity(intent)
                        }
                        "Rate Us", "Share App" -> {
                            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("About")
            }
            items(aboutItems) { item ->
                SettingsItem(item, onClick = {
                    when (item.title) {
                        "About Developer" -> showDeveloperDialog = true
                        "Version Info" -> showVersionDialog = true
                    }
                })
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Kiwi QR Generator",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                        Text(
                            "Version 1.0.0",
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Dark scrim overlay when dialog is showing
        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
            )
        }
    }
}
