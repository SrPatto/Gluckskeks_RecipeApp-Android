package com.saltto.gluckskeks_recipeapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.saltto.gluckskeks_recipeapp.R
import com.saltto.gluckskeks_recipeapp.ui.components.ReturnButton
import com.saltto.gluckskeks_recipeapp.ui.components.TagInputUI
import com.saltto.gluckskeks_recipeapp.utils.createImageUri
import java.io.File

@Composable
fun EditRecipeScreen(
    navController: NavHostController,
    recipeID: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeID)

    // States that will be filled when data loads
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var categories = remember { mutableStateListOf<String>() }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var originalPhotoUrl by remember { mutableStateOf<String?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    // Load recipe once
    LaunchedEffect(Unit) {
        recipeRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                name = snapshot.child("name").value.toString()
                description = snapshot.child("description").value.toString()
                ingredients = snapshot.child("ingredients")
                    .children.joinToString(",") { it.value.toString() }
                instructions = snapshot.child("instructions").value.toString()

                categories.clear()
                snapshot.child("categories").children.forEach {
                    categories.add(it.value.toString())
                }

                originalPhotoUrl = snapshot.child("picture").value.toString()
                isLoading = false
            }
        }
    }

    // Camera launcher
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Update UI AFTER the camera finishes writing the file
            photoUri = capturedImageUri
        } else {
            Toast.makeText(context, "Photo not captured", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery launcher
    val pickPhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) photoUri = uri
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading recipe...")
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ReturnButton { navController.popBackStack() }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text("Edit Recipe", style = MaterialTheme.typography.titleLarge)
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recipe Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }

            item {
                TagInputUI(
                    tags = categories,
                    onAddTag = { categories.add(it) },
                    onRemoveTag = { categories.remove(it) }
                )
            }

            item {
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text("Ingredients (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 8
                )
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    val imageModifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()

                    // Show selected image OR original image
                    when {
                        photoUri != null -> Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Recipe photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )

                        originalPhotoUrl != null -> Image(
                            painter = rememberAsyncImagePainter(originalPhotoUrl),
                            contentDescription = null,
                            modifier = imageModifier,
                            contentScale = ContentScale.Crop
                        )

                        else -> Image(
                            painter = painterResource(R.drawable.default_image),
                            contentDescription = null,
                            modifier = imageModifier
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(onClick = {
                            pickPhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) { Text("Gallery") }

                        Button(onClick = {
                            val uri = createImageUri(context)
                            capturedImageUri = uri      // store the URI
                            takePhotoLauncher.launch(uri)
                        }) {
                            Text("Take Photo")
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val storageRef = FirebaseStorage.getInstance()
                            .reference.child("recipes_photos/$recipeID.jpg")

                        // If user selected a new photo: upload it → save URL
                        if (photoUri != null) {
                            storageRef.putFile(photoUri!!)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                                        val updatedData = mapOf(
                                            "name" to name,
                                            "description" to description,
                                            "ingredients" to ingredients.split(","),
                                            "instructions" to instructions,
                                            "categories" to categories,
                                            "picture" to downloadUrl.toString()
                                        )

                                        recipeRef.updateChildren(updatedData)

                                        Toast.makeText(
                                            context,
                                            "Recipe updated!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                    }
                                }
                        } else {
                            // No new photo → keep old one
                            val updatedData = mapOf(
                                "name" to name,
                                "description" to description,
                                "ingredients" to ingredients.split(","),
                                "instructions" to instructions,
                                "categories" to categories,
                                "picture" to originalPhotoUrl
                            )

                            recipeRef.updateChildren(updatedData)
                            Toast.makeText(context, "Recipe updated!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    enabled = name.isNotBlank() && description.isNotBlank()
                ) { Text("Save Changes") }
            }
        }
    }
}
