package com.saltto.gluckskeks_recipeapp.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.saltto.gluckskeks_recipeapp.R

@Composable
fun TagEditItem(
    modifier: Modifier = Modifier,
    text: String,
    onRemove: (String) -> Unit
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(
            onClick = { onRemove(text) },
            modifier = Modifier.size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove tag",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TagInputField(
    modifier: Modifier = Modifier,
    tag: String,
    onValueChange: (String) -> Unit,
    onDone: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = tag,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onDone(tag) }
        ),
        textStyle = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    ) { innerTextField ->
        Box {
            if (tag.isEmpty()) {
                Text(
                    text = "Add tag",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            innerTextField()
        }
    }
}

@Composable
fun SuggestedTagsDropdown(
    allTags: List<String>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val visibleTags = allTags.take(4)
    val hiddenTags = allTags.drop(4)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            text = "Suggested tags",
            style = MaterialTheme.typography.titleSmall
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy((-6).dp)
        ) {
            visibleTags.forEach { tag ->
                AssistChip(
                    onClick = {
                        if (tag !in selectedTags) {
                            onTagSelected(tag)
                        }
                    },
                    enabled = tag !in selectedTags,
                    label = { Text(tag) }
                )
            }

            AssistChip(
                onClick = { expanded = true },
                label = { Text("More") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            hiddenTags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag) },
                    onClick = {
                        expanded = false
                        if (tag !in selectedTags) {
                            onTagSelected(tag)
                        }
                    },
                    enabled = tag !in selectedTags
                )
            }
        }
    }
}

@Composable
fun TagInputUI(
    tags: List<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    var tag by remember { mutableStateOf("") }

    val defaultTags = remember {
        mutableStateListOf(
            "Meat",
            "Seafood",
            "Vegetables",
            "Dairy",
            "Vegetarian",
            "Fast food",
            "Main dish",
            "Snack",
            "Breakfast",
            "Dessert",
            "Bread / Pastry",
            "Drink",
            "Oven",
            "No cooking",
            "Very easy",
            "Gourmet",
            "Less than 1 hour",
            "More than 1 hour",
            "Budget-friendly",
            "Expensive",
            "For 1 person",
            "To share",
            "Christmas",
            "Romantic",
            "Medium difficulty"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(8.dp)
            )
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { item ->
                TagEditItem(
                    text = item,
                    onRemove = onRemoveTag
                )
            }

            TagInputField(
                tag = tag,
                onValueChange = { tag = it },
                onDone = { value ->
                    if (value.isNotBlank()) onAddTag(value)
                    tag = ""
                }
            )
        }

        SuggestedTagsDropdown(
            allTags = defaultTags,
            selectedTags = tags,
            onTagSelected = onAddTag
        )
    }
}

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    text: String,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                CircleShape
            )
            .background(
                MaterialTheme.colorScheme.surface,
                CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberDrawablePainter(
                drawable = getDrawable(
                    context,
                    com.saltto.gluckskeks_recipeapp.R.drawable.tag
                )
            ),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TagCardUI(
    tags: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(8.dp)
            )
            .background(
                Color(0x00DB1A6A),
                RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { item ->
                TagItem(text = item)
            }
        }
    }
}