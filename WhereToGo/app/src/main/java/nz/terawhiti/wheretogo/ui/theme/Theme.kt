package nz.terawhiti.wheretogo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

val Ink = Color(0xFF1A2423)
val Teal = Color(0xFF0E4F4C)
val Paper = Color(0xFFF6F1E8)
val CardWhite = Color(0xFFFFFCF7)
val Emergency = Color(0xFF8E1B2C)
val EmergencySoft = Color(0xFFF8E8EB)

private val Colors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    background = Paper,
    surface = CardWhite,
    onBackground = Ink,
    onSurface = Ink,
    error = Emergency,
)

@Composable
fun WhereToGoTheme(largeText: Boolean, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val scaled = if (largeText) Density(density.density, density.fontScale * 1.2f) else density
    CompositionLocalProvider(LocalDensity provides scaled) {
        MaterialTheme(colorScheme = Colors, content = content)
    }
}
