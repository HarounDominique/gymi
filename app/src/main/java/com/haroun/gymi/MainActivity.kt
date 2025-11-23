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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
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

        // --- MAIN SCREEN ---
        composable("main") {
            MainScreen(
                onEmpujeClick = { navController.navigate("push") },
                onTironClick = { navController.navigate("pull") },
                onPiernaClick = { navController.navigate("legs") }
            )
        }

        // --- CATEGORY SCREENS ---
        listOf("push", "pull", "legs").forEach { type ->
            composable(type) {
                val fileName = when (type) {
                    "push" -> "push_tables"
                    "pull" -> "pull_tables"
                    else -> "legs_tables"
                }
                val vm: PushViewModel = viewModel(factory = PushViewModelFactory(context, fileName))
                PushScreen(navController, vm)
            }
        }

        // --- DETAIL SCREENS ---
        listOf("push", "pull", "legs").forEach { routeType ->  // Cambiado de 'type' a 'routeType'
            composable(
                route = "$routeType/exercise/{index}",
                arguments = listOf(
                    navArgument("index") {
                        type = NavType.StringType  // Ahora 'type' se refiere a la propiedad del navArgument
                    }
                )
            ) { backStackEntry ->
                val fileName = when (routeType) {  // Cambiado de 'type' a 'routeType'
                    "push" -> "push_tables"
                    "pull" -> "pull_tables"
                    else -> "legs_tables"
                }
                val vm: PushViewModel = viewModel(factory = PushViewModelFactory(context, fileName))

                val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: -1
                val size = vm.tables.size

                if (index !in 0 until size) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ejercicio no encontrado")
                    }
                    return@composable
                }

                PushExerciseDetailScreen(
                    navController = navController,
                    viewModel = vm,
                    tableIndex = index
                )
            }
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
