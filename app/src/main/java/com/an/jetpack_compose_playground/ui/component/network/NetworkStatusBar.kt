package com.an.jetpack_compose_playground.ui.component.network

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import com.an.jetpack_compose_playground.R

@ExperimentalAnimationApi   // Required for animations in Jetpack Compose
@ExperimentalCoroutinesApi  // Required for using coroutines in Compose
@Composable
fun NetworkStatusBar(
    isConnected: Boolean
) {
    // State to control the visibility of the status bar.
    var visibility by remember { mutableStateOf(false) }

    // Animates the visibility of the status bar with vertical expansion and shrink effects.
    AnimatedVisibility(
        visible = visibility, // Controls whether the status bar is visible.
        enter = expandVertically(), // Animation for appearing: expands vertically.
        exit = shrinkVertically()   // Animation for disappearing: shrinks vertically.
    ) {
        // Display the status bar with the appropriate connectivity message and color.
        NetworkStatusBox(isConnected = isConnected)
    }

    // React to changes in connectivity state.
    LaunchedEffect(isConnected) {
        if (!isConnected) {
            visibility = true // Show the status bar when disconnected.
        } else {
            delay(2000) // Delay hiding the status bar for 2 seconds after reconnecting.
            visibility = false // Hide the status bar after the delay.
        }
    }
}

/**
 * A composable function to display the connectivity status message.
 *
 * @param isConnected A boolean indicating whether the network is connected.
 */
@Composable
private fun NetworkStatusBox(isConnected: Boolean) {
    // Animate the background color change based on the connectivity state.
    val backgroundColor by animateColorAsState(
        if (isConnected) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else MaterialTheme.colorScheme.error,
        label = ""
    )
    val message = if (isConnected) {
        stringResource(R.string.network_status_wifi_on)
    } else stringResource(R.string.network_status_wifi_off)

    val iconResource = if (isConnected) {
        Icons.Default.Wifi // Icon for "connected" status.
    } else {
        Icons.Default.WifiOff // Icon for "disconnected" status.
    }

    // A box to display the connectivity message and icon.
    Box(
        modifier = Modifier
            .background(backgroundColor) // Background color changes based on connection state.
            .fillMaxWidth() // Make the box span the entire width.
            .wrapContentHeight()
            .padding(8.dp), // Padding around the content.
        contentAlignment = Alignment.Center // Center the content inside the box.
    ) {
        // Row to display an icon and a message horizontally.
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon representing the current connectivity state.
            Icon(
                imageVector = iconResource, // Resource ID for the icon.
                contentDescription = "", // Content description for accessibility.
                tint = Color.White // Icon color.
            )
            Spacer(modifier = Modifier.size(8.dp)) // Spacer between the icon and the text.
            // Text displaying the connectivity message.
            Text(
                text = message,
                color = Color.White, // Text color.
                fontSize = 15.sp // Font size.
            )
        }
    }
}
