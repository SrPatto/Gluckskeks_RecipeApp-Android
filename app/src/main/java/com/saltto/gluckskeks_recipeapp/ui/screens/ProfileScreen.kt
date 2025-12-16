package com.saltto.gluckskeks_recipeapp.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.drawablepainter.rememberDrawablePainter
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
import com.saltto.gluckskeks_recipeapp.R
import kotlinx.coroutines.launch
import kotlin.toString


@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return

    var username by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val userRef = FirebaseDatabase.getInstance()
        .reference.child("users").child(uid)

    val storageRef = FirebaseStorage.getInstance()
        .reference.child("user_photos/$uid.jpg")

    // SEGMENTED BUTTON STATE
    val options = listOf("Your Recipes", "Favorites")
    var selectedIndex by remember { mutableIntStateOf(0) }

    // RECIPES STATE
    val recipeList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }

    // IMAGE PICKER
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    userRef.child("photoUrl").setValue(downloadUrl.toString())
                    photoUrl = downloadUrl.toString()
                    Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    val imageModifier = Modifier
        .size(140.dp)
        .clip(CircleShape)
        .clickable { imagePicker.launch("image/*") }
        .border(
            border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.inverseSurface),
            CircleShape
        )

    // LOAD USER INFO
    LaunchedEffect(uid) {
        userRef.get().addOnSuccessListener { snap ->
            username = snap.child("username").value?.toString() ?: "Unknown User"
            photoUrl = snap.child("photoUrl").value?.toString()
        }
    }

    // LOAD RECIPES BASED ON FILTER
    LaunchedEffect(selectedIndex, uid) {
        isLoading = true
        recipeList.clear()

        if (selectedIndex == 0) {
            loadUserRecipes(uid, recipeList) {
                isLoading = false
            }
        } else {
            loadFavoriteRecipes(uid, recipeList) {
                isLoading = false
            }
        }
    }

    val listState = rememberLazyListState()

    // UI
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {


        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.comida_mini),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                        )
                        if (!photoUrl.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(photoUrl),
                                contentDescription = null,
                                modifier = imageModifier,
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(imageModifier, contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(90.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = username, style = MaterialTheme.typography.headlineMedium)
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            signOut(context) {
                                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Logout", style = MaterialTheme.typography.titleLarge)
                    }

                    Spacer(Modifier.height(16.dp))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                                label = { Text(label) },
                                shape = SegmentedButtonDefaults.itemShape(index, options.size)
                            )
                        }
                    }
                }
            }


            when {
                isLoading -> {
                    item { Text("Loading recipes...") }
                }

                recipeList.isEmpty() -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                if (selectedIndex == 0)
                                    "You haven't created any recipes yet"
                                else
                                    "No favorite recipes yet"
                            )
                            Image(
//                            painter = painterResource(R.drawable.default_image),
                                painter = rememberDrawablePainter(
                                    drawable = getDrawable(
                                        context,
                                        com.saltto.gluckskeks_recipeapp.R.drawable.karuko
                                    )
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        }

                    }
                }

                else -> {
                    items(recipeList.size) { index ->
                        val recipeId = recipeList[index]
                        RecipeCard(
                            recipeID = recipeId,
                            onClickable = { navController.navigate("recipe/$recipeId") },
                            isEditable = selectedIndex == 0,
                            onEditClick = { navController.navigate("edit_recipe/$it") }
                        )
                    }
                }
            }
        }
    }
    // EDIT USERNAME DIALOG
    if (showEditDialog) {
        var newName by remember { mutableStateOf(username) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Username") },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it }
                )
            },
            confirmButton = {
                Button(onClick = {
                    userRef.child("username").setValue(newName)
                    username = newName
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
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

fun loadUserRecipes(
    uid: String,
    recipeList: MutableList<String>,
    onDone: () -> Unit
) {
    FirebaseDatabase.getInstance().reference
        .child("recipes")
        .orderByChild("author")
        .equalTo(uid)
        .get()
        .addOnSuccessListener { snapshot ->
            for (recipe in snapshot.children) {
                recipe.key?.let { recipeList.add(it) }
            }
            onDone()
        }
}

fun loadFavoriteRecipes(
    uid: String,
    recipeList: MutableList<String>,
    onDone: () -> Unit
) {
    FirebaseDatabase.getInstance().reference
        .child("favorites")
        .child(uid)
        .get()
        .addOnSuccessListener { snapshot ->
            for (fav in snapshot.children) {
                fav.key?.let { recipeList.add(it) }
            }
            onDone()
        }
}