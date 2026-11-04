package nz.terawhiti.wheretogo.connectivity

enum class TransportKind { WIFI, CELLULAR, ETHERNET, VPN, OTHER }

data class NetworkSnapshot(
    val connected: Boolean,
    val internetCapability: Boolean,
    val validated: Boolean,
    val transports: Set<TransportKind>,
)

enum class StarlinkApi { NOT_INTEGRATED }

data class UplinkReport(
    val internetLikely: Boolean,
    val validated: Boolean,
    val transports: Set<TransportKind>,
    val starlinkApi: StarlinkApi,
    val summary: String,
)

/**
 * Seam for a future Starlink integration. This build does not call a Starlink API.
 */
interface StarlinkUplink {
    val integrated: Boolean
    val note: String
}

object NoStarlinkUplink : StarlinkUplink {
    override val integrated = false
    override val note =
        "No Starlink API is called. Starlink is only a way to restore internet. It is not a health service."
}

fun interpret(snapshot: NetworkSnapshot, starlink: StarlinkUplink = NoStarlinkUplink): UplinkReport {
    val names = snapshot.transports.joinToString(", ") { kind ->
        when (kind) {
            TransportKind.WIFI -> "Wi-Fi"
            TransportKind.CELLULAR -> "mobile data"
            TransportKind.ETHERNET -> "ethernet"
            TransportKind.VPN -> "VPN"
            TransportKind.OTHER -> "another network"
        }
    }
    val via = if (names.isBlank()) "" else " ($names)"
    val summary = when {
        !snapshot.connected ->
            "No internet right now. Calls still work if the phone is not in airplane mode. The map needs a connection. The directory and handout are already on this phone. ${starlink.note}"
        snapshot.validated ->
            "This phone looks online$via. A Starlink dish would show up as ordinary Wi-Fi, and this check cannot tell that apart. ${starlink.note}"
        else ->
            "A network is connected$via, but internet is not confirmed. Nothing is sent automatically. ${starlink.note}"
    }
    return UplinkReport(
        internetLikely = snapshot.validated,
        validated = snapshot.validated,
        transports = snapshot.transports,
        starlinkApi = if (starlink.integrated) error("This build has no Starlink API") else StarlinkApi.NOT_INTEGRATED,
        summary = summary,
    )
}
