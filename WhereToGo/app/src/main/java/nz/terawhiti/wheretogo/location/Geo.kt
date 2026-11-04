package nz.terawhiti.wheretogo.location

import java.util.Locale
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object Geo {
    private const val EARTH_METRES = 6_371_000.0

    fun distanceMetres(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val p1 = Math.toRadians(lat1)
        val p2 = Math.toRadians(lat2)
        val dp = Math.toRadians(lat2 - lat1)
        val dl = Math.toRadians(lon2 - lon1)
        val a = sin(dp / 2).pow(2) + cos(p1) * cos(p2) * sin(dl / 2).pow(2)
        return 2 * EARTH_METRES * asin(sqrt(a))
    }

    /** Degrees clockwise from north, from the first point toward the second. */
    fun bearingDegrees(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val p1 = Math.toRadians(lat1)
        val p2 = Math.toRadians(lat2)
        val dl = Math.toRadians(lon2 - lon1)
        val y = sin(dl) * cos(p2)
        val x = cos(p1) * sin(p2) - sin(p1) * cos(p2) * cos(dl)
        return (Math.toDegrees(atan2(y, x)) + 360.0) % 360.0
    }

    fun compass(bearingDegrees: Double): String {
        val dirs = listOf(
            "north", "northeast", "east", "southeast",
            "south", "southwest", "west", "northwest",
        )
        val normalized = ((bearingDegrees % 360.0) + 360.0) % 360.0
        val index = ((normalized + 22.5) / 45.0).toInt() % 8
        return dirs[index]
    }

    fun formatDistance(metres: Double): String {
        if (metres < 1000.0) return "${metres.toInt()} m"
        return String.format(Locale.US, "%.1f km", metres / 1000.0)
    }
}
