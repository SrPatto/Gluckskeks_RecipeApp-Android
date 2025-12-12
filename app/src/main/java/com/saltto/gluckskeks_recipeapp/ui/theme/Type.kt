package com.saltto.gluckskeks_recipeapp.ui.theme

import com.saltto.gluckskeks_recipeapp.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val MiFuente = FontFamily(
    Font(R.font.fffonts, FontWeight.Normal),
    Font(R.font.ari_bold, FontWeight.Bold)
)
val AppTypography = Typography(
    headlineMedium = Typography().headlineMedium.copy(fontFamily = MiFuente),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = MiFuente,fontWeight = FontWeight.Normal, fontSize = 15.sp,lineHeight = 15.sp,),
    titleLarge = TextStyle(
        fontFamily = MiFuente,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = MiFuente,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = Typography().labelLarge.copy(fontFamily = MiFuente, fontWeight = FontWeight.Bold),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = MiFuente,
        fontWeight = FontWeight.Normal,
        lineHeight = 12.sp,
        fontSize = 11.sp
    )
)

/*val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MiFuente,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MiFuente,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
)
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
*/