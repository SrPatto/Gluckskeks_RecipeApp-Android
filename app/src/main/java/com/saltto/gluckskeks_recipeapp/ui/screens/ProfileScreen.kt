package com.saltto.gluckskeks_recipeapp.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.saltto.gluckskeks_recipeapp.navigation.Routes
import com.saltto.gluckskeks_recipeapp.ui.components.RecipeCard
import kotlin.toString

@Composable
fun ProfileScreen(navController: NavHostController, modifier: Modifier) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var username by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val database = FirebaseDatabase.getInstance().reference.child("users").child(uid)
    val storageRef = FirebaseStorage.getInstance().reference.child("user_photos/$uid.jpg")

    val recipeList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }
    // Pick image launcher
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                storageRef.putFile(it).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        database.child("photoUrl").setValue(downloadUrl.toString())
                        photoUrl = downloadUrl.toString()
                        Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    val imageModifier = Modifier
        .size(140.dp)
        .clip(CircleShape)
        .clickable { imagePicker.launch("image/*") }

    // Load data
    LaunchedEffect(uid) {
        database.get().addOnSuccessListener { snap ->
            username = snap.child("username").value?.toString() ?: "Unknown User"
            photoUrl = snap.child("photoUrl").value?.toString()
        }

        val recipesRef = FirebaseDatabase.getInstance().reference
            .child("recipes")
            .orderByChild("author")
            .equalTo(uid)

        recipesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recipeList.clear()
                for (recipe in snapshot.children) {
                    recipeList.add(recipe.key.toString())   // store recipe ID
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Profile", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(20.dp))

            if (!photoUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = photoUrl),
                    contentDescription = "Profile Photo",
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = imageModifier,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile",
                        modifier = Modifier.size(90.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // USERNAME + EDIT ICON
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(username, style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "edit username")
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    signOut(context) {
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.PROFILE) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Logout", style = MaterialTheme.typography.titleLarge) }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    isLoading -> {
                        item { Text("Loading recipes...") }
                    }

                    recipeList.isEmpty() -> {
                        item { Text("No recipes yet — upload one!") }
                    }

                    else -> {
                        items(recipeList.size) { index ->
                            RecipeCard(
                                recipeID = recipeList[index],
                                onClickable = { navController.navigate("recipe/${recipeList[index]}") },
                                isEditable = true,
                                onEditClick = { navController.navigate("edit_recipe/$it") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        var newName by remember { mutableStateOf(username) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(onClick = {
                    database.child("username").setValue(newName)
                    username = newName
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) { Text("Cancel") }
            },
            title = { Text("Edit Username") },
            text = {
                TextField(value = newName, onValueChange = { newName = it })
            }
        )
    }
}

fun signOut(context: Context, onComplete: () -> Unit) {
    val webClientId = "889990981952-e9nl8iaosao70sm3nrb1gnsvpn2in19o.apps.googleusercontent.com"

    Firebase.auth.signOut()

    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    googleSignInClient.signOut().addOnCompleteListener {
        onComplete()
    }
}