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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.afterhours.AfterHoursPathway
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.location.LocationSummary
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.ui.components.PlaceholderCard
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import java.time.ZoneId

@Composable
fun AfterHoursScreen(
    padding: PaddingValues,
    fix: StoredFix?,
    onBack: () -> Unit,
    onPrepare: (String, String) -> Unit,
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var callback by rememberSaveable { mutableStateOf("") }
    var situation by rememberSaveable { mutableStateOf("") }
    var includeLocation by rememberSaveable { mutableStateOf(false) }
    val locationText = if (includeLocation) {
        fix?.let { LocationSummary.plainText(it, ZoneId.systemDefault()) }
    } else {
        null
    }
    val clinicPhone = Directory.clinic.phones.first().number
    val clinicHours = Directory.clinic.hours ?: "Monday to Friday, 8am to 5pm"
    val afterPhone = Directory.afterHours.phones.first().number
    val talkPhone = Directory.needToTalk.phones.first().number
    val draft = AfterHoursPathway.draft(
        name = name,
        callback = callback,
        situation = situation,
        locationSummary = locationText,
        clinicPhone = clinicPhone,
        clinicHours = clinicHours,
        afterHoursPhone = afterPhone,
        talkPhone = talkPhone,
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        ScreenTitle(AfterHoursPathway.TITLE)
        PlaceholderCard(AfterHoursPathway.DANGER, "Calling 111 comes first. This screen does not contact a district service for you.")
        Button(
            onClick = { dial(context, Directory.emergency.phones.first()) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Call 111") }
        Text(Directory.clinic.hours ?: "Monday to Friday, 8am to 5pm", fontWeight = FontWeight.Medium)
        Button(
            onClick = { dial(context, Directory.clinic.phones.first()) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Clinic ${Directory.clinic.phones.first().number}") }
        Text(Directory.afterHours.summary)
        Button(
            onClick = { dial(context, Directory.afterHours.phones.first()) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("After hours $afterPhone") }
        Text("You can also call or text $talkPhone.")
        Button(
            onClick = { dial(context, Directory.needToTalk.phones.first()) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Call $talkPhone") }
        OutlinedButton(
            onClick = { text(context, Directory.needToTalk.texts.first()) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Text $talkPhone") }
        PlaceholderCard("No district inbox in the saved files", AfterHoursPathway.MISSING_CHANNEL)
        Text("Write what you would say on the phone. You can read it out, or share it yourself. It is not delivered to Health New Zealand.")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name, if you want it on the note") },
            singleLine = true,
        )
        OutlinedTextField(
            value = callback,
            onValueChange = { callback = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("A number to call back, if you want one") },
            singleLine = true,
        )
        OutlinedTextField(
            value = situation,
            onValueChange = { situation = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("What is happening") },
            minLines = 4,
        )
        FilterChip(
            selected = includeLocation,
            onClick = { includeLocation = !includeLocation },
            label = { Text("Include the saved location") },
        )
        if (includeLocation && fix == null) {
            Text("No location is saved. Open Where I am and save one first.")
        }
        SelectionContainer {
            Text(draft, fontFamily = FontFamily.Monospace)
        }
        Button(onClick = { shareText(context, AfterHoursPathway.TITLE, draft) }, modifier = Modifier.fillMaxWidth()) {
            Text("Share the note")
        }
        OutlinedButton(onClick = { smsDraft(context, draft) }, modifier = Modifier.fillMaxWidth()) {
            Text("Send as SMS")
        }
        OutlinedButton(
            onClick = { onPrepare(AfterHoursPathway.TITLE, draft) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Prepare to share later") }
        TextButton(onClick = {
            name = ""
            callback = ""
            situation = ""
            includeLocation = false
        }) { Text("Clear the note") }
    }
}
