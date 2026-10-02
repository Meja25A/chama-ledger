package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.ChamaTheme
import com.example.ui.viewmodel.ChamaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ChamaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChamaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}
