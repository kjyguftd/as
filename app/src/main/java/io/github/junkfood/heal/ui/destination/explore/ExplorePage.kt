package io.github.junkfood.heal.ui.destination.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.junkfood.heal.R
import io.github.junkfood.heal.ui.common.LocalNavHostController
import io.github.junkfood.heal.ui.common.NavigationGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorePage(
    navHostController: NavHostController = LocalNavHostController.current,
    exploreViewModel: ExploreViewModel = viewModel()
) {
    val viewState = exploreViewModel.stateFlow.collectAsState()
    Scaffold(
        modifier = Modifier
            .padding()
            .fillMaxSize(),
        topBar = {
            io.github.junkfood.heal.ui.component.SmallTopAppBar(
                title = { Text(stringResource(id = R.string.explore)) },
            )
        },
        content = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    onClick = {
                        navHostController.navigate(NavigationGraph.SEARCH)
                    }
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = stringResource(id = R.string.search_podcast)
                        )

                        Icon(Icons.Outlined.Search, null)

                    }

                }

                Text(
                    modifier = Modifier
                        .padding(all = 12.dp),
                    text = stringResource(id = R.string.discover),
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                ) {

                }
            }
        }
    )
}