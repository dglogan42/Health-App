package nz.terawhiti.wheretogo.location

enum class FixSource { CURRENT, LAST_KNOWN }

/**
 * One location the user asked for. This is not a track.
 * [CURRENT] is a fresh reading. [LAST_KNOWN] is the last fix the phone already had.
 */
data class StoredFix(
    val latitude: Double,
    val longitude: Double,
    val accuracyMetres: Float?,
    val capturedAtEpochMs: Long,
    val source: FixSource,
)
