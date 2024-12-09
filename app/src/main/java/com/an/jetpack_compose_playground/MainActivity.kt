package com.an.jetpack_compose_playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.an.jetpack_compose_playground.AppConstants.HOME
import com.an.jetpack_compose_playground.AppConstants.ROUTE_BIOMETRIC_AUTH
import com.an.jetpack_compose_playground.AppConstants.ROUTE_BOOK_PAGER
import com.an.jetpack_compose_playground.AppConstants.ROUTE_CIRCLE_REVEAL_PAGER
import com.an.jetpack_compose_playground.AppConstants.ROUTE_COMPOSE_TEXT_EDITOR
import com.an.jetpack_compose_playground.AppConstants.ROUTE_NETWORK_STATUS
import com.an.jetpack_compose_playground.AppConstants.ROUTE_PARALLAX_PAGER
import com.an.jetpack_compose_playground.AppConstants.ROUTE_RUNTIME_PERMISSION
import com.an.jetpack_compose_playground.ui.component.network.NetworkObserver
import com.an.jetpack_compose_playground.ui.screen.BiometricAuthScreen
import com.an.jetpack_compose_playground.ui.screen.BookPagerScreen
import com.an.jetpack_compose_playground.ui.screen.CircleRevealPagerScreen
import com.an.jetpack_compose_playground.ui.screen.HomeScreen
import com.an.jetpack_compose_playground.ui.screen.NetworkStatusScreen
import com.an.jetpack_compose_playground.ui.screen.ParallaxPagerScreen
import com.an.jetpack_compose_playground.ui.screen.RuntimePermissionScreen
import com.an.jetpack_compose_playground.ui.screen.TextEditorScreen
import com.an.jetpack_compose_playground.ui.theme.JetpackComposePlaygroundTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposePlaygroundTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = HOME,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(route = HOME) {
                        HomeScreen(navController = navController)
                    }
                    composable(route = ROUTE_BOOK_PAGER) {
                        BookPagerScreen()
                    }
                    composable(route = ROUTE_PARALLAX_PAGER) {
                        ParallaxPagerScreen()
                    }
                    composable(route = ROUTE_CIRCLE_REVEAL_PAGER) {
                        CircleRevealPagerScreen()
                    }
                    composable(route = ROUTE_COMPOSE_TEXT_EDITOR) {
                        TextEditorScreen()
                    }
                    composable(route = ROUTE_NETWORK_STATUS) {
                        NetworkStatusScreen(NetworkObserver(context))
                    }
                    composable(route = ROUTE_RUNTIME_PERMISSION) {
                        RuntimePermissionScreen()
                    }
                    composable(route = ROUTE_BIOMETRIC_AUTH) {
                        BiometricAuthScreen()
                    }
                }
            }
        }
    }
}
