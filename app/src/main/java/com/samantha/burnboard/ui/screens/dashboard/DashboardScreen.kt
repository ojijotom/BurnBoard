package com.samantha.burnboard.ui.screens.dashboard

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import com.samantha.burnboard.R
import com.samantha.burnboard.navigation.ROUT_ABOUT
import com.samantha.burnboard.navigation.ROUT_CALORIES
import com.samantha.burnboard.navigation.ROUT_HOME
import com.samantha.burnboard.navigation.ROUT_PROFILE
import com.samantha.burnboard.navigation.ROUT_SETTINGS
import com.samantha.burnboard.navigation.ROUT_STEP
import com.samantha.burnboard.navigation.ROUT_WORKOUT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Entity
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val value: String
)

@Dao
interface ActivityDao {
    @Query("SELECT * FROM Activity")
    suspend fun getAllActivities(): List<Activity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)
}

@Database(entities = [Activity::class], version = 1)
abstract class ActivityDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile private var INSTANCE: ActivityDatabase? = null

        fun getDatabase(context: Application): ActivityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ActivityDatabase::class.java,
                    "activity_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val activityDao = ActivityDatabase.getDatabase(application).activityDao()

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities

    init {
        loadActivities()
    }

    fun insertActivity(activity: Activity) {
        viewModelScope.launch {
            activityDao.insertActivity(activity)
            loadActivities()
        }
    }

    private fun loadActivities() {
        viewModelScope.launch {
            _activities.value = activityDao.getAllActivities()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, viewModel: ActivityViewModel = viewModel()) {
    val userName = "Samantha"
    val steps = 7500
    val calories = 560
    val workoutMinutes = 45

    val activities by viewModel.activities.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome back, $userName!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActivityCard(
                title = "Steps",
                value = steps.toString(),
                icon = R.drawable.img,
                onClick = { navController.navigate(ROUT_STEP) }
            )
            ActivityCard(
                title = "Calories",
                value = calories.toString(),
                icon = R.drawable.img,
                onClick = { navController.navigate(ROUT_CALORIES) }
            )
            ActivityCard(
                title = "Workout",
                value = "${workoutMinutes} min",
                icon = R.drawable.img,
                onClick = { navController.navigate(ROUT_WORKOUT) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Workouts",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(activities.size) { index ->
                val activity = activities[index]
                WorkoutCard(
                    title = activity.title,
                    duration = activity.value,
                    calories = "N/A"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                NavigationCard(
                    title = "Home",
                    icon = R.drawable.img,
                    onClick = { navController.navigate(ROUT_HOME) }
                )
            }
            item {
                NavigationCard(
                    title = "About",
                    icon = R.drawable.img,
                    onClick = { navController.navigate(ROUT_ABOUT) }
                )
            }
            item {
                NavigationCard(
                    title = "Settings",
                    icon = R.drawable.img,
                    onClick = {navController.navigate(ROUT_SETTINGS) }
                )
            }
            item {
                NavigationCard(
                    title = "Profile",
                    icon = R.drawable.img,
                    onClick = {navController.navigate(ROUT_PROFILE) }
                )
            }
        }
    }
}

@Composable
fun ActivityCard(title: String, value: String, icon: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color(0xFF00C6FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Text(text = title, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun WorkoutCard(title: String, duration: String, calories: String) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(end = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Duration: $duration", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Text(text = "Calories: $calories", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun NavigationCard(title: String, icon: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = Color(0xFF00C6FF),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(rememberNavController())
}
