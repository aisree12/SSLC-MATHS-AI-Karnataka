package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = IndigoInkDeep,
    primaryContainer = IndigoInk,
    onPrimaryContainer = IndigoInkContainer,
    secondary = SaffronGoldBright,
    onSecondary = OnSaffronGoldContainer,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = SaffronGoldContainer,
    tertiary = EmeraldChalkLight,
    onTertiary = OnEmeraldChalkContainer,
    tertiaryContainer = EmeraldChalk,
    onTertiaryContainer = EmeraldChalkContainer,
    background = SlateChalkboard,
    onBackground = Color(0xFFF1F5F9),
    surface = SlateCard,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = SlateCardElevated,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SlateOutline,
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D)
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoInk,
    onPrimary = Color.White,
    primaryContainer = IndigoInkContainer,
    onPrimaryContainer = OnIndigoInkContainer,
    secondary = SaffronGold,
    onSecondary = Color.White,
    secondaryContainer = SaffronGoldContainer,
    onSecondaryContainer = OnSaffronGoldContainer,
    tertiary = EmeraldChalk,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldChalkContainer,
    onTertiaryContainer = OnEmeraldChalkContainer,
    background = WarmIvory,
    onBackground = IndigoInkDeep,
    surface = WarmPaper,
    onSurface = IndigoInkDeep,
    surfaceVariant = WarmGridSurface,
    onSurfaceVariant = Color(0xFF475569),
    outline = WarmOutline,
    error = CrimsonAlert,
    errorContainer = CrimsonContainer
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SslcMathsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
