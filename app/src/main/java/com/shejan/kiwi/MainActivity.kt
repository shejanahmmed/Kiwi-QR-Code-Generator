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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shejan.kiwi.ui.HomeScreen
import com.shejan.kiwi.ui.SettingsScreen
import com.shejan.kiwi.ui.HistoryScreen
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import com.shejan.kiwi.ui.theme.KiwiTheme

import androidx.activity.SystemBarStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            KiwiTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = AmoledBlack,
                    bottomBar = {
                        FloatingNavBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen() }
                        composable("settings") { SettingsScreen() }
                        composable("history") { HistoryScreen() }
                        // Future routes: scanner
                        composable("scanner") { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Scanner Coming Soon", color = Color.White) } }
                    }
                }
            }
        }
    }
}

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
                .background(DarkGrey)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(22.dp)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                IconButton(
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo("home") {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.route,
                        tint = if (currentRoute == item.route) KiwiGreen else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

data class NavigationItem(val route: String, val iconRes: Int)