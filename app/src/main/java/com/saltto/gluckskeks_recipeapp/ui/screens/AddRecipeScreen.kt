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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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
fun AddRecipeScreen(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var categories = remember { mutableStateListOf<String>() }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

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
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> photoUri = uri }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ReturnButton {
                navController.popBackStack()
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "New Recipe", style = MaterialTheme.typography.titleLarge)
                }
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Recipe Name:", style = MaterialTheme.typography.titleLarge) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description:", style = MaterialTheme.typography.titleLarge) },
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
                    label = {
                        Text(
                            "Ingredients (comma-separated):",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions:", style = MaterialTheme.typography.titleLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 8
                )
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    val imageModifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()

                    // Show preview
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Recipe photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.default_image),
                            contentDescription = null,
                            modifier = imageModifier
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                pickPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Choose from Gallery",
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(onClick = {
                                pickPhotoLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }) { Text("Choose from Gallery") }

                            Button(
                                onClick = {
                                    val uri = createImageUri(context)
                                    capturedImageUri = uri      // store the URI
                                    takePhotoLauncher.launch(uri)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Take Photo",
                                    style = MaterialTheme.typography.titleSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val recipeId = FirebaseDatabase.getInstance()
                            .getReference("recipes")
                            .push().key ?: return@Button

                        val storageRef = FirebaseStorage.getInstance()
                            .reference.child("recipes_photos/$recipeId.jpg")

                        if (photoUri != null) {
                            storageRef.putFile(photoUri!!)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                                        val recipeData = mapOf(
                                            "id" to recipeId,
                                            "author" to uid,
                                            "name" to name,
                                            "description" to description,
                                            "ingredients" to ingredients.split(","),
                                            "instructions" to instructions,
                                            "categories" to categories,
                                            "picture" to downloadUrl.toString()
                                        )

                                        FirebaseDatabase.getInstance()
                                            .getReference("recipes")
                                            .child(recipeId)
                                            .setValue(recipeData)

                                        Toast.makeText(
                                            context,
                                            "Recipe added!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        navController.popBackStack()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                context,
                                "Select an image first!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    },
                    enabled = name.isNotBlank() && description.isNotBlank()
                ) { Text("Add Recipe") }
            }
        }
    }
}


fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val filename = "${System.currentTimeMillis()}.jpg"
    val stream = context.openFileOutput(filename, Context.MODE_PRIVATE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
    stream.close()
    val file = File(context.filesDir, filename)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}