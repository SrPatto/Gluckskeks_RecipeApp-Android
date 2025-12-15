package com.saltto.gluckskeks_recipeapp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.saltto.gluckskeks_recipeapp.R
import com.saltto.gluckskeks_recipeapp.ui.components.FavoriteIconButton
import com.saltto.gluckskeks_recipeapp.ui.components.ReturnButton
import com.saltto.gluckskeks_recipeapp.ui.components.TagCardUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@Composable
fun RecipeScreen(
    navController: NavHostController,
    recipeID: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val favoritesRef = FirebaseDatabase.getInstance()
        .getReference("favorites")
        .child(uid ?: "")

    var isFavorite by remember { mutableStateOf(false) }

    var authorID by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var userPhoto by remember { mutableStateOf<String?>(null) }

    var recipeName by remember { mutableStateOf("") }
    var recipeDescription by remember { mutableStateOf("") }
    var recipeIngredients by remember { mutableStateOf(listOf<String>()) }
    var recipeInstructions by remember { mutableStateOf("") }
    var recipeImg by remember { mutableStateOf<String?>(null) }
    var recipeCategories by remember { mutableStateOf(listOf<String>()) }

    val recipeRef = FirebaseDatabase.getInstance().reference.child("recipes").child(recipeID)

    val shareRecipe = {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(recipeImg)
                val input = url.openStream()


                val file = File(context.cacheDir, "recipe_$recipeID.jpg")
                file.outputStream().use { input.copyTo(it) }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                val shareText = """
                🍽 *$recipeName*

                📄 *Description:*
                $recipeDescription

                🥣 *Ingredients:*
                ${recipeIngredients.joinToString(", ")}

                🔪 *Instructions:*
                $recipeInstructions
            """.trimIndent()

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(
                    Intent.createChooser(intent, "Share recipe")
                )

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error sharing recipe", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(recipeID) {
        recipeRef.get().addOnSuccessListener { snap ->
            authorID = snap.child("author").value?.toString() ?: ""
            recipeName = snap.child("name").value?.toString() ?: "No name"
            recipeDescription = snap.child("description").value?.toString() ?: "No description"
            recipeIngredients =
                snap.child("ingredients").children.map { it.getValue(String::class.java) ?: "" }
            recipeInstructions = snap.child("instructions").value?.toString() ?: "No instructions"
            recipeImg = snap.child("picture").value?.toString()

            // Convert list from Firebase to List<String>
            recipeCategories =
                snap.child("categories").children.map { it.getValue(String::class.java) ?: "" }

        }
    }

    LaunchedEffect(authorID) {
        if (authorID.isNotEmpty()) {
            FirebaseDatabase.getInstance().reference.child("users").child(authorID)
                .get().addOnSuccessListener { snap ->
                    username = snap.child("username").value?.toString() ?: "Unknown User"
                    userPhoto = snap.child("photoUrl").value?.toString()
                }
        }
    }

    LaunchedEffect(recipeID, uid) {
        if (uid == null) return@LaunchedEffect

        favoritesRef.child(recipeID)
            .get()
            .addOnSuccessListener { snapshot ->
                isFavorite = snapshot.exists()
            }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReturnButton {
                navController.popBackStack()
            }
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(8.dp,0.dp, 8.dp, 2.dp)) {
                IconButton(onClick = { shareRecipe() }) {
                    Icon(
                        painter = painterResource(R.drawable.share_icon),
                        contentDescription = "Share Recipe"
                    )
                }
                FavoriteIconButton(
                    isFavorite = isFavorite,
                    onToggle = { newValue ->
                        if (uid == null) return@FavoriteIconButton

                        if (newValue) {
                            FirebaseDatabase.getInstance()
                                .getReference("favorites")
                                .child(uid)
                                .child(recipeID)
                                .setValue(true)
                        } else {
                            FirebaseDatabase.getInstance()
                                .getReference("favorites")
                                .child(uid)
                                .child(recipeID)
                                .removeValue()
                        }

                        isFavorite = newValue
                    }
                )
            }

        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = recipeName, style = MaterialTheme.typography.titleLarge)
            }
            item {
                //Recipe image
                if (!recipeImg.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(recipeImg),
                        contentDescription = "Recipe image",
                        modifier = Modifier
                            // .height(140.dp)
                            .heightIn(min = 120.dp, max = 300.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.default_image),
                        contentDescription = null,
                        modifier = Modifier
                            // .height(140.dp)
                            .heightIn(min = 120.dp, max = 300.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            item {
                TagCardUI(recipeCategories)
            }
            item {
                Text(text = recipeDescription, style = MaterialTheme.typography.bodyLarge)
            }

            item {
                Column(Modifier.fillMaxWidth()) {
                    Text("Ingredients", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(6.dp))

                    recipeIngredients.forEach { ingredient ->
                        Text("• $ingredient")
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    thickness = 2.dp
                )
            }
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Instructions", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = recipeInstructions, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}