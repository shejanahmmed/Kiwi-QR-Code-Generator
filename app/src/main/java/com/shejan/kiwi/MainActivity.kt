package com.shejan.kiwi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shejan.kiwi.ui.HomeScreen
import com.shejan.kiwi.ui.SettingsScreen
import com.shejan.kiwi.ui.HistoryScreen
import com.shejan.kiwi.ui.ScannerScreen
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import com.shejan.kiwi.ui.theme.KiwiTheme

import androidx.activity.SystemBarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import com.shejan.kiwi.logic.ThemeManager

/**
 * Main Activity of the Kiwi app.
 * Handles the overall navigation structure, edge-to-edge display, 
 * and incoming share intents from other apps.
 */
class MainActivity : ComponentActivity() {
    private var sharedTextState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Process any text shared from other apps
        handleIntent(intent)
        
        // Initialize Theme Persistence
        ThemeManager.init(this)
        
        // Automatically handles light/dark system bar icons based on system theme
        enableEdgeToEdge()
        setContent {
            val themePreference by ThemeManager.themeFlow.collectAsState()
            val isDarkTheme = when (themePreference) {
                "Light Mode" -> false
                "Dark Mode" -> true
                else -> isSystemInDarkTheme() // "System"
            }

            KiwiTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val sharedText by sharedTextState
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        FloatingNavBar(navController)
                    }
                ) { innerPadding ->
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(
                            if (currentRoute == "scanner") PaddingValues(0.dp) else innerPadding
                        )
                    ) {
                        composable("home") { 
                            HomeScreen(initialUrl = sharedText) 
                            // Reset state after consumption to prevent re-triggering
                            LaunchedEffect(sharedText) {
                                if (sharedText != null) {
                                    sharedTextState.value = null
                                }
                            }
                        }
                        composable("settings") { SettingsScreen() }
                        composable("history") { HistoryScreen() }
                        composable("scanner") { ScannerScreen() }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    /**
     * Extracts text from an incoming ACTION_SEND intent.
     * @param intent The intent to handle.
     */
    private fun handleIntent(intent: android.content.Intent?) {
        if (intent?.action == android.content.Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(android.content.Intent.EXTRA_TEXT)?.let { incomingText ->
                sharedTextState.value = incomingText
            }
        }
    }
}

/**
 * A floating navigation bar component shown at the bottom of the screen.
 * @param navController The navigation controller used for switching routes.
 */
@Composable
fun FloatingNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        NavigationItem("home", R.drawable.ic_home),
        NavigationItem("scanner", R.drawable.ic_scanner),
        NavigationItem("history", R.drawable.ic_history),
        NavigationItem("settings", R.drawable.ic_settings)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(22.dp)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                IconButton(
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.route,
                        tint = if (currentRoute == item.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Data class representing a navigation item in the bottom bar.
 * @property route The navigation route associated with this item.
 * @property iconRes The drawable resource ID for the item's icon.
 */
data class NavigationItem(val route: String, val iconRes: Int)