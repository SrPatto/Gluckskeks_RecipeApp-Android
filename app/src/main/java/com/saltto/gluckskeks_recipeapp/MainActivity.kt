package com.saltto.gluckskeks_recipeapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saltto.gluckskeks_recipeapp.navigation.AppNavigation
import com.saltto.gluckskeks_recipeapp.ui.theme.Gluckskeks_RecipeAppTheme
import com.saltto.gluckskeks_recipeapp.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            Gluckskeks_RecipeAppTheme(
                darkTheme = isDarkTheme,
                dynamicColor = false // FORCE your colors
            ) {
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = themeViewModel::toggleTheme
                )
            }
        }
    }
}
