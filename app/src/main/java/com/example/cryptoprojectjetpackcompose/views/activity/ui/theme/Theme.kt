package com.example.cryptoprojectjetpackcompose.views.activity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun CryptoProjectJetpackComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Create my own gradient color
@get:Composable
val Colors.gradientBottom: Color
    get() = Color(0xFF450EFF)

@get:Composable
val Colors.gradientTop: Color
    get() = Color(0xFFE100F5)

@get:Composable
val Colors.buttonColor: Color
    get() = Color(0xFFEA5AFC)

@get:Composable
val Colors.itemColor: Color
    get() = Color(0xFFB364FF)

@get:Composable
val Colors.textColorGreen: Color
    get() = Color(0xFF0FFF50)

@get:Composable
val Colors.textColorRed: Color
    get() = Color(0xFFFF3131)

@get:Composable
val Colors.textColorCyan: Color
    get() = Color(0xFF5C6BC0)