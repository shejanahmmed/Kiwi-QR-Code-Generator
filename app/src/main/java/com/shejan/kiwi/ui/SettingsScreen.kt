package com.shejan.kiwi.ui

import android.content.Intent
import android.net.Uri
import com.shejan.kiwi.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.AshGrey
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }

    if (showDeveloperDialog) {
        Dialog(onDismissRequest = { showDeveloperDialog = false }) {
            DeveloperProfileCard()
        }
    }

    if (showVersionDialog) {
        Dialog(onDismissRequest = { showVersionDialog = false }) {
            VersionDialog()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KiwiGreen
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            item {
                SectionHeader("Support")
            }
            items(supportItems) { item ->
                SettingsItem(item, onClick = {
                    when (item.title) {
                        "Privacy Policy" -> {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.farjan.me/KIWIPrivecyPolicy/"))
                            context.startActivity(intent)
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
    }
}

@Composable
fun VersionDialog() {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clip(RoundedCornerShape(28.dp))
            .background(DarkGrey)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(35.dp))
                .background(AshGrey),
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
}

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(AshGrey)
            .clickable { onClick() }
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
fun DeveloperProfileCard() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(28.dp))
            .background(DarkGrey)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Placeholder
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(AshGrey)
                .border(2.dp, KiwiGreen.copy(alpha = 0.3f), RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("S", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = KiwiGreen)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Farjan Ahmmed", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("(Shejan)", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("@shejanahmmed", color = KiwiGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        
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
}

@Composable
fun SocialIconButton(iconRes: Int, label: String, useTint: Boolean = true, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
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
