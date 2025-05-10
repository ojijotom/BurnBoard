package com.samantha.burnboard.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.samantha.burnboard.navigation.ROUT_ABOUT
import com.samantha.burnboard.navigation.ROUT_LOGIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val darkBackground = Color(0xFF121212)
    val primaryText = Color.White
    val accent = Color(0xFFFF9800)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = primaryText) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1F1F1F))
            )
        },
        containerColor = darkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SettingsItem(icon = Icons.Default.Person, title = "Profile", onClick = {
                navController.navigate("profile")
            })

            SettingsItem(icon = Icons.Default.Notifications, title = "Notifications", onClick = {
                // handle click
            })

            SettingsItem(icon = Icons.Default.DarkMode, title = "Dark Mode", onClick = {
                // toggle dark mode
            })

            SettingsItem(icon = Icons.Default.Info, title = "About", onClick = {navController.navigate(
                ROUT_ABOUT
            )
                // show about dialog
            })

            SettingsItem(icon = Icons.Default.Logout, title = "Logout", onClick = {navController.navigate(
                ROUT_LOGIN
            )
                // handle logout
            })
        }
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFFF9800),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
