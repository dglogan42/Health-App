package nz.terawhiti.wheretogo.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Handout
import nz.terawhiti.wheretogo.data.Phone
import java.io.File

fun dial(context: Context, phone: Phone) {
    val digits = phone.number.filter { it.isDigit() || it == '+' }
    start(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits")), "No phone app is available.")
}

fun text(context: Context, phone: Phone) {
    val digits = phone.number.filter { it.isDigit() || it == '+' }
    start(context, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$digits")), "No messaging app is available.")
}

fun smsDraft(context: Context, body: String) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).putExtra("sms_body", body)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        shareText(context, "Message", body)
    }
}

fun shareText(context: Context, title: String, body: String) {
    val share = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, title)
        .putExtra(Intent.EXTRA_TEXT, body)
    start(context, Intent.createChooser(share, title), "Nothing on this phone can share that.")
}

fun copyText(context: Context, label: String, body: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    if (clipboard == null) {
        toast(context, "This phone has no clipboard.")
        return
    }
    clipboard.setPrimaryClip(ClipData.newPlainText(label, body))
    toast(context, "Copied. It stays on this phone until you paste it somewhere.")
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
    } catch (_: Exception) {
        toast(context, "The handout could not be opened. The questions on this page are the same text.")
    }
}

fun clinicMap(context: Context) {
    val url =
        "https://www.openstreetmap.org/?mlat=${Directory.LAT}&mlon=${Directory.LON}#map=17/${Directory.LAT}/${Directory.LON}"
    openUrl(context, url)
}

fun clinicDirections(context: Context) {
    val geo = Uri.parse("geo:${Directory.LAT},${Directory.LON}?q=${Uri.encode(Directory.ADDRESS)}")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, geo))
    } catch (_: ActivityNotFoundException) {
        openUrl(
            context,
            "https://www.openstreetmap.org/directions?to=${Directory.LAT}%2C${Directory.LON}",
        )
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    )
    start(context, intent, "Settings could not be opened.")
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
