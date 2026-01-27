package com.example.sunotemplategen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.sunotemplategen.domain.SunoPromptGenerator
import com.example.sunotemplategen.ui.MainScreen
// import com.example.sunotemplategen.ui.theme.SunoTemplateGenTheme // Placeholder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // TODO: Get API Key from secure source or BuildConfig
        val apiKey = "YOUR_GEMINI_API_KEY" 
        val generator = SunoPromptGenerator(apiKey)

        setContent {
            MaterialTheme { // Using default MaterialTheme for prototype
                Surface {
                    MainScreen(generator)
                }
            }
        }
    }
}
