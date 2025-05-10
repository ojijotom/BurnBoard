package com.samantha.burnboard.ui.screens.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Dashboard
import com.samantha.burnboard.navigation.ROUT_WORKOUT

import kotlinx.coroutines.launch

// ==========================
// Room Database Components
// ==========================
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stepCount: Int
)

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: Workout)

    @Query("SELECT * FROM workouts ORDER BY id DESC LIMIT 1")
    suspend fun getLastWorkout(): Workout?
}

@Database(entities = [Workout::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==========================
// UI Constants
// ==========================
val Teal = Color(0xFF00BFA5)
val DeepBlue = Color(0xFF0D47A1)

// ==========================
// Main Composable
// ==========================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var stepCount by remember { mutableStateOf(0) }

    val workoutDao = remember { AppDatabase.getDatabase(context).workoutDao() }
    val coroutineScope = rememberCoroutineScope()

    // Load previous workout
    LaunchedEffect(Unit) {
        val lastWorkout = workoutDao.getLastWorkout()
        lastWorkout?.let { stepCount = it.stepCount }
    }

    // Save workout
    fun saveWorkout() {
        val workout = Workout(stepCount = stepCount)
        coroutineScope.launch {
            workoutDao.insertWorkout(workout)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BurnBoard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepBlue)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = DeepBlue,
                contentColor = Color.White
            ) {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home"
                    )
                }
                IconButton(onClick = { navController.navigate(ROUT_WORKOUT) }) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = "Workout"
                    )
                }
                IconButton(onClick = { navController.navigate("dashboard") }) {
                    Icon(
                        imageVector = Icons.Filled.Dashboard,
                        contentDescription = "Dashboard"
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(paddingValues)  // Use padding from Scaffold
                    .padding(16.dp),  // Optional extra padding
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Teal, DeepBlue)))
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Welcome to BurnBoard 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Log. Track. Transform.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Buttons (replaced with icons in the BottomAppBar)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Removed buttons since they are now in the BottomAppBar
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Quote
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1F1F1F),
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = "\"Fitness is not about being better than someone else... it's about being better than you used to be.\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB0BEC5),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Save Progress Button
                Button(
                    onClick = {
                        saveWorkout()
                        stepCount = 0
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
                ) {
                    Text("Save Progress", color = Color.White)
                }
            }
        }
    )
}

// ==========================
// Reusable Button Composable
// ==========================
@Composable
fun ActionButton(label: String, backgroundColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==========================
// Preview
// ==========================
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
