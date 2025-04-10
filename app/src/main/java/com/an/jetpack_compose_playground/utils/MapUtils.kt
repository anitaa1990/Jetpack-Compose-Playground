package com.an.jetpack_compose_playground.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object MapUtils {
    private val EARTH_RADIUS_KM = 6371.0  // Average radius of the Earth

    /**
     * Calculates the shortest distance between two points on Earth
     * using the Haversine formula.
     *
     * Haversine accounts for Earth's curvature.
     *
     * @param start starting coordinate (latitude, longitude)
     * @param end ending coordinate
     * @return distance in kilometers (Double)
     */
    fun calculateHaversineDistance(start: LatLng, end: LatLng): Double {
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)

        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Linearly interpolates (lerps) between two colors based on a given [fraction].
     * Used to create smooth gradients between a start and end color.
     *
     * @param startColor The ARGB color to start from (Int representation).
     * @param endColor The ARGB color to interpolate to (Int representation).
     * @param fraction A value between 0.0 and 1.0 representing interpolation progress.
     *                 0.0 = startColor, 1.0 = endColor, 0.5 = halfway blend.
     *
     * @return The interpolated color as a packed ARGB Int.
     */
    fun lerpColor(startColor: Int, endColor: Int, fraction: Float): Int {
        // Decompose startColor into alpha, red, green, blue (8-bit components)
        val startA = (startColor shr 24) and 0xff // alpha
        val startR = (startColor shr 16) and 0xff // red
        val startG = (startColor shr 8) and 0xff  // green
        val startB = startColor and 0xff          // blue

        // Decompose endColor into alpha, red, green, blue (8-bit components)
        val endA = (endColor shr 24) and 0xff
        val endR = (endColor shr 16) and 0xff
        val endG = (endColor shr 8) and 0xff
        val endB = endColor and 0xff

        // Linearly interpolate each channel using the formula:
        // result = start + ((end - start) * fraction)
        val a = (startA + ((endA - startA) * fraction)).toInt()
        val r = (startR + ((endR - startR) * fraction)).toInt()
        val g = (startG + ((endG - startG) * fraction)).toInt()
        val b = (startB + ((endB - startB) * fraction)).toInt()

        // Recombine the channels into a single ARGB Int:
        // (alpha << 24) | (red << 16) | (green << 8) | blue
        return (a and 0xff shl 24) or
                (r and 0xff shl 16) or
                (g and 0xff shl 8) or
                (b and 0xff)
    }

    /**
     * Calculates the initial bearing (azimuth) between two geographical points on the Earth's surface.
     *
     * The bearing is the angle between the line connecting the two points and a reference direction (usually North).
     * This method uses the Haversine formula to calculate the bearing based on the latitudes and longitudes of the two points.
     * The result is given in degrees, ranging from -180 to +180, where:
     * - 0° is pointing North,
     * - 90° is pointing East,
     * - 180° is pointing South,
     * - -90° is pointing West.
     *
     * @param start The starting point, represented as a LatLng object.
     * @param end The destination point, represented as a LatLng object.
     * @return The bearing in degrees between the two points.
     */
    fun getBearing(start: LatLng, end: LatLng): Float {
        // Convert the latitudes and longitudes from degrees to radians
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)

        // Calculate the difference in longitude between the two points
        val dLon = lon2 - lon1

        // Use the spherical law of cosines to calculate the bearing components
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        // Calculate the initial bearing (azimuth) and convert it from radians to degrees
        return Math.toDegrees(atan2(y, x)).toFloat() // Return the result as a float in degrees
    }

    /**
     * Linearly interpolates between two angles, taking into account the shortest direction around the circle.
     *
     * This function ensures that the interpolation between two angles (start and end) is always done in the shortest
     * direction, whether it's clockwise or counterclockwise. It can be useful for smoothly rotating objects, such as
     * rotating a vehicle or a map marker in a game or app.
     *
     * @param start The starting angle in degrees.
     * @param end The ending angle in degrees.
     * @param fraction The fraction of the way from the start angle to the end angle (0.0 to 1.0).
     * @return The interpolated angle in degrees.
     */
    fun lerpAngle(start: Float, end: Float, fraction: Float): Float {
        // Calculate the shortest angle difference, considering wrapping around 360 degrees
        var delta = (end - start + 360) % 360
        if (delta > 180) delta -= 360 // Adjust if the difference is greater than 180 to avoid unnecessary wrapping

        // Linearly interpolate between the start and end angles
        return (start + delta * fraction + 360) % 360 // Ensure the result is within the 0-360 range
    }

    /**
     * Cubic easing function for smooth animation transitions.
     *
     * Provides a gradual start (ease-in), fast middle, and gradual stop (ease-out),
     * which looks more natural than linear motion.
     *
     * @param t the normalized time or progress (range 0.0 to 1.0)
     * @return eased value also in range [0, 1]
     */
    fun EaseInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4 * t * t * t
        } else {
            1 - (-2 * t + 2).let { it * it * it } / 2
        }
    }

    /**
     * Decodes a Google Maps encoded polyline into a list of LatLng points.
     *
     * Google Maps API uses an encoding method to compress a list of latitude and longitude points
     * into a single string for more efficient transmission. This method decodes such an encoded polyline
     * back into a list of LatLng objects representing the geographical points.
     *
     * The encoded polyline string is processed by decoding each 5-bit chunk, reconstructing the original
     * latitude and longitude deltas, and applying them cumulatively to the current latitude and longitude.
     * The deltas are then divided by 1E5 to obtain the actual values in degrees.
     *
     * This method supports polyline encoding as specified by Google Maps, where:
     * - Each latitude and longitude value is encoded using a variable-length base-64 format.
     * - The polyline is encoded with delta values (the difference between consecutive coordinates),
     *   which are then converted to latitude and longitude deltas.
     *
     * @param encoded The encoded polyline string to decode.
     * @return A list of LatLng objects representing the decoded polyline's geographical points.
     */
    fun decodePolyline(encoded: String): List<LatLng> {
        val polyline = ArrayList<LatLng>() // List to hold decoded LatLng points
        var index = 0 // Index to track the current position in the encoded string
        val len = encoded.length // Length of the encoded string
        var lat = 0 // Current latitude, stored as an integer for precision
        var lng = 0 // Current longitude, stored as an integer for precision

        // Loop through the encoded polyline string
        while (index < len) {
            var b: Int // Temporary variable to hold byte values during decoding
            var shift = 0 // Shift value for bit manipulation
            var result = 0 // Result of combining bits from the encoded byte

            // Decode the latitude delta
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20) // Continue until we reach the last byte of the current value
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat // Update the cumulative latitude

            // Reset for longitude decoding
            shift = 0
            result = 0

            // Decode the longitude delta
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20) // Continue until we reach the last byte of the current value
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng // Update the cumulative longitude

            // Convert the lat/lng to actual values (divide by 1E5 to get the precision)
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            polyline.add(p) // Add the decoded LatLng point to the list
        }

        return polyline // Return the decoded polyline as a list of LatLng objects
    }
}