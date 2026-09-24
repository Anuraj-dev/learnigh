package com.anuraj.learnigh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.anuraj.learnigh.ui.navigation.LearnighNavHost
import com.anuraj.learnigh.ui.theme.LearnighTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as LearnighApp
        setContent {
            LearnighTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LearnighNavHost(
                        repository = app.repository,
                        settings = app.settings,
                    )
                }
            }
        }
    }
}
