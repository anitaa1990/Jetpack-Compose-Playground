package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.an.jetpack_compose_playground.R
import com.an.jetpack_compose_playground.ui.component.network.NetworkObserver
import com.an.jetpack_compose_playground.ui.component.network.NetworkStatusBar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun NetworkStatusScreen(
    networkObserver: NetworkObserver
) {
    val scope = rememberCoroutineScope()
    val isInternetConnected = remember { mutableStateOf(false) }

    // Collect network state updates
    LaunchedEffect(Unit) {
        scope.launch {
            networkObserver.isConnectedFlow.collectLatest { isConnected ->
                isInternetConnected.value = isConnected
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.btn_txt_network_status)
                    )
                },
                modifier = Modifier
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // displays network status bar when network is connected/disconnected
            NetworkStatusBar(isInternetConnected.value)

            Text(text = stringResource(R.string.info_txt_network_status_screen))
        }
    }
}