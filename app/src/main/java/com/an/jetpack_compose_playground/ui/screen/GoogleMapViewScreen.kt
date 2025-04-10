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
import com.an.jetpack_compose_playground.ui.component.GoogleMapView
import com.an.jetpack_compose_playground.ui.component.permission.PermissionHandler
import com.an.jetpack_compose_playground.ui.component.permission.PermissionManager
import com.an.jetpack_compose_playground.utils.MapUtils
import com.google.android.gms.maps.model.LatLng

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
        val pickupLocation = LatLng(12.984566, 80.264089)
        val dropLocation = LatLng(12.949640, 80.237957)
        val route = MapUtils.decodePolyline("o`gnAqq{hNWfCK~BGzBEPMXLLD?b@OfC@pBH`BLGvAGvAG|AOnEqDIe@rGIjAnAHlBN]|H]xEbEt@b@BTFJJTb@h@dB\\vCLRVP`@J`ACZ?FBHJH?|CC~@B|@BVOf@@lAVXHhAx@lAdAv@l@\\Rp@X~Bh@pB^b@LdCb@CNf@J\\FpATpKnBnDj@vCj@fD|@dD`AtDfAdCx@bC~@l@TlCv@zKxCf@Pz@`@hG~BdE|AxCdAhJ|CnDfAzA^dBj@bCt@nBp@xC~@bJhCx@ThD|@pBh@nIjBf@GHDd@Lt@TTZ`Cr@|CbAhF`BxAl@p@f@x@v@tB~BSTGIkBsBmAgASOw@a@yBq@eGkBiEuASCIDGDOLQRk@pDe@tDm@rHKzBY?YNVeDL}AZi@Dc@")
        GoogleMapView(
            pickupLocation = pickupLocation,
            dropLocation = dropLocation,
            routePolyline = route
        )
    }
}
