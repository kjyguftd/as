package io.github.junkfood.heal.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import io.github.junkfood.heal.R
import io.github.junkfood.heal.ui.getTopDestinationRoute

private const val TAG = "NavigationBar"

@Composable
fun NavigationBarImpl() {
    val navController = LocalNavHostController.current
    var selectedTab by remember { mutableStateOf(0) }

    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        selectedTab = navController.getTopDestinationRoute()
    }
    NavigationBar() {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = {
                navController.navigate(NavigationGraph.FEED) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(NavigationGraph.FEED) {
                        saveState = true
                    }
                }
            },
            icon = {
                Icon(
                    if (selectedTab != 0) Icons.Outlined.Home else Icons.Filled.Home,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.home)) },
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = {
                if (selectedTab == 1) navController.popBackStack(NavigationGraph.LISTEN, false)
                else
                    navController.navigate(NavigationGraph.EXPLORE) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(NavigationGraph.FEED) {
                            saveState = true
                        }
                    }
            },
            icon = {
                Icon(
                    if (selectedTab != 1) Icons.Outlined.Search else Icons.Filled.Search,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.explore)) },
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = {
                navController.navigate(NavigationGraph.LIBRARY) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(NavigationGraph.FEED) {
                        saveState = true
                    }
                }
            },
            icon = {
                Icon(
                    if (selectedTab != 2) Icons.Outlined.CollectionsBookmark else Icons.Filled.CollectionsBookmark,
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.library)) },
        )

    }
}