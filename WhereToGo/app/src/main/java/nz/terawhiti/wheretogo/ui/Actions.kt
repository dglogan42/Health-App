package nz.terawhiti.wheretogo.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Handout
import nz.terawhiti.wheretogo.data.Phone
import java.io.File
import java.util.concurrent.Executors

private val locationExecutor = Executors.newSingleThreadExecutor()

fun dial(context: Context, phone: Phone) {
    val digits = phone.number.filter { it.isDigit() || it == '+' }
    start(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits")), "No phone app is available.")
}

fun text(context: Context, phone: Phone) {
    val digits = phone.number.filter { it.isDigit() || it == '+' }
    start(context, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$digits")), "No messaging app is available.")
}

fun openUrl(context: Context, url: String) {
    start(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)), "No app can open that link.")
}

fun openHandout(context: Context) {
    try {
        val file = File(context.cacheDir, Handout.ASSET)
        if (!file.exists() || file.length() == 0L) {
            context.assets.open(Handout.ASSET).use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val view = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "application/pdf")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        start(context, view, "No PDF viewer is installed. The questions are still on this page.")
    } catch (error: Exception) {
        toast(context, "The handout could not be opened. The questions on this page are the same text.")
    }
}

fun clinicMap(context: Context) {
    val url = "https://www.openstreetmap.org/?mlat=${Directory.LAT}&mlon=${Directory.LON}#map=17/${Directory.LAT}/${Directory.LON}"
    openUrl(context, url)
}

fun clinicDirections(context: Context) {
    val geo = Uri.parse("geo:${Directory.LAT},${Directory.LON}?q=${Uri.encode(Directory.ADDRESS)}")
    val intent = Intent(Intent.ACTION_VIEW, geo)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val url = "https://www.openstreetmap.org/directions?to=${Directory.LAT}%2C${Directory.LON}"
        openUrl(context, url)
    }
}

fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}

fun shareCurrentLocation(context: Context) {
    val manager = context.getSystemService(LocationManager::class.java)
    if (manager == null) {
        toast(context, "This phone has no location service.")
        return
    }
    val provider = when {
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> {
            toast(context, "Turn on location, or tell someone the address in your own words.")
            return
        }
    }
    if (!hasLocationPermission(context)) return
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            manager.getCurrentLocation(provider, CancellationSignal(), locationExecutor) { location ->
                context.mainExecutor.execute {
                    if (location == null) toast(context, "Location is not ready yet. Try again in a moment.")
                    else shareLocation(context, location)
                }
            }
        } else {
            val last = manager.getLastKnownLocation(provider)
            if (last != null) shareLocation(context, last)
            else toast(context, "No recent location is stored. Open a map app and try again outside.")
        }
    } catch (_: SecurityException) {
        toast(context, "Location permission is off.")
    }
}

private fun shareLocation(context: Context, location: Location) {
    val url = "https://www.openstreetmap.org/?mlat=${location.latitude}&mlon=${location.longitude}#map=18/${location.latitude}/${location.longitude}"
    val text = "I am here: $url"
    val share = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, text)
    start(context, Intent.createChooser(share, "Share where I am"), "Nothing on this phone can share that.")
}

private fun start(context: Context, intent: Intent, message: String) {
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        toast(context, message)
    }
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
