package com.samantha.burnboard.ui.screens.workout

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- Data + Room Setup ---
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val instructions: String,
    val duration: String,
    val category: String
)

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    @Query("SELECT * FROM workouts")
    fun getAll(): LiveData<List<Workout>>
}

@Database(entities = [Workout::class], version = 1)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

// --- ViewModel ---
class WorkoutViewModel(context: Context) : ViewModel() {
    private val workoutDao = WorkoutDatabase.getDatabase(context).workoutDao()
    val workouts: LiveData<List<Workout>> = workoutDao.getAll()


    fun addWorkout(workout: Workout) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutDao.insert(workout)
        }
    }
}

class WorkoutViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutViewModel(context) as T
    }
}

// --- UI ---
@Composable
fun WorkoutScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(context))
    val workouts by viewModel.workouts.observeAsState(initial = emptyList())
    val categorized = workouts.groupBy { it.category }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "🏋️ Workout Categories",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        categorized.forEach { (category, list) ->
            item {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                list.forEach { workout ->
                    WorkoutCard(workout)
                }
            }
        }

        item {
            AddWorkoutForm(viewModel)
        }
    }
}

@Composable
fun WorkoutCard(workout: Workout) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = workout.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🕒 ${workout.duration}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF80CBC4)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = workout.instructions,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCCCCCC),
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun AddWorkoutForm(viewModel: WorkoutViewModel) {
    var title by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "➕ Add New Workout",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        StyledTextField("Title", title) { title = it }
        StyledTextField("Instructions", instructions) { instructions = it }
        StyledTextField("Duration", duration) { duration = it }
        StyledTextField("Category", category) { category = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && instructions.isNotBlank() && duration.isNotBlank() && category.isNotBlank()) {
                    viewModel.addWorkout(
                        Workout(title = title, instructions = instructions, duration = duration, category = category)
                    )
                    title = ""; instructions = ""; duration = ""; category = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Workout", color = Color.White)
        }
    }
}

@Composable
fun StyledTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00E676),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF00E676),
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun WorkoutScreenPreview() {
    WorkoutScreen(navController = rememberNavController())
}
