package nz.terawhiti.wheretogo.ui

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Phone
import nz.terawhiti.wheretogo.location.LocationSummary
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.relay.PoliceRelay
import nz.terawhiti.wheretogo.ui.components.PlaceholderCard
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import java.time.ZoneId

@Composable
fun RelayScreen(
    padding: PaddingValues,
    fix: StoredFix?,
    onBack: () -> Unit,
    onPrepare: (String, String) -> Unit,
) {
    val context = LocalContext.current
    var note by rememberSaveable { mutableStateOf("") }
    var includeLocation by rememberSaveable { mutableStateOf(false) }
    val locationText = if (includeLocation) {
        fix?.let { LocationSummary.plainText(it, ZoneId.systemDefault()) }
    } else {
        null
    }
    val draft = PoliceRelay.draft(note, locationText)
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        ScreenTitle(PoliceRelay.TITLE)
        PlaceholderCard("If someone is in danger, call 111", PoliceRelay.EXPLANATION)
        Text(PoliceRelay.NON_EMERGENCY)
        Button(
            onClick = { dial(context, Phone("Call", PoliceRelay.EMERGENCY_NUMBER)) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Call 111") }
        OutlinedButton(
            onClick = { dial(context, Phone("Call", PoliceRelay.NON_EMERGENCY_NUMBER)) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Call 105") }
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note to relay") },
            minLines = 4,
        )
        Text("The note stays on this phone until you clear it or leave this screen. It is not sent to Police.")
        FilterChip(
            selected = includeLocation,
            onClick = { includeLocation = !includeLocation },
            label = { Text("Include the saved location") },
        )
        if (includeLocation && fix == null) {
            Text("No location is saved. Open Where I am and save one first. This note will not add a location.")
        }
        SelectionContainer {
            Text(draft, fontFamily = FontFamily.Monospace)
        }
        Button(onClick = { shareText(context, PoliceRelay.TITLE, draft) }, modifier = Modifier.fillMaxWidth()) {
            Text("Share the note")
        }
        OutlinedButton(onClick = { smsDraft(context, draft) }, modifier = Modifier.fillMaxWidth()) {
            Text("Send as SMS")
        }
        OutlinedButton(
            onClick = { onPrepare(PoliceRelay.TITLE, draft) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Prepare to share later") }
        TextButton(onClick = { note = "" }) { Text("Clear the note") }
    }
}
