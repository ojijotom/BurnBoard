package com.samantha.burnboard.ui.screens.step

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 1. ROOM DATABASE

@Entity(tableName = "step_counter")
data class StepCounter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stepCount: Int
)

@Dao
interface StepCounterDao {
    @Insert
    suspend fun insertStepCounter(stepCounter: StepCounter)

    @Query("SELECT * FROM step_counter ORDER BY id DESC LIMIT 1")
    suspend fun getLatestStepCount(): StepCounter?
}

@Database(entities = [StepCounter::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepCounterDao(): StepCounterDao
}

// 2. VIEWMODEL

class StepViewModel(application: Application) : AndroidViewModel(application) {
    private val stepCounterDao: StepCounterDao = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "step_counter_db"
    ).build().stepCounterDao()

    private val _stepCount = mutableStateOf(0)
    val stepCount: State<Int> = _stepCount

    init {
        viewModelScope.launch {
            val latestStep = stepCounterDao.getLatestStepCount()
            _stepCount.value = latestStep?.stepCount ?: 0
        }
    }

    fun incrementStep() {
        _stepCount.value++
        saveStepCount(_stepCount.value)
    }

    fun resetStep() {
        _stepCount.value = 0
        saveStepCount(0)
    }

    private fun saveStepCount(stepCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            stepCounterDao.insertStepCounter(StepCounter(stepCount = stepCount))
        }
    }
}

// 3. COMPOSABLE UI

@Composable
fun StepScreen(navController: NavController) {
    val stepViewModel: StepViewModel = viewModel()
    var isCounting by remember { mutableStateOf(false) }
    val stepCount by stepViewModel.stepCount

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isCounting) {
        if (isCounting) {
            while (isCounting) {
                delay(1000) // simulate 1 step per second
                stepViewModel.incrementStep()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Workout Step Counter",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "$stepCount Steps",
            style = MaterialTheme.typography.displayMedium,
            color = Color.Cyan
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { isCounting = !isCounting },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCounting) Color.Red else Color.Green
            )
        ) {
            Text(text = if (isCounting) "Pause" else "Start")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                stepViewModel.resetStep()
                isCounting = false
            }
        ) {
            Text("Reset")
        }
    }
}

// 4. PREVIEW

@Preview(showBackground = true)
@Composable
fun StepScreenPreview() {
    StepScreen(navController = rememberNavController())
}
