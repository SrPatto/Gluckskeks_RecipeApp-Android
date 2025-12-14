package com.saltto.gluckskeks_recipeapp.navigation

import android.app.Application
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.saltto.gluckskeks_recipeapp.ui.components.SettingsDropdownMenu
import com.saltto.gluckskeks_recipeapp.ui.screens.AddRecipeScreen
import com.saltto.gluckskeks_recipeapp.ui.screens.EditRecipeScreen
import com.saltto.gluckskeks_recipeapp.ui.screens.HomeScreen
import com.saltto.gluckskeks_recipeapp.ui.screens.LoginScreen
import com.saltto.gluckskeks_recipeapp.ui.screens.ProfileScreen
import com.saltto.gluckskeks_recipeapp.ui.screens.RecipeScreen
import com.saltto.gluckskeks_recipeapp.ui.screens.SignUpScreen


object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "sign-up"
    const val HOME = "home"
    const val PROFILE = "profile"

    const val RECIPE = "recipe/{recipeId}"
    const val ADD_RECIPE = "add_recipe"
    const val EDIT_RECIPE = "edit_recipe/{recipeId}"
}

enum class DestinationNavBar(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("home", "Home", Icons.Default.Home, "Main Menu"),
    ADD_RECIPE("add_recipe", "New Recipe", Icons.Default.AddCircle, "New Recipe"),
    PROFILE("profile", "Profile", Icons.Default.Person, "Go to Profile")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    NavHost(navController, startDestination = startDestination) {
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.SIGNUP) { SignUpScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController, modifier) }
        composable(Routes.PROFILE) { ProfileScreen(navController, modifier) }
        composable(Routes.ADD_RECIPE) { AddRecipeScreen(navController, modifier) }
        composable(Routes.RECIPE) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            RecipeScreen(navController, recipeID = recipeId, modifier)
        }

        composable(Routes.EDIT_RECIPE) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            EditRecipeScreen(navController, recipeID = recipeId, modifier)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) {
        Routes.HOME
    } else {
        Routes.LOGIN
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Get the current destination
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val hideBottomBar = currentRoute?.let {
        it.startsWith("login") ||
                it.startsWith("sign-up") ||
                it.startsWith("add_recipe") ||
                it.startsWith("edit_recipe")
    } ?: false

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentRoute == Routes.HOME) {
                TopAppBar(
                    title = {
                        Text(
                            "GLUCKSKEKS",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        SettingsDropdownMenu(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = onToggleTheme
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (!hideBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    DestinationNavBar.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(Routes.HOME)
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = {
                                Text(
                                    destination.label,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(contentPadding),
            startDestination = startDestination
        )
    }
}


