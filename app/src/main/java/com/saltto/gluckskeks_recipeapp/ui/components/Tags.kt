package com.saltto.gluckskeks_recipeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagEditItem(
    modifier: Modifier = Modifier,
    text: String,
    onRemove: (String) -> Unit
) {
    Row(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall
        )
        IconButton(
            onClick = {
                onRemove(text)
            },
            modifier = Modifier.size(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.DarkGray
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
        onValueChange = {
            onValueChange(it)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone(tag)
            }
        ),
        textStyle = MaterialTheme.typography.headlineSmall
    ) {
        Box {
            if (tag.isEmpty())
                Text(
                    text = "Add Tag", style = MaterialTheme.typography.titleSmall
                )
            it.invoke()
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

    Column(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
            .background(Color.DarkGray, RoundedCornerShape(4.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            tags.forEach { item ->
                TagEditItem(
                    text = item,
                    onRemove = { onRemoveTag(item) }
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
    }
}

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    text: String,
) {
    Row(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = CircleShape
            )
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.DarkGray
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black

        )
    }
}

@Composable
fun TagCardUI(
    tags: List<String>
) {
    var tag by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .background(Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { item ->
                TagItem(
                    text = item,
                )
            }
        }


    }
}