package nz.terawhiti.wheretogo.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ConnectionCheck(context: Context) {
    private val appContext = context.applicationContext
    private val executor = Executors.newSingleThreadExecutor()

    fun snapshot(): NetworkSnapshot {
        val manager = appContext.getSystemService(ConnectivityManager::class.java)
            ?: return NetworkSnapshot(false, false, false, emptySet())
        val network = manager.activeNetwork ?: return NetworkSnapshot(false, false, false, emptySet())
        val caps = manager.getNetworkCapabilities(network) ?: return NetworkSnapshot(false, false, false, emptySet())
        val transports = buildSet {
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) add(TransportKind.WIFI)
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) add(TransportKind.CELLULAR)
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) add(TransportKind.ETHERNET)
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) add(TransportKind.VPN)
            if (isEmpty()) add(TransportKind.OTHER)
        }
        return NetworkSnapshot(
            connected = true,
            internetCapability = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET),
            validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED),
            transports = transports,
        )
    }

    /** A general internet check. It does not call Starlink. */
    fun probe(onResult: (String) -> Unit) {
        executor.execute {
            val detail = probeNow()
            appContext.mainExecutor.execute { onResult(detail) }
        }
    }

    private fun probeNow(): String {
        return try {
            val connection = (URL("https://connectivitycheck.gstatic.com/generate_204").openConnection() as HttpURLConnection)
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.instanceFollowRedirects = false
            connection.requestMethod = "GET"
            val code = connection.responseCode
            connection.disconnect()
            if (code == 204 || code in 200..399) {
                "A general internet check got a response (HTTP $code). ${NoStarlinkUplink.note}"
            } else {
                "A general internet check did not confirm a connection (HTTP $code). ${NoStarlinkUplink.note}"
            }
        } catch (_: Exception) {
            "A general internet check did not reach the network. Calls may still work. ${NoStarlinkUplink.note}"
        }
    }
}
