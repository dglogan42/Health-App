package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.ui.components.ScreenTitle

@Composable
fun AboutScreen(padding: PaddingValues, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        ScreenTitle("About")
        Text("This is a prototype for Te Rawhiti, built from the pages and files saved with this project and from the QR code on the “Where to go?” poster. It is not a Health New Zealand product. It does not give medical advice, it does not replace the clinic, and it does not send what you type anywhere.")
        Text("The poster opens ${Directory.SOURCE}. That site says it was made in a student practicum and is for information, not clinical care. This app repeats that limit.")
        Text("Included from those files: Health New Zealand, OpenStreetMap, Firstport and the Disability Helpline, Claybeds, and Starlink as a connection backup. The file named SimplyAble was a second copy of the Claybeds page.")
        Text("The Guide tab follows the scanned handout “Treating Myself Well”. Youthline is shown as 0800 376 633, and Lifeline text as 4357, because those are the numbers the organisations publish. Other numbers follow the where-to-go pages.")
        Text("Locate keeps one GPS fix on this phone after you ask for it, until you delete it. It does not track you in the background and it does not call 111. Share, text, and copy happen only when you tap them.")
        Text("After hours uses ${Directory.afterHours.phones.first().number}, the number Te Rawhiti gives for urgent help outside clinic hours. The saved files do not include a district message inbox. Former district health boards are now part of Health New Zealand. This app does not message them.")
        Text("Relay a message uses the public numbers 111 and 105. 105 is not printed on the clinic poster. The screen does not contact Police.")
        Text("Bot telehandling is off until you turn it on. It only offers fixed steps. It does not diagnose or suggest a dose, and it does not read your handout answers.")
        Text("The connection check asks whether this phone has internet. It does not call a Starlink API. Starlink is only a way to restore internet. It is not a health service.")
        Text("Waiting notes stay on this phone until you share or remove them. A connection does not send them. App backup of these notes is turned off.")
        OutlinedButton(onClick = { openUrl(context, Directory.SOURCE) }, modifier = Modifier.fillMaxWidth()) {
            Text("Open the where-to-go site")
        }
    }
}
