package com.haroun.gymi

import android.annotation.SuppressLint
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
                                onEmpujeClick = { navController.navigate("push") },
                                onTironClick = { navController.navigate("pull") },
                                onPiernaClick = { navController.navigate("legs") }
                            )
                        }

                        composable("push") {
                            val vm: PushViewModel = viewModel(
                                factory = PushViewModelFactory(
                                    context = this@MainActivity,
                                    fileName = "push_tables"
                                )
                            )
                            PushScreen(onBack = { navController.popBackStack() }, viewModel = vm)
                        }

                        composable("pull") {
                            val vm: PushViewModel = viewModel(
                                factory = PushViewModelFactory(
                                    context = this@MainActivity,
                                    fileName = "pull_tables"
                                )
                            )
                            PushScreen(onBack = { navController.popBackStack() }, viewModel = vm)
                        }

                        composable("legs") {
                            val vm: PushViewModel = viewModel(
                                factory = PushViewModelFactory(
                                    context = this@MainActivity,
                                    fileName = "legs_tables"
                                )
                            )
                            PushScreen(onBack = { navController.popBackStack() }, viewModel = vm)
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    onEmpujeClick: () -> Unit,
    onTironClick: () -> Unit,
    onPiernaClick: () -> Unit
) {
    Scaffold {
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
        MainScreen(
            onEmpujeClick = {},
            onTironClick = {},
            onPiernaClick = {}
        )
    }
}

