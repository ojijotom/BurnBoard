package com.samantha.burnboard.ui.screens.about



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.samantha.burnboard.ui.theme.newblue

// Define custom colors for this screen
val Teal = Color(0xFF00BFA5)
val DeepBlue = Color(0xFF0D47A1)
val CardBackground = Color(0xFF1A1A1A)

@Composable
fun AboutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(rememberScrollState())
    ) {
        // Top header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Teal, newblue, DeepBlue)
                    )
                )
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "About BurnBoard",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main content card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = CardBackground,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BurnBoard is your intelligent fitness companion. Whether you're walking your first mile or crushing daily workouts, BurnBoard empowers you to track progress, stay inspired, and achieve your goals—step by step.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFB0C4DE),
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "🚀 Key Features",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Teal
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                FeatureItem("🏃 Step Tracking - Real-time step monitoring with visual feedback.")
                FeatureItem("🔥 Calorie Burn - Intuitive calorie tracking based on activity.")
                FeatureItem("📊 Dashboard - Clean summaries of your fitness data.")
                FeatureItem("🧘 Workout Logging - Quickly log and review workout history.")


                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Our mission is to simplify fitness. BurnBoard makes it easy to stay active, be accountable, and make every step count—on your terms.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF9DAEBF),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Divider(
                    thickness = 1.dp,
                    color = Color(0xFF2C3E50)
                )

                Spacer(modifier = Modifier.height(12.dp))


            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FeatureItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFF81D4FA),
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen(navController = rememberNavController())
}


