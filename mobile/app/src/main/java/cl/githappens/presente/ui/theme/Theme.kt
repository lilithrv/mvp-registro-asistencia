package cl.githappens.presente.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Acid,
    onPrimary = Ink,
    background = Ink,
    onBackground = Paper,
    surface = PaperDark,
    onSurface = Paper,
    outline = HairlineDark,
    errorContainer = ErrorDark,
    onErrorContainer = White
)

private val LightColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = White,
    secondary = Acid,
    onSecondary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    outline = Hairline,
    errorContainer = ErrorSoft,
    onErrorContainer = Ink
)

@Composable
fun PresenteMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
