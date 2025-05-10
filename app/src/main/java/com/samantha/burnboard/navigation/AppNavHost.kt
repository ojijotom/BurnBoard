package com.samantha.burnboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samantha.burnboard.data.UserDatabase
import com.samantha.burnboard.repository.UserRepository
import com.samantha.burnboard.ui.screens.about.AboutScreen
import com.samantha.burnboard.ui.screens.home.HomeScreen
import com.samantha.burnboard.ui.screens.auth.LoginScreen
import com.samantha.burnboard.ui.screens.auth.RegisterScreen
import com.samantha.burnboard.ui.screens.calories.CaloriesScreen
import com.samantha.burnboard.ui.screens.contact.ContactScreen
import com.samantha.burnboard.ui.screens.dashboard.DashboardScreen
import com.samantha.burnboard.ui.screens.profile.ProfileScreen
import com.samantha.burnboard.ui.screens.settings.SettingsScreen
import com.samantha.burnboard.ui.screens.splash.SplashScreen
import com.samantha.burnboard.ui.screens.step.StepScreen
import com.samantha.burnboard.ui.screens.workout.WorkoutScreen
import com.samantha.burnboard.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {

    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUT_HOME) {
            HomeScreen(navController)
        }
        composable(ROUT_ABOUT) {
            AboutScreen(navController)
        }
        composable(ROUT_CONTACT) {
            ContactScreen(navController)
        }
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUT_DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(ROUT_STEP) {
            StepScreen(navController)
        }
        composable(ROUT_WORKOUT) {
            WorkoutScreen(navController)
        }
        composable(ROUT_CALORIES) {
            CaloriesScreen(navController)
        }
        composable(ROUT_PROFILE) {
            ProfileScreen(navController)
        }
        composable(ROUT_SETTINGS) {
            SettingsScreen(navController)
        }














        //AUTHENTICATION

        // Initialize Room Database and Repository for Authentication
        val appDatabase = UserDatabase.getDatabase(context)
        val authRepository = UserRepository(appDatabase.userDao())
        val authViewModel: AuthViewModel = AuthViewModel(authRepository)
        composable(ROUT_REGISTER) {
            RegisterScreen(authViewModel, navController) {
                navController.navigate(ROUT_LOGIN) {
                    popUpTo(ROUT_REGISTER) { inclusive = true }
                }
            }
        }

        composable(ROUT_LOGIN) {
            LoginScreen(authViewModel, navController) {
                navController.navigate(ROUT_HOME) {
                    popUpTo(ROUT_LOGIN) { inclusive = true }
                }
            }
        }




    }
}