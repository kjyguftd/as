package io.github.junkfood.heal.ui.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    exitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    popEnterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    popExitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    content = content
)


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.fadedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        fadeIn(animationSpec = tween(300))
    },
    exitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    popEnterTransition = {
        fadeIn(animationSpec = tween(300))
    },
    popExitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    content = content
)


@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.slideComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        slideInAnimation()
//        + fadeIn(animationSpec = tween(90))
    },
    exitTransition = {
        slideOutAnimation() + fadeOut(animationSpec = tween(90))
    },
    popEnterTransition = {
        slideInAnimation()
//        + fadeIn(animationSpec = tween(90))
    },
    popExitTransition = {
        slideOutAnimation() + fadeOut(animationSpec = tween(90))
    },
    content = content
)

fun slideInAnimation(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    initialOffsetY: (fullHeight: Int) -> Int = { it },
): EnterTransition =
    slideIn(
        initialOffset = { IntOffset(0, initialOffsetY(it.height)) },
        animationSpec = animationSpec
    )

fun slideOutAnimation(
    animationSpec: FiniteAnimationSpec<IntOffset> =
        spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        ),
    targetOffsetY: (fullHeight: Int) -> Int = { it / 2 },
): ExitTransition =
    slideOut(
        targetOffset = { IntOffset(0, targetOffsetY(it.height)) },
        animationSpec = animationSpec
    )