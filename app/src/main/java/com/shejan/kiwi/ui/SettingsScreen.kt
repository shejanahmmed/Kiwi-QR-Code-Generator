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
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.AshGrey
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen

/**
 * Data model for a single setting entry.
 */
data class SettingsItemData(
    val title: String,
    val icon: ImageVector
)

// Predefined lists for settings categories
val appearanceItems = listOf(
    SettingsItemData("App Theme", Icons.Default.Palette)
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

/**
 * A header text for a group of settings.
 */
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = KiwiGreen,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
    )
}

/**
 * A interactive row representing a single setting item within a grouped card.
 * 
 * @param item The data for the item.
 * @param isLastItem Whether this is the last item in the group (hides the divider).
 * @param onClick Callback when the item is clicked.
 */
@Composable
fun SettingsItem(
    item: SettingsItemData,
    isLastItem: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Styled Icon Container
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = KiwiGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
        if (!isLastItem) {
            Divider(
                modifier = Modifier.padding(start = 70.dp, end = 16.dp),
                color = Color.White.copy(alpha = 0.05f),
                thickness = 1.dp
            )
        }
    }
}

/**
 * Dialog displaying app version and build information.
 * 
 * @param onDismiss Callback when the dialog is closed.
 */
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
            .clip(RoundedCornerShape(32.dp))
            .background(DarkGrey)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon Container
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Kiwi QR Generator",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Text(
                text = "v1.0.0 (Stable)",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Version Status Badge
            Surface(
                color = KiwiGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = KiwiGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LATEST VERSION",
                        color = KiwiGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Build: stable-v1.0.0.1",
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.05f), androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Profile card displaying developer information and social links.
 * 
 * @param onDismiss Callback when the dialog is closed.
 */
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
            .clip(RoundedCornerShape(32.dp))
            .background(DarkGrey)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Developer Initials with Gradient
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(KiwiGreen, Color(0xFF1B5E20))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FA",
                    color = AmoledBlack,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Farjan Ahmmed",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Text(
                text = "(Shejan)",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Developer Role Badge
            Surface(
                color = KiwiGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "APP DEVELOPER",
                    color = KiwiGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(36.dp))
            
            Text(
                text = "Connect with me",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Grid of Social Link Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialLink(
                    iconRes = R.drawable.ic_email,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:farjan.swe@gmail.com")
                        }
                        context.startActivity(intent)
                    }
                )
                SocialLink(
                    iconRes = R.drawable.ic_github,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shejanahmmed")))
                    }
                )
                SocialLink(
                    iconRes = R.drawable.ic_linkedin,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/farjan-ahmmed/")))
                    }
                )
                SocialLink(
                    iconRes = R.drawable.ic_facebook,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/beingshejan/")))
                    }
                )
                SocialLink(
                    iconRes = R.drawable.ic_instagram,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/iamshejan/")))
                    }
                )
            }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.05f), androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * A clickable button for a social link icon.
 * 
 * @param iconRes The drawable resource ID for the icon.
 * @param onClick Callback when the icon is clicked.
 */
@Composable
fun SocialLink(iconRes: Int, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AshGrey)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Dialog displaying theme selection options.
 *
 * @param selectedTheme The currently selected theme name.
 * @param onThemeSelected Callback when a new theme is selected.
 * @param onDismiss Callback when the dialog is closed.
 */
@Composable
fun ThemeSelectionDialog(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val view = LocalView.current
    SideEffect {
        (view.parent as? DialogWindowProvider)?.window
            ?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clip(RoundedCornerShape(32.dp))
            .background(DarkGrey)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Theme",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Options
            ThemeOptionItem(
                title = "System Default", 
                icon = Icons.Default.Settings, 
                isSelected = selectedTheme == "System Default",
                onClick = { onThemeSelected("System Default"); onDismiss() }
            )
            ThemeOptionItem(
                title = "Light Mode", 
                icon = Icons.Default.WbSunny, 
                isSelected = selectedTheme == "Light Mode",
                onClick = { onThemeSelected("Light Mode"); onDismiss() }
            )
            ThemeOptionItem(
                title = "Dark Mode", 
                icon = Icons.Default.NightlightRound, 
                isSelected = selectedTheme == "Dark Mode",
                onClick = { onThemeSelected("Dark Mode"); onDismiss() }
            )
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.05f), androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ThemeOptionItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = KiwiGreen,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = if (isSelected) KiwiGreen else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = KiwiGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * The Settings Screen of the Kiwi app.
 * Provides access to support actions, privacy policy, and developer information.
 */
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf("Dark Mode") }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    val isDialogShowing = showThemeDialog || showVersionDialog || showDeveloperDialog
    val scrimAlpha by animateFloatAsState(
        targetValue = if (isDialogShowing) 0.6f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "scrimAlpha"
    )

    // Dialog for Theme Selection
    if (showThemeDialog) {
        Dialog(
            onDismissRequest = { showThemeDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ThemeSelectionDialog(
                selectedTheme = selectedTheme,
                onThemeSelected = { selectedTheme = it },
                onDismiss = { showThemeDialog = false }
            )
        }
    }

    // Dialog for Version Info
    if (showVersionDialog) {
        Dialog(
            onDismissRequest = { showVersionDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            VersionDialog(onDismiss = { showVersionDialog = false })
        }
    }

    // Dialog for Developer Profile
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
            // Header Text
            item {
                Text(
                    text = "Settings",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp, top = 8.dp)
                )
            }

            // Appearance Section Group
            item {
                Column {
                    SectionHeader("Appearance")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AshGrey)
                    ) {
                        appearanceItems.forEachIndexed { index, item ->
                            SettingsItem(
                                item = item,
                                isLastItem = index == appearanceItems.size - 1,
                                onClick = {
                                    when (item.title) {
                                        "App Theme" -> showThemeDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Support Section Group
            item {
                Column {
                    SectionHeader("Support")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AshGrey)
                    ) {
                        supportItems.forEachIndexed { index, item ->
                            SettingsItem(
                                item = item,
                                isLastItem = index == supportItems.size - 1,
                                onClick = {
                                    when (item.title) {
                                        "Privacy Policy" -> {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.farjan.me/KIWIPrivecyPolicy/"))
                                            context.startActivity(intent)
                                        }
                                        "Rate Us" -> {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                                context.startActivity(intent)
                                            }
                                        }
                                        "Share App" -> {
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, "Kiwi QR Generator")
                                                putExtra(Intent.EXTRA_TEXT, "Check out Kiwi, a fast and beautiful QR Code Generator & Scanner app! Download it here: https://play.google.com/store/apps/details?id=${context.packageName}")
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Share Kiwi with"))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // About Section Group
            item {
                Column {
                    SectionHeader("About")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AshGrey)
                    ) {
                        aboutItems.forEachIndexed { index, item ->
                            SettingsItem(
                                item = item,
                                isLastItem = index == aboutItems.size - 1,
                                onClick = {
                                    when (item.title) {
                                        "About Developer" -> showDeveloperDialog = true
                                        "Version Info" -> showVersionDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Version Footer
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

        // Dark scrim overlay for dialog focus
        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
            )
        }
    }
}
