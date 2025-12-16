package com.saltto.gluckskeks_recipeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.saltto.gluckskeks_recipeapp.R
import com.saltto.gluckskeks_recipeapp.ui.theme.Gluckskeks_RecipeAppTheme

@Composable
fun RecipeCard(
    recipeID: String,
    onClickable: () -> Unit,
    isEditable: Boolean = false,
    onEditClick: (String) -> Unit = {}
) {
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
    var recipeImg by remember { mutableStateOf<String?>(null) }
    var recipeCategories by remember { mutableStateOf(listOf<String>()) }

    val recipeRef = FirebaseDatabase.getInstance().reference.child("recipes").child(recipeID)

    LaunchedEffect(recipeID) {
        recipeRef.get().addOnSuccessListener { snap ->
            authorID = snap.child("author").value?.toString() ?: ""
            recipeName = snap.child("name").value?.toString() ?: "No name"
            recipeDescription = snap.child("description").value?.toString() ?: "No description"
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

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickable() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.container),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize(),
                contentScale = ContentScale.FillBounds
            )

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp, 2.dp, 6.dp, 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (!userPhoto.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(userPhoto),
                                contentDescription = "User image",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                tint = Color(0xFF000000)
                            )
                        }
                        Text(
                            username,
                            modifier = Modifier.padding(12.dp),
                            color = Color(0xFF000000)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 2.dp)
                    ) {
                        if (isEditable) {
                            RecipeDropdownMenu(
                                recipeID = recipeID,
                                onDelete = { id ->

                                    val db = FirebaseDatabase.getInstance().reference

                                    db.child("recipes")
                                        .child(id)
                                        .removeValue()

                                    db.child("favorites")
                                        .get()
                                        .addOnSuccessListener { snapshot ->
                                            snapshot.children.forEach { userNode ->
                                                userNode.child(id).ref.removeValue()
                                            }
                                        }
                                },
                                onEdit = { id ->
                                    onEditClick(id)
                                }
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

                Column(Modifier.padding(6.dp, 0.dp, 6.dp, 4.dp)) {
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
                                //  .height(140.dp)
                                .heightIn(min = 120.dp, max = 300.dp)
                                .fillMaxWidth(),
                        )
                    }

                    Text(
                        recipeName,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        color = Color(0xFF000000)
                    )
                    Text(
                        recipeDescription,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        color = Color(0xFF000000)
                    )

                    Box(modifier = Modifier.padding(5.dp, 8.dp, 8.dp, 8.dp)) {
                        TagCardUI(
                            recipeCategories
                        )
                    }
                }
            }
        }

    }

}
