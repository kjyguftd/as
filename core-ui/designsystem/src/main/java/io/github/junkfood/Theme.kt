package io.github.junkfood

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.DynamicColors
import io.github.junkfood.ColorSchemeUtil.colorSchemeFromColor

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}

@Composable
fun PodcastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    seedColor: Int,
    isDynamicColorEnabled: Boolean,
    content: @Composable () -> Unit
) {

    rememberSystemUiController().run {
        setStatusBarColor(Color.Transparent, !darkTheme)
        setSystemBarsColor(Color.Transparent, !darkTheme)
        setNavigationBarColor(Color.Transparent, !darkTheme)
    }
    val colorScheme = when {
        DynamicColors.isDynamicColorAvailable() && isDynamicColorEnabled -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        else -> colorSchemeFromColor(seedColor, darkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}