package com.haroun.gymi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.haroun.gymi.ui.theme.GymiTheme
import com.haroun.gymi.ui.push.PushScreen
import com.haroun.gymi.ui.push.PushExerciseDetailScreen

// Factories y ViewModels separados
import com.haroun.gymi.persistence.push.PushViewModel
import com.haroun.gymi.persistence.push.PushViewModelFactory
import com.haroun.gymi.persistence.pull.PullViewModel
import com.haroun.gymi.persistence.pull.PullViewModelFactory
import com.haroun.gymi.persistence.legs.LegsViewModel
import com.haroun.gymi.persistence.legs.LegsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "main") {

        // --- MAIN SCREEN ---
        composable("main") {
            MainScreen(
                onEmpujeClick = { navController.navigate("push") },
                onTironClick = { navController.navigate("pull") },
                onPiernaClick = { navController.navigate("legs") }
            )
        }

        // --- PUSH ---
        composable("push") {
            val pushViewModel: com.haroun.gymi.persistence.ExerciseViewModel = viewModel<PushViewModel>(
                factory = PushViewModelFactory(context, "push_tables")
            )
            PushScreen(navController, pushViewModel as PushViewModel)
        }

        composable(
            "push/exercise/{index}",
            arguments = listOf(navArgument("index") { this.type = NavType.StringType })
        ) { backStackEntry ->
            val pushViewModel: com.haroun.gymi.persistence.ExerciseViewModel = viewModel<PushViewModel>(
                factory = PushViewModelFactory(context, "push_tables")
            )
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: -1

            if (index !in 0 until pushViewModel.tables.size) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ejercicio no encontrado")
                }
                return@composable
            }

            PushExerciseDetailScreen(navController, pushViewModel as PushViewModel, index)
        }

        // --- PULL ---
        composable("pull") {
            val pullViewModel: com.haroun.gymi.persistence.ExerciseViewModel = viewModel<PullViewModel>(
                factory = PullViewModelFactory(context, "pull_tables")
            )
            PushScreen(navController, pullViewModel as PushViewModel)
        }

        composable(
            "pull/exercise/{index}",
            arguments = listOf(navArgument("index") { this.type = NavType.StringType })
        ) { backStackEntry ->
            val pullViewModel: com.haroun.gymi.persistence.ExerciseViewModel = viewModel<PullViewModel>(
                factory = PullViewModelFactory(context, "pull_tables")
            )
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: -1

            if (index !in 0 until pullViewModel.tables.size) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ejercicio no encontrado")
                }
                return@composable
            }

            PushExerciseDetailScreen(navController, pullViewModel as PushViewModel, index)
        }

        // --- LEGS ---
        composable("legs") {
            val legsViewModel: com.haroun.gymi.persistence.ExerciseViewModel = viewModel<LegsViewModel>(
                factory = LegsViewModelFactory(context, "legs_tables")
            )
            PushScreen(navController, legsViewModel as PushViewModel)
        }

        composable(
            "legs/exercise/{index}",
            arguments = listOf(navArgument("index") { this.type = NavType.StringType })
        ) { backStackEntry ->
            val legsViewModel: com.haroun.gymi.persistence.ExerciseViewModel = viewModel<LegsViewModel>(
                factory = LegsViewModelFactory(context, "legs_tables")
            )
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: -1

            if (index !in 0 until legsViewModel.tables.size) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ejercicio no encontrado")
                }
                return@composable
            }

            PushExerciseDetailScreen(navController, legsViewModel as PushViewModel, index)
        }
    }
}

/* MainScreen composable */
@Composable
fun MainScreen(
    onEmpujeClick: () -> Unit,
    onTironClick: () -> Unit,
    onPiernaClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onEmpujeClick, modifier = Modifier.fillMaxWidth()) {
                Text("Empuje")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onTironClick, modifier = Modifier.fillMaxWidth()) {
                Text("Tirón")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onPiernaClick, modifier = Modifier.fillMaxWidth()) {
                Text("Pierna")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GymiTheme {
        MainScreen(onEmpujeClick = {}, onTironClick = {}, onPiernaClick = {})
    }
}