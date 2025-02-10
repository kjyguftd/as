package io.github.junkfood.heal.ui.destination.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.junkfood.heal.ui.common.LocalNavHostController
import io.github.junkfood.heal.ui.component.BackButton
import io.github.junkfood.heal.ui.component.SearchItem
import io.github.junkfood.heal.ui.component.SmallTopAppBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    navHostController: NavHostController = LocalNavHostController.current,
    searchViewModel: SearchViewModel = viewModel()
) {
    val viewState = searchViewModel.stateFlow.collectAsState()
    val TAG = "SearchPage"
    Scaffold (
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    BackButton { navHostController.popBackStack() }
                },
                title = {
                    Row() {
                        androidx.compose.material.TextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = viewState.value.searchString,
                            onValueChange = {
                                searchViewModel.updateSearchString(it)
                            },
                            maxLines = 1,
                            trailingIcon = {
                                TextField(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    value = viewState.value.searchString,
                                    onValueChange = {
                                        searchViewModel.updateSearchString(it)
                                    },
                                    maxLines = 1,
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            Log.d(TAG, "SearchPage: click search")
                                            searchViewModel.searchPodcast()
                                        }) {
                                            Icon(Icons.Outlined.Search, null)
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                )
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        )
                    }
                }
            )
        }
    ){
        Column(
            modifier = Modifier.padding(it)
        ) {
            LazyColumn {
                for (item in viewState.value.searchResult) {
                    item {
                        SearchItem(
                            imageModel = item.coverUrl,
                            podcastTitle = item.title,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

