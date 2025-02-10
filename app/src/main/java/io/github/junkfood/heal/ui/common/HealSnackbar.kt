package io.github.junkfood.heal.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

object SnackbarUtil {
    lateinit var scope: CoroutineScope
    lateinit var snackbarHostState: SnackbarHostState
}

@Composable
fun HealSnackbar(data: String, ) {
    SnackbarHost(hostState = SnackbarHostState()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Snackbar(
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = data,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
        }

    }
}
