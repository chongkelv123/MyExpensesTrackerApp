package com.example.myexpensestrackerapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.myexpensestrackerapp.navigation.ExpenseTrackerNavigation
import com.example.myexpensestrackerapp.ui.theme.ExpenseTrackerAppTheme
import com.example.myexpensestrackerapp.ui.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    // Initialize the ViewModel using the standard Android ViewModel pattern
    private lateinit var expenseViewModel: ExpenseViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        val repository = (application as ExpenseTrackerApplication).repository
        expenseViewModel = ViewModelProvider(this,
            ExpenseViewModel.Factory(repository))[ExpenseViewModel::class.java]

        // Enable edge-to-edge design
        enableEdgeToEdge()

        setContent {
            ExpenseTrackerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the viewModel to the navigation to share it across screens
                    ExpenseTrackerNavigation(viewModel = expenseViewModel)
                }
            }
        }
    }
}