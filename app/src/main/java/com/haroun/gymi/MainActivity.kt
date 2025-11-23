// app/src/main/java/com/haroun/gymi/MainActivity.kt
package com.haroun.gymi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.haroun.gymi.ui.theme.GymiTheme
import com.haroun.gymi.ui.push.PushScreen
import com.haroun.gymi.ui.push.PushExerciseDetailScreen
import com.haroun.gymi.persistence.PushViewModel
import com.haroun.gymi.persistence.PushViewModelFactory

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

        composable("main") {
            MainScreen(
                onEmpujeClick = { navController.navigate("push") },
                onTironClick = { navController.navigate("pull") },
                onPiernaClick = { navController.navigate("legs") }
            )
        }

        // --- PUSH main screen ---
        composable("push") {
            val vm: PushViewModel = viewModel(
                factory = PushViewModelFactory(context = context, fileName = "push_tables")
            )
            PushScreen(navController = navController, viewModel = vm)
        }

        // --- PUSH detail screen ---
        composable(
            route = "push/exercise/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->

            // 👇 CORREGIDO: remember kullanımı con key
            val parentEntry = remember(key1 = navController) {
                navController.getBackStackEntry("push")
            }

            val vm: PushViewModel = viewModel(
                parentEntry,
                factory = PushViewModelFactory(context = context, fileName = "push_tables")
            )

            val index = backStackEntry.arguments?.getInt("index") ?: 0
            PushExerciseDetailScreen(
                navController = navController,
                viewModel = vm,
                tableIndex = index
            )
        }

        // --- PULL screen ---
        composable("pull") {
            val vm: PushViewModel = viewModel(
                factory = PushViewModelFactory(context = context, fileName = "pull_tables")
            )
            PushScreen(navController = navController, viewModel = vm)
        }

        // --- LEGS screen ---
        composable("legs") {
            val vm: PushViewModel = viewModel(
                factory = PushViewModelFactory(context = context, fileName = "legs_tables")
            )
            PushScreen(navController = navController, viewModel = vm)
        }
    }
}

/* Keep your MainScreen composable here or import it; same as previous */
@Composable
fun MainScreen(
    onEmpujeClick: () -> Unit,
    onTironClick: () -> Unit,
    onPiernaClick: () -> Unit
) {
    Scaffold { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
