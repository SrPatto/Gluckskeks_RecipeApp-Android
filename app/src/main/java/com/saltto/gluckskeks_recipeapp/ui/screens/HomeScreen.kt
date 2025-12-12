package com.saltto.gluckskeks_recipeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saltto.gluckskeks_recipeapp.navigation.Routes
import com.saltto.gluckskeks_recipeapp.ui.components.RecipeCard
import com.saltto.gluckskeks_recipeapp.ui.components.ReturnButton

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val recipeList = remember { mutableStateListOf<String>() }
    var isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val recipesRef = FirebaseDatabase.getInstance().reference.child("recipes")

        recipesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                for (recipe in snapshot.children) {
                    recipeList.add(recipe.key.toString())   // store recipe ID
                }
                isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text("GLUCKSKEKS", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading.value -> {
                    item { Text("Loading recipes...") }
                }

                recipeList.isEmpty() -> {
                    item { Text("No recipes yet — upload one!") }
                }

                else -> {
                    items(recipeList.size) { index ->
                        RecipeCard(recipeList[index]) {
                            navController.navigate("recipe/${recipeList[index]}")
                        }
                    }
                }
            }
        }
    }
}