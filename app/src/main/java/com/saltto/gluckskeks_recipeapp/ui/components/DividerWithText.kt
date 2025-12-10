package com.saltto.gluckskeks_recipeapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DividerWithText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
    thickness: Dp = 1.dp,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Centers the content within the Row
    ) {
        // Left Divider
        HorizontalDivider(
            modifier = Modifier.weight(1f), // Fills available space
            color = color,
            thickness = thickness
        )

        // Text in the middle
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp), // Add padding around the text
            style = MaterialTheme.typography.bodyMedium
        )

        // Right Divider
        HorizontalDivider(
            modifier = Modifier.weight(1f), // Fills available space
            color = color,
            thickness = thickness
        )
    }
}
