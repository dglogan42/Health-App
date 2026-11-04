package nz.terawhiti.wheretogo.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

sealed class FixAttempt {
    data class Ready(val fix: StoredFix) : FixAttempt()
    data object NeedsPermission : FixAttempt()
    data class Failed(val message: String) : FixAttempt()
}

/**
 * Reads a location only when [fetch] is called. No background updates and no stored trail.
 */
class OneShotLocation(context: Context) {
    private val appContext = context.applicationContext
    private val executor = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    fun fetch(source: FixSource, onResult: (FixAttempt) -> Unit) {
        val manager = appContext.getSystemService(LocationManager::class.java)
        if (manager == null) {
            deliver(onResult, FixAttempt.Failed("This phone has no location service."))
            return
        }
        if (!hasLocationPermission(appContext)) {
            deliver(onResult, FixAttempt.NeedsPermission)
            return
        }
        try {
            if (source == FixSource.LAST_KNOWN) {
                deliver(onResult, lastKnown(manager))
            } else {
                current(manager, onResult)
            }
        } catch (_: SecurityException) {
            deliver(onResult, FixAttempt.NeedsPermission)
        }
    }

    private fun lastKnown(manager: LocationManager): FixAttempt {
        val fixes = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).mapNotNull { provider ->
            try {
                manager.getLastKnownLocation(provider)
            } catch (_: SecurityException) {
                null
            }
        }
        val best = fixes.maxByOrNull { it.time }
        return when {
            best != null -> FixAttempt.Ready(best.toFix(FixSource.LAST_KNOWN))
            !providerEnabled(manager) ->
                FixAttempt.Failed("Turn on location, or tell someone the address in your own words.")
            else ->
                FixAttempt.Failed("Location is not ready yet. Try again in a moment, or tell someone the address in your own words.")
        }
    }

    private fun current(manager: LocationManager, onResult: (FixAttempt) -> Unit) {
        val provider = when {
            manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> {
                deliver(onResult, FixAttempt.Failed("Turn on location, or tell someone the address in your own words."))
                return
            }
        }
        val delivered = AtomicBoolean(false)
        fun finish(attempt: FixAttempt) {
            if (delivered.compareAndSet(false, true)) deliver(onResult, attempt)
        }
        val notReady = FixAttempt.Failed(
            "Location is not ready yet. Try again in a moment, or tell someone the address in your own words.",
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val signal = CancellationSignal()
            manager.getCurrentLocation(provider, signal, executor) { location ->
                if (location != null) finish(FixAttempt.Ready(location.toFix(FixSource.CURRENT)))
                else finish(notReady)
            }
            main.postDelayed({
                if (!delivered.get()) {
                    signal.cancel()
                    finish(notReady)
                }
            }, 20_000)
        } else {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    manager.removeUpdates(this)
                    finish(FixAttempt.Ready(location.toFix(FixSource.CURRENT)))
                }

                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

                override fun onProviderEnabled(provider: String) = Unit
                override fun onProviderDisabled(provider: String) = Unit
            }
            @Suppress("DEPRECATION")
            manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            main.postDelayed({
                if (!delivered.get()) {
                    manager.removeUpdates(listener)
                    finish(notReady)
                }
            }, 20_000)
        }
    }

    private fun providerEnabled(manager: LocationManager): Boolean =
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    private fun deliver(onResult: (FixAttempt) -> Unit, attempt: FixAttempt) {
        if (Looper.myLooper() == Looper.getMainLooper()) onResult(attempt)
        else main.post { onResult(attempt) }
    }
}

private fun Location.toFix(source: FixSource) = StoredFix(
    latitude = latitude,
    longitude = longitude,
    accuracyMetres = if (hasAccuracy()) accuracy else null,
    capturedAtEpochMs = time,
    source = source,
)
