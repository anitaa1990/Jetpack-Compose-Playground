package com.an.jetpack_compose_playground.ui.component.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * A composable function to handle runtime permission requests in a Jetpack Compose environment.
 *
 * This function abstracts the entire permission lifecycle, including:
 * - Automatically requesting permissions if they are not already granted.
 * - Showing rationale dialogs for permissions that need an explanation.
 * - Displaying a settings dialog for permissions that have been permanently denied.
 * - Triggering a callback when all permissions are granted.
 *
 * It integrates seamlessly with `PermissionManager` for permission handling logic
 * and delegates dialog UI customization to the caller.
 *
 * @param permissions The array of permissions to request (e.g., `Manifest.permission.CAMERA`).
 * @param permissionManager The `PermissionManager` instance to handle permission checks and results.
 * @param onPermissionsGranted A callback invoked when all permissions are granted.
 * @param rationaleDialogContent A composable content for the rationale dialog, allowing customization
 *                               of how the rationale is presented to the user.
 * @param settingsDialogContent A composable content for the settings dialog, allowing customization
 *                              of how the app guides users to enable permissions manually in settings.
 */
@Composable
fun PermissionHandler(
    permissions: Array<String>,
    permissionManager: PermissionManager,
    onPermissionsGranted: () -> Unit,
    rationaleDialogContent: @Composable (List<String>, () -> Unit) -> Unit,
    settingsDialogContent: @Composable (() -> Unit) -> Unit
) {
    val context = LocalContext.current

    // State to manage the type of dialog to display
    var dialogState by remember { mutableStateOf<PermissionDialogState>(PermissionDialogState.None) }

    // State to trigger permission requests dynamically
    var triggerPermissionRequest by remember { mutableStateOf(false) }

    // A launcher to request multiple permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // Handle permission results using PermissionManager
        permissionManager.handlePermissionsResult(
            permissions = permissions,
            result = result,
            onGranted = onPermissionsGranted,
            onDenied = { denied, rationale ->
                // Update dialogState based on the permissions that require a rationale or are permanently denied
                when {
                    rationale.isNotEmpty() -> {
                        dialogState = PermissionDialogState.Rationale(rationale) {
                            triggerPermissionRequest = true // Retry rationale permissions
                        }
                    }
                    denied.isNotEmpty() -> {
                        // Show Settings dialog for permanently denied permissions
                        dialogState = PermissionDialogState.Settings
                    }
                }
            }
        )
    }

    // Trigger permission request when the state changes
    LaunchedEffect(triggerPermissionRequest) {
        if (triggerPermissionRequest) {
            permissionLauncher.launch(permissions)
            triggerPermissionRequest = false // Reset state after launching permissions
        }
    }

    // Automatically request permissions if they are not already granted
    LaunchedEffect(Unit) {
        if (!permissionManager.arePermissionsGranted(permissions)) {
            triggerPermissionRequest = true
        } else {
            onPermissionsGranted() // If permissions are already granted, invoke the callback immediately
        }
    }

    // Render the appropriate dialog based on the current dialogState
    when (val state = dialogState) {
        is PermissionDialogState.Rationale -> {
            // Show the rationale dialog with the provided content
            rationaleDialogContent(state.permissions, state.retryAction)
        }
        is PermissionDialogState.Settings -> {
            // Show the settings dialog with the provided content
            settingsDialogContent {
                // Open the app's settings page
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }
        PermissionDialogState.None -> {
            // No dialog to show
        }
    }
}

/**
 * A sealed class to represent the state of permission dialogs.
 *
 * This is used to manage the type of dialog that should be displayed
 * based on the current permission state (e.g., rationale or settings dialog).
 */
sealed class PermissionDialogState {

    /**
     * No dialog is currently displayed.
     */
    object None : PermissionDialogState()

    /**
     * A rationale dialog is displayed, asking the user to grant permissions.
     *
     * @property permissions The list of permissions that require a rationale.
     * @property retryAction The action to retry the permission request.
     */
    data class Rationale(
        val permissions: List<String>,
        val retryAction: () -> Unit
    ) : PermissionDialogState()

    /**
     * A settings dialog is displayed, prompting the user to manually enable permissions
     * from the app's settings page.
     */
    object Settings : PermissionDialogState()
}
