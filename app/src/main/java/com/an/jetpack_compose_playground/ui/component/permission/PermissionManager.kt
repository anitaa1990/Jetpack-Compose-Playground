package com.an.jetpack_compose_playground.ui.component.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * A utility class for managing runtime permissions in Android.
 *
 * This class simplifies the process of checking permissions, handling the results of permission requests,
 * and determining whether a rationale should be shown to the user. It abstracts away the repetitive
 * boilerplate code typically associated with runtime permissions, providing a clean interface for managing
 * permissions in an Android application.
 *
 * @param context The context of the calling component (usually an Activity or Fragment).
 */
class PermissionManager(
    private val context: Context
) {

    /**
     * Checks if all specified permissions are already granted.
     *
     * This method determines whether all the permissions in the given array have already been granted.
     * It's typically used to decide if a permission request is necessary.
     *
     * @param permissions An array of permission strings to check (e.g., `Manifest.permission.CAMERA`).
     * @return `true` if all permissions are granted, otherwise `false`.
     */
    fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Handles the results of a permission request.
     *
     * This method processes the results of a permission request and categorizes them into:
     * - Granted permissions: All permissions that were successfully granted by the user.
     * - Denied permissions: Permissions that were denied by the user.
     * - Rationale-needed permissions: Permissions that were denied and require an explanation
     *   (via `shouldShowRequestPermissionRationale`).
     *
     * Based on the categorization, this method invokes the appropriate callback for granted or denied permissions.
     *
     * @param permissions The permissions that were requested.
     * @param result A map where the key is the permission string, and the value is a boolean indicating
     *               whether the permission was granted (`true`) or denied (`false`).
     * @param onGranted A callback invoked when all requested permissions are granted.
     *                  Use this to proceed with the operation that requires permissions.
     * @param onDenied A callback invoked when one or more permissions are denied.
     *                 - The first parameter is the list of denied permissions.
     *                 - The second parameter is the list of permissions that require a rationale.
     */
    fun handlePermissionsResult(
        permissions: Array<String>,
        result: Map<String, Boolean>,
        onGranted: () -> Unit,
        onDenied: (List<String>, List<String>) -> Unit
    ) {
        // Filter out the permissions that were denied by the user
        val deniedPermissions = permissions.filter { result[it] == false }

        // Identify permissions that require a rationale (e.g., the user previously denied them)
        val rationaleNeeded = deniedPermissions.filter {
            ActivityCompat.shouldShowRequestPermissionRationale(context as androidx.activity.ComponentActivity, it)
        }

        // If no permissions were denied, invoke the "granted" callback
        if (deniedPermissions.isEmpty()) {
            onGranted()
        } else {
            // Otherwise, invoke the "denied" callback with the denied permissions and rationale-needed permissions
            onDenied(deniedPermissions, rationaleNeeded)
        }
    }
}
