package io.github.junkfood.heal.ui

import android.annotation.SuppressLint
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import io.github.junkfood.PodcastTheme
import io.github.junkfood.heal.player.ExoPlayerHolder
import io.github.junkfood.heal.ui.common.*
import io.github.junkfood.heal.ui.common.NavigationGraph.EPISODE_ID
import io.github.junkfood.heal.ui.common.NavigationGraph.PODCAST_ID
import io.github.junkfood.heal.ui.common.NavigationGraph.withArgument

import io.github.junkfood.heal.ui.common.SettingsProvider
import io.github.junkfood.heal.ui.common.animatedComposable
import io.github.junkfood.heal.ui.common.slideComposable
import io.github.junkfood.heal.ui.component.BottomDrawer
import io.github.junkfood.heal.ui.component.ListenPageDrawer
import io.github.junkfood.heal.ui.component.NowPlayingBar
import io.github.junkfood.heal.ui.destination.episode.EpisodePage
import io.github.junkfood.heal.ui.destination.explore.ExplorePage
import io.github.junkfood.heal.ui.destination.feed.FeedPage
import io.github.junkfood.heal.ui.destination.library.LibraryPage
import io.github.junkfood.heal.ui.destination.library.LibraryViewModel
import io.github.junkfood.heal.ui.destination.listen.ListenPage
import io.github.junkfood.heal.ui.destination.podcast.PodcastPage
import io.github.junkfood.heal.ui.destination.search.SearchPage
import io.github.junkfood.heal.ui.destination.settings.AppearancePreferences
import io.github.junkfood.heal.ui.destination.settings.SettingsPage
import io.github.junkfood.heal.ui.destination.subscription.SubscriptionPage
import kotlinx.coroutines.launch

private const val TAG = "HomeEntry"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Preview
@Composable
fun HomeEntry() {
    SnackbarUtil.scope = rememberCoroutineScope()
    SnackbarUtil.snackbarHostState = remember { SnackbarHostState()}
    //
    SettingsProvider {
        PodcastTheme(
            darkTheme = LocalDarkTheme.current.isDarkTheme(),
            seedColor = LocalSeedColor.current,
            isDynamicColorEnabled = LocalDynamicColorSwitch.current
        ) {
            val drawerState =
                rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
            val scope = rememberCoroutineScope()
            val navController = LocalNavHostController.current
            val onBackPressed = { navController.popBackStack() }
            val playerState = ExoPlayerHolder.playerStateFlow.collectAsState().value
            val backStack =
                navController.currentBackStackEntryAsState().value?.destination?.route.toString()
            val context = LocalContext.current
            //composable function handles the back button press
            BackHandler(drawerState.isVisible)//check is the drawer is currently visible
            {
                //if visible, launch a coroutine to hide the drawer
                scope.launch { drawerState.hide() }
            }
            //a layout strcture provides slots for UI component
            Scaffold(modifier = Modifier, bottomBar = {
                Column() {
                    //a composable shows or hides its content with animation based on visible parameter
                    AnimatedVisibility(
                        visible = playerState.isPlayerAvailable && drawerState.currentValue != ModalBottomSheetValue.HalfExpanded
                    ) {
                        //a custom composable displays the currently playing episodes' information
                        NowPlayingBar(
                            episodeTitle = playerState.currentEpisode.title,
                            podcastTitle = playerState.currentPodcast.title,
                            progress = playerState.currentProgress,
                            image = playerState.currentEpisode.cover,
                            //show the drawer if clicked
                            onClick = {
//                                navController.navigate(NavigationGraph.LISTEN)
                                scope.launch { drawerState.show() }
                            },
                            isPlaying = playerState.isPlaying,
                            //play/pause button
                            onButtonClicked = {
                                if (playerState.isPlaying) ExoPlayerHolder.get().pause()
                                else ExoPlayerHolder.get().play()
                            }
                        )
                    }
                    AnimatedVisibility(
                        visible = drawerState.currentValue != ModalBottomSheetValue.HalfExpanded,
                    ) {
                        //a custom composable represents the navigation bar
                        NavigationBarImpl()
                    }

                }
            }, topBar = { },
                //define the main content of Scaffold
                content = {
                val libraryViewModel = LibraryViewModel()
                //a composable manage navigation between different screens with animation
                AnimatedNavHost(
                    modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
                    navController = navController,
                    startDestination = NavigationGraph.FEED
                ) {
                    animatedComposable(NavigationGraph.FEED) {
                        FeedPage(navController)
                    }
                    animatedComposable(NavigationGraph.EXPLORE) {
                        ExplorePage()
                    }
                    slideComposable(NavigationGraph.LISTEN) {
                        ListenPage(navController)
                    }
                    animatedComposable(NavigationGraph.LIBRARY) {
                        LibraryPage(navController, libraryViewModel)
                    }
                    animatedComposable(NavigationGraph.SETTINGS) {
                        SettingsPage(navController)
                    }
                    animatedComposable(
                        //add an argument to the route
                        NavigationGraph.EPISODE.withArgument(EPISODE_ID),
                        arguments = listOf(navArgument(EPISODE_ID) {
                            type = NavType.LongType
                        })
                    ) { backStackEntry ->
                        EpisodePage(
                            navController,
                            backStackEntry.arguments?.getLong(EPISODE_ID) ?: 0
                        )
                    }
                    animatedComposable(
                        NavigationGraph.PODCAST.withArgument(PODCAST_ID),
                        arguments = listOf(navArgument(PODCAST_ID) {
                            type = NavType.LongType
                        })
                    ) { backStackEntry ->
                        PodcastPage(
                            navController,
                            backStackEntry.arguments?.getLong(PODCAST_ID) ?: 0
                        )
                    }
                    animatedComposable(NavigationGraph.SUBSCRIPTIONS) {
                        SubscriptionPage()
                    }
                    animatedComposable(NavigationGraph.APPEARANCE) {
                        AppearancePreferences()
                    }
                    animatedComposable(NavigationGraph.SEARCH) {
                        SearchPage()
                    }
                }
            })
            //a custom composable displays a drawer with the ListenPage
            ListenPageDrawer(drawerState = drawerState, sheetContent = {
                ListenPage(onDismiss = { scope.launch { drawerState.hide() } })
            })
        }
    }
}

//retrieve the most recent destination while exit
fun NavHostController.getTopDestinationRoute(): Int {
    this.backQueue.reversed().forEach {
        Log.d(TAG, it.destination.route.toString())
        with(it.destination.route) {
            when (this) {
                NavigationGraph.FEED -> return 0
                NavigationGraph.EXPLORE -> return 1
                NavigationGraph.LIBRARY -> return 2
            }
        }
    }
    return 3
}