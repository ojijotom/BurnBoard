package com.samantha.burnboard.ui.screens.calories

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Entity(tableName = "calorie_table")
data class CalorieRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caloriesBurned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface CalorieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CalorieRecord)

    @Query("SELECT * FROM calorie_table ORDER BY timestamp DESC")
    suspend fun getAll(): List<CalorieRecord>
}

@Database(entities = [CalorieRecord::class], version = 1)
abstract class CalorieDatabase : RoomDatabase() {
    abstract fun calorieDao(): CalorieDao

    companion object {
        @Volatile private var instance: CalorieDatabase? = null

        fun getDatabase(context: Context): CalorieDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CalorieDatabase::class.java,
                    "calorie_db"
                ).build().also { instance = it }
            }
        }
    }
}

@Composable
fun CaloriesScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { CalorieDatabase.getDatabase(context) }
    val dao = db.calorieDao()
    val scope = rememberCoroutineScope()

    var caloriesBurned by remember { mutableStateOf(0) }
    var isCounting by remember { mutableStateOf(false) }

    LaunchedEffect(isCounting) {
        while (isCounting) {
            delay(10_000)
            caloriesBurned += 5
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Calories Burned",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$caloriesBurned kcal",
            style = MaterialTheme.typography.displaySmall,
            color = Color(0xFF00BFA5),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isCounting = !isCounting
                if (!isCounting && caloriesBurned > 0) {
                    scope.launch(Dispatchers.IO) {
                        dao.insert(CalorieRecord(caloriesBurned = caloriesBurned))
                        caloriesBurned = 0
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCounting) Color.Red else Color(0xFF00BFA5)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isCounting) "Stop Workout" else "Start Workout",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Keep moving to burn more calories!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CaloriesScreenPreview() {
    CaloriesScreen(navController = rememberNavController())
}
