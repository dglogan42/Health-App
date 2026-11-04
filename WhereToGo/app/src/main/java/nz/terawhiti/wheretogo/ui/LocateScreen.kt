package nz.terawhiti.wheretogo.ui

import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.connectivity.UplinkReport
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.location.LocationSummary
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.outbox.OutboxItem
import nz.terawhiti.wheretogo.ui.components.PlaceholderCard
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import nz.terawhiti.wheretogo.ui.theme.CardWhite
import nz.terawhiti.wheretogo.ui.theme.Emergency
import java.time.ZoneId

@Composable
fun LocateScreen(
    padding: PaddingValues,
    fix: StoredFix?,
    status: String?,
    busy: Boolean,
    uplink: UplinkReport?,
    probeNote: String?,
    checkingLink: Boolean,
    outbox: List<OutboxItem>,
    onFresh: () -> Unit,
    onLastKnown: () -> Unit,
    onDeleteFix: () -> Unit,
    onPrepare: (String, String) -> Unit,
    onRefreshLink: () -> Unit,
    onProbe: () -> Unit,
    onRemoveQueued: (Long) -> Unit,
    onClearQueued: () -> Unit,
) {
    val context = LocalContext.current
    val summary = fix?.let { LocationSummary.plainText(it, ZoneId.systemDefault()) }
    val airplane = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1
    LaunchedEffect(Unit) { onRefreshLink() }
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTitle("Where I am")
        PlaceholderCard(
            "This does not replace 111",
            "This helps you say where someone is. It does not send help and it does not call anyone. If someone is in danger, call 111.",
        )
        if (airplane) {
            Text("Airplane mode is on. A GPS fix can still work. Calls and texts wait until the phone can use a network.")
        }
        Text("A fix you save stays on this phone until you delete it. The app does not track you in the background. Nothing is shared until you tap Share, SMS, or copy.")
        if (summary == null) {
            Text("No location is saved. A fresh fix uses satellites and can work with no internet. The last stored phone location can work when a new fix is not ready. Nothing is read until you tap a button.")
        }
        status?.let { Text(it, color = Emergency, fontWeight = FontWeight.Medium) }
        Button(onClick = onFresh, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
            Text(if (busy) "Looking…" else "Save a fresh GPS fix")
        }
        OutlinedButton(onClick = onLastKnown, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
            Text("Use the phone’s last stored location")
        }
        TextButton(onClick = { openAppSettings(context) }) { Text("Location permission settings") }
        if (summary != null) {
            Text("Saved on this phone until you delete it.", fontWeight = FontWeight.Medium)
            SelectionContainer {
                Text(summary, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = { shareText(context, "Where I am", summary) }, modifier = Modifier.fillMaxWidth()) {
                Text("Share where I am")
            }
            OutlinedButton(onClick = { smsDraft(context, summary) }, modifier = Modifier.fillMaxWidth()) {
                Text("Send as SMS")
            }
            OutlinedButton(onClick = { copyText(context, "Where I am", summary) }, modifier = Modifier.fillMaxWidth()) {
                Text("Copy the note")
            }
            OutlinedButton(
                onClick = { onPrepare("Location note", summary) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Prepare to share later") }
            Text("Prepare keeps a copy on this phone. A connection does not send it.")
            OutlinedButton(onClick = onDeleteFix, modifier = Modifier.fillMaxWidth()) {
                Text("Delete saved location")
            }
        }
        ScreenTitle("Connection")
        Text("Starlink is only a way to restore internet. It is not a health service. This check does not call a Starlink API.")
        uplink?.let { Text(it.summary) }
        probeNote?.let { Text(it) }
        Button(onClick = onProbe, enabled = !checkingLink, modifier = Modifier.fillMaxWidth()) {
            Text(if (checkingLink) "Checking…" else "Check connection")
        }
        OutlinedButton(
            onClick = { openUrl(context, Directory.named("Starlink").links.first().url) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Open Starlink") }
        Text("You can open this link when you have a connection.")
        if (outbox.isNotEmpty()) {
            Text(
                "Waiting on this phone",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (uplink?.internetLikely == true) {
                Text("A connection looks available. These notes are still only on this phone until you share them.")
            } else {
                Text("Nothing sends by itself. Share a note when you have a way to pass it on.")
            }
            for (item in outbox) {
                Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(item.title, fontWeight = FontWeight.Bold)
                        Text(item.body, maxLines = 6)
                        Button(onClick = { shareText(context, item.title, item.body) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Share this note")
                        }
                        OutlinedButton(onClick = { smsDraft(context, item.body) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Send as SMS")
                        }
                        TextButton(onClick = { onRemoveQueued(item.id) }) { Text("Remove") }
                    }
                }
            }
            TextButton(onClick = onClearQueued) { Text("Remove all waiting notes") }
        }
        ScreenTitle("Clinic")
        Text(Directory.CLINIC, fontWeight = FontWeight.Bold)
        Text(Directory.ADDRESS)
        Text("Monday to Friday, 8am to 5pm.")
        Button(onClick = { clinicMap(context) }, modifier = Modifier.fillMaxWidth()) { Text("Open in OpenStreetMap") }
        OutlinedButton(onClick = { clinicDirections(context) }, modifier = Modifier.fillMaxWidth()) { Text("Directions") }
        OutlinedButton(
            onClick = { dial(context, Directory.clinic.phones.first()) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Call the clinic") }
        Text("Map data © OpenStreetMap contributors. The map link needs internet. The address above does not.")
    }
}
