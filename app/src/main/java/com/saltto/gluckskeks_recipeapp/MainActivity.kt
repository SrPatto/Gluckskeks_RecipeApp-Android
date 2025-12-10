package com.saltto.gluckskeks_recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saltto.gluckskeks_recipeapp.navigation.AppNavigation
import com.saltto.gluckskeks_recipeapp.ui.theme.Gluckskeks_RecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Gluckskeks_RecipeAppTheme {
                GluckskeksApp()
            }
        }
    }
}

@Composable
fun GluckskeksApp() {
    AppNavigation()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Gluckskeks_RecipeAppTheme {
        GluckskeksApp()
    }
}