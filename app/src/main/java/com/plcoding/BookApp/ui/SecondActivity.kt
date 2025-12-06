package com.plcoding.material3expressiveguide.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.plcoding.material3expressiveguide.BookApp

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apple Style Theme Overrides
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF007AFF), // iOS Blue
                    background = Color(0xFFF2F2F7), // iOS System Gray 6
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSurface = Color.Black
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Reuse the exact same UI and ViewModel logic
                    // The ViewModel will be instantiated in this new process (:client2)
                    // and will bind to the remote service independently.
                    BookApp(isSecondInstance = true)
                }
            }
        }
    }
}

