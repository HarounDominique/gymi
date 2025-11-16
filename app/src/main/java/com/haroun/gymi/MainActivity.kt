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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.haroun.gymi.ui.theme.GymiTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haroun.gymi.persistence.PushViewModel
import com.haroun.gymi.persistence.PushViewModelFactory
import com.haroun.gymi.persistence.ExerciseStorage
import com.haroun.gymi.ui.push.PushScreen

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
                    val navController = rememberNavController()

                    NavHost(navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(
                                onEmpujeClick = { navController.navigate("push") }
                            )
                        }
                        composable("push") { backStackEntry ->
                            // Creamos el ViewModel usando Factory
                            val pushViewModel: PushViewModel = viewModel(
                                factory = PushViewModelFactory(
                                    storage = ExerciseStorage(context = this@MainActivity)
                                )
                            )
                            PushScreen(
                                onBack = { navController.popBackStack() },
                                viewModel = pushViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MainScreen(onEmpujeClick: () -> Unit = {}) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onEmpujeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Empuje")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Tirón */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tirón")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Pierna */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pierna")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GymiTheme {
        MainScreen()
    }
}
