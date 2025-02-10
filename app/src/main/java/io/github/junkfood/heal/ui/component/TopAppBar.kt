package io.github.junkfood.heal.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {

    androidx.compose.material3.LargeTopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {

    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior
    )

}