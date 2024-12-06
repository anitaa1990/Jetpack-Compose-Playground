package com.an.jetpack_compose_playground.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.an.jetpack_compose_playground.ui.common.MainScaffold
import com.an.jetpack_compose_playground.ui.component.permission.PermissionHandler
import com.an.jetpack_compose_playground.ui.component.permission.PermissionManager

@Composable
fun RuntimePermissionScreen() {
    val context = LocalContext.current

    // Define the permissions required
    val cameraPermission = arrayOf(android.Manifest.permission.CAMERA)
    val multiplePermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.READ_CONTACTS
    )

    // State variables to trigger permission requests
    var requestSinglePermission by remember { mutableStateOf(false) }
    var requestMultiplePermissions by remember { mutableStateOf(false) }

    // Create an instance of PermissionManager
    val permissionManager = remember { PermissionManager(context) }

    MainScaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button for single camera permission
            Button(onClick = { requestSinglePermission = true }) {
                Text("Request Camera Permission")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button for multiple permissions
            Button(onClick = { requestMultiplePermissions = true }) {
                Text("Request Location & Contacts Permissions")
            }
        }
    }

    // Single PermissionHandler
    if (requestSinglePermission) {
        PermissionHandler(
            permissions = cameraPermission,
            permissionManager = permissionManager,
            onPermissionsGranted = {
                Toast.makeText(context, "Camera permission granted!", Toast.LENGTH_SHORT).show()
                requestSinglePermission = false
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
                        TextButton(onClick = { requestSinglePermission = false }) { Text("Cancel") }
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
                        TextButton(onClick = { requestSinglePermission = false }) { Text("Cancel") }
                    }
                )
            }
        )
    }

    // Multiple PermissionHandler
    if (requestMultiplePermissions) {
        PermissionHandler(
            permissions = multiplePermissions,
            permissionManager = permissionManager,
            onPermissionsGranted = {
                Toast.makeText(context, "All permissions granted!", Toast.LENGTH_SHORT).show()
                requestMultiplePermissions = false
            },
            rationaleDialogContent = { permissions, retry ->
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Permissions Required") },
                    text = { Text("We need the following permissions: ${permissions.joinToString(", ")}") },
                    confirmButton = {
                        TextButton(onClick = retry) { Text("Retry") }
                    },
                    dismissButton = {
                        TextButton(onClick = { requestMultiplePermissions = false }) { Text("Cancel") }
                    }
                )
            },
            settingsDialogContent = { openSettings ->
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Permissions Denied") },
                    text = { Text("Please grant permissions from Settings.") },
                    confirmButton = {
                        TextButton(onClick = openSettings) { Text("Open Settings") }
                    },
                    dismissButton = {
                        TextButton(onClick = { requestMultiplePermissions = false }) { Text("Cancel") }
                    }
                )
            }
        )
    }
}
