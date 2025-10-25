package com.genxsolutions.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.genxsolutions.myapplication.ui.screens.getstarted.GetStartedScreen
import com.genxsolutions.myapplication.ui.screens.home.HomeScreen
import com.genxsolutions.myapplication.ui.theme.PDFConverterPDFReaderTheme
import com.genxsolutions.myapplication.utils.PreferencesManager

class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        preferencesManager = PreferencesManager(this)
        
        setContent {
            PDFConverterPDFReaderTheme {
                AppNavigation(
                    isFirstTime = preferencesManager.isFirstTime(),
                    onGetStartedClick = {
                        preferencesManager.setFirstTimeDone()
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    isFirstTime: Boolean,
    onGetStartedClick: () -> Unit
) {
    var showGetStarted by remember { mutableStateOf(isFirstTime) }

    if (showGetStarted) {
        GetStartedScreen(
            onGetStartedClick = {
                onGetStartedClick()
                showGetStarted = false
            }
        )
    } else {
        HomeScreen()
    }
}