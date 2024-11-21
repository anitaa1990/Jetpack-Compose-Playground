package com.an.jetpack_compose_playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.an.jetpack_compose_playground.AppConstants.HOME
import com.an.jetpack_compose_playground.AppConstants.ROUTE_BOOK_PAGER
import com.an.jetpack_compose_playground.ui.screen.BookPagerScreen
import com.an.jetpack_compose_playground.ui.screen.HomeScreen
import com.an.jetpack_compose_playground.ui.theme.JetpackComposePlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposePlaygroundTheme {
                val navController = rememberNavController()

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
                }
            }
        }
    }
}
