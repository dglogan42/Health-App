package nz.terawhiti.wheretogo.location

import nz.terawhiti.wheretogo.data.Directory
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object LocationSummary {
    fun plainText(fix: StoredFix, zone: ZoneId): String {
        val lat = String.format(Locale.US, "%.5f", fix.latitude)
        val lon = String.format(Locale.US, "%.5f", fix.longitude)
        val accuracy = fix.accuracyMetres?.let { "${it.toInt()} m" } ?: "not reported"
        val source = when (fix.source) {
            FixSource.CURRENT -> "fresh GPS fix"
            FixSource.LAST_KNOWN -> "last location this phone had stored"
        }
        val local = Instant.ofEpochMilli(fix.capturedAtEpochMs).atZone(zone)
        val utc = Instant.ofEpochMilli(fix.capturedAtEpochMs).atZone(ZoneOffset.UTC)
        val localText = local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"))
        val utcText = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'"))
        val metres = Geo.distanceMetres(fix.latitude, fix.longitude, Directory.LAT, Directory.LON)
        val toward = Geo.compass(Geo.bearingDegrees(fix.latitude, fix.longitude, Directory.LAT, Directory.LON))
        val map = "https://www.openstreetmap.org/?mlat=$lat&mlon=$lon#map=16/$lat/$lon"
        return buildString {
            appendLine("EMERGENCY LOCATOR NOTE — this is not a call to 111")
            appendLine("If someone is in danger, call 111.")
            appendLine("Lat: $lat")
            appendLine("Lon: $lon")
            appendLine("Accuracy: $accuracy")
            appendLine("Source: $source")
            appendLine("Captured: $localText")
            appendLine("UTC: $utcText")
            appendLine("Clinic: ${Directory.CLINIC}")
            appendLine(Directory.ADDRESS)
            appendLine("The clinic is about ${Geo.formatDistance(metres)} to the $toward of this point.")
            appendLine("Map (needs internet to open): $map")
            append("Saved on this phone. Not sent.")
        }
    }
}
