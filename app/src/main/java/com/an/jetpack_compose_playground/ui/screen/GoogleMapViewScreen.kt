package com.an.jetpack_compose_playground.ui.screen

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.an.jetpack_compose_playground.ui.component.permission.PermissionHandler
import com.an.jetpack_compose_playground.ui.component.permission.PermissionManager

@Composable
fun GoogleMapViewScreen() {
    val context = LocalContext.current

    val permissionManager = remember { PermissionManager(context) }
    val locationPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    var isPermissionGranted by remember { mutableStateOf(
        permissionManager.arePermissionsGranted(locationPermission)
    ) }
    var requestPermission by remember { mutableStateOf(!isPermissionGranted) }

    if (requestPermission) {
        PermissionHandler(
            permissions = locationPermission,
            permissionManager = permissionManager,
            onPermissionsGranted = {
                Toast.makeText(context, "Location permission granted!", Toast.LENGTH_SHORT).show()
                requestPermission = false
                isPermissionGranted = true
            },
            rationaleDialogContent = { permissions, retry ->
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Permission Required") },
                    text = { Text("We need the following permission: ${permissions.joinToString(", ")}") },
                    confirmButton = {
                        TextButton(onClick = retry) { Text("Retry") }
                    },
                    dismissButton = {
                        TextButton(onClick = { requestPermission = false }) { Text("Cancel") }
                    }
                )
            },
            settingsDialogContent = { openSettings ->
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Permission Denied") },
                    text = { Text("Please grant permissions from Settings.") },
                    confirmButton = {
                        TextButton(onClick = openSettings) { Text("Open Settings") }
                    },
                    dismissButton = {
                        TextButton(onClick = { requestPermission = false }) { Text("Cancel") }
                    }
                )
            }
        )
    }

    if (isPermissionGranted) {
        // TODO: open GoogleMapsView
    }
}