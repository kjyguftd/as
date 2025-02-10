package io.github.junkfood.heal.ui.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.junkfood.ColorSchemeUtil.DEFAULT_SEED_COLOR
import io.github.junkfood.heal.App.Companion.context
import io.github.junkfood.heal.util.PreferenceUtil
import kotlinx.coroutines.flow.Flow

val LocalDarkTheme = compositionLocalOf { PreferenceUtil.DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val LocalNavHostController = staticCompositionLocalOf { NavHostController(context) }
val settingFlow: Flow<PreferenceUtil.AppSettings> = PreferenceUtil.AppSettingsStateFlow
val LocalDynamicColorSwitch = compositionLocalOf { false }

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsProvider(content: @Composable () -> Unit) {
    val appSettingsState = settingFlow.collectAsState(PreferenceUtil.AppSettings()).value
    CompositionLocalProvider(
        LocalDarkTheme provides appSettingsState.darkTheme,
        LocalSeedColor provides appSettingsState.seedColor,
        LocalNavHostController provides rememberAnimatedNavController(),
        LocalDynamicColorSwitch provides appSettingsState.isDynamicColorEnabled,
        content = content
    )
}