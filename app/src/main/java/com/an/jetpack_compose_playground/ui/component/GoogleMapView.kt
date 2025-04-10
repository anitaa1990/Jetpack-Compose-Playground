package com.an.jetpack_compose_playground.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.an.jetpack_compose_playground.R
import com.an.jetpack_compose_playground.utils.MapUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.delay

/**
 * A Composable function that displays a Google Map with a pickup and drop location,
 * along with a polyline route connecting these locations.
 *
 * The map is initially centered between the pickup and drop locations with an animated camera movement.
 * Markers are added at both the pickup and drop locations, and the polyline route is drawn between them.
 * Each segment of the polyline is color-interpolated from bright yellow (pickup end) to black (drop end).
 *
 * This composable also includes the functionality to simulate the movement of a car along the route.
 * The car is represented by a custom icon, and its position updates in real-time based on the polyline's path.
 *
 * @param pickupLocation The geographical location (LatLng) of the pickup point.
 * @param dropLocation The geographical location (LatLng) of the drop point.
 * @param routePolyline A list of LatLng coordinates representing the route between the pickup and drop locations.
 */
@Composable
fun GoogleMapView(
    pickupLocation: LatLng,
    dropLocation: LatLng,
    routePolyline: List<LatLng>
) {
    val context = LocalContext.current

    // Camera position state to manage map's camera (center, zoom, bearing, tilt)
    val cameraPositionState = rememberCameraPositionState()

    // Launch effect to animate the camera position whenever pickup or drop location changes
    LaunchedEffect(pickupLocation, dropLocation) {
        val bounds = LatLngBounds.builder()
            .include(pickupLocation)
            .include(dropLocation)
            .build()

        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, 15),
            durationMs = 1000
        )
    }

    // Google Map composable with various configurations
    GoogleMap(
        modifier = Modifier.fillMaxSize(),  // Map fills the entire available screen space
        cameraPositionState = cameraPositionState,  // Holds current camera position state
        properties = MapProperties(),  // Define map properties like traffic, satellite view, etc.
        uiSettings = MapUiSettings()  // Map UI settings for gestures and controls
    ) {
        // Create and display the pickup marker with custom icon
        val pickupIcon = remember {
            BitmapDescriptorFactory.fromBitmap(
                ContextCompat.getDrawable(context, R.drawable.ic_pickup_marker)!!.toBitmap()
            )
        }
        val pickLocationState = rememberUpdatedMarkerState(position = pickupLocation)
        Marker(
            state = pickLocationState,
            icon = pickupIcon,
            anchor = Offset(0.5f, 0.5f),
            title = "Home"
        )

        // Create and display the drop marker with custom icon
        val dropIcon = remember {
            BitmapDescriptorFactory.fromBitmap(
                ContextCompat.getDrawable(context, R.drawable.ic_drop_marker)!!.toBitmap()
            )
        }
        val dropLocationState = rememberUpdatedMarkerState(position = dropLocation)
        Marker(
            state = dropLocationState,
            icon = dropIcon,
            anchor = Offset(0.5f, 0.5f),
            title = "Work"
        )

        // Draw the route polyline with color interpolation between the pickup and drop points
        for (i in 0 until routePolyline.lastIndex) {
            val fraction = i.toFloat() / routePolyline.lastIndex

            val color = MapUtils.lerpColor(
                startColor = Color(0xFFFAC901).toArgb(),  // Bright Yellow (pickup end)
                endColor = Color.Black.toArgb(),          // Black (drop end)
                fraction = fraction  // Fraction used to interpolate color
            )

            Polyline(
                points = listOf(routePolyline[i], routePolyline[i + 1]),  // Draw segment between two points
                color = Color(color),  // Color interpolated between pickup and drop
                width = 10f  // Line thickness
            )
        }

        // Simulate car movement along the polyline
        val carIcon = remember {
            BitmapDescriptorFactory.fromBitmap(
                ContextCompat.getDrawable(context, R.drawable.ic_marker_auto)!!.toBitmap()
            )
        }

        // Function to simulate car movement
        SimulateCarMovement(
            routePolyline = routePolyline,
            carIcon = carIcon
        )
    }
}

/**
 * A Composable function to simulate the movement of a vehicle (e.g., car or bike) along a polyline route.
 *
 * The vehicle's marker smoothly moves from the start of the polyline to the end, updating its position and rotation
 * at each step. The car’s speed is adjustable, and its rotation smoothly interpolates to match the direction of travel.
 *
 * The movement is animated frame-by-frame, creating a smooth transition that mimics real-world vehicle movement.
 * The car’s position is updated along the route based on the computed distance and speed, and easing functions are used
 * to create realistic acceleration and deceleration.
 *
 * @param routePolyline The list of LatLng coordinates representing the route the vehicle will follow.
 * @param carIcon The custom icon representing the vehicle on the map.
 * @param speedKmph The speed of the vehicle in kilometers per hour, which affects the animation speed.
 */
@Composable
fun SimulateCarMovement(
    routePolyline: List<LatLng>,
    carIcon: BitmapDescriptor,
    speedKmph: Double = 20.0
) {

    // Car's initial position is set to the first point in the polyline
    val carPosition = remember { mutableStateOf(routePolyline.first()) }

    // Car's initial rotation (orientation) in degrees
    val carRotation = remember { mutableFloatStateOf(0f) }

    // Marker state to track the car's position and rotation
    val markerState = rememberUpdatedMarkerState(position = carPosition.value)

    // Display the car's marker on the map
    Marker(
        state = markerState,
        icon = carIcon,     // Vehicle icon
        flat = true,        // Ensure marker rotates on 2D plane
        anchor = Offset(0.5f, 0.5f),  // Anchor the icon at the center
        rotation = carRotation.floatValue   // Update the marker's rotation with the car's rotation
    )

    // Launch the animation of the car movement along the polyline
    LaunchedEffect(routePolyline) {
        for (i in 0 until routePolyline.lastIndex) {
            val start = routePolyline[i]
            val end = routePolyline[i + 1]

            val startPoint = LatLng(start.latitude, start.longitude)
            val endPoint = LatLng(end.latitude, end.longitude)

            val startRotation = carRotation.floatValue
            val targetRotation = MapUtils.getBearing(startPoint, endPoint)

            val distanceMeters = MapUtils.calculateHaversineDistance(startPoint, endPoint)

            val durationMs = ((distanceMeters / 1000.0) / speedKmph) * 3600_000

            // Break the segment into smaller steps for smooth animation
            val steps = 30
            val stepDuration = (durationMs / steps).coerceIn(10.0, 100.0).toLong()

            // Animate car movement in steps
            for (step in 1..steps) {
                val t = step.toFloat() / steps // Progress of the current step (0.0 to 1.0)

                // Apply easing function to smooth out the movement
                val easedT = MapUtils.EaseInOutCubic(t)

                // Interpolate between the start and end LatLng based on eased progress
                val lat = start.latitude + (end.latitude - start.latitude) * easedT
                val lng = start.longitude + (end.longitude - start.longitude) * easedT

                // Interpolate rotation to smoothly turn the vehicle
                val interpolatedRotation = MapUtils.lerpAngle(startRotation, targetRotation, easedT)

                // Update the car's position and rotation
                carPosition.value = LatLng(lat, lng)
                carRotation.floatValue = interpolatedRotation

                // Small delay between each step to control the animation speed
                delay(stepDuration)
            }
        }
    }
}
