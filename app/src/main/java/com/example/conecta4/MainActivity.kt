package com.example.conecta4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.conecta4.ui.theme.Conecta4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Conecta4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
    }
}


//funcion para botones

object NavigationGuard {
    private var lastClickTime = 0L
    private const val CLICK_INTERVAL = 800L

    fun canNavigate(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= CLICK_INTERVAL) {
            lastClickTime = currentTime
            return true
        }
        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AppNavigation() {

}







