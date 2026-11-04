package nz.terawhiti.wheretogo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nz.terawhiti.wheretogo.data.Service
import nz.terawhiti.wheretogo.ui.components.ScreenTitle
import nz.terawhiti.wheretogo.ui.theme.Teal

@Composable
fun ServiceScreen(service: Service, padding: PaddingValues, onBack: () -> Unit) {
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
        Text(service.category, color = Teal)
        ScreenTitle(service.name)
        service.hours?.let { Text(it, fontWeight = FontWeight.Medium) }
        Text(service.summary)
        for (phone in service.phones) {
            Button(onClick = { dial(context, phone) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Call, contentDescription = null)
                Text("  ${phone.label} ${phone.number}")
            }
        }
        for (phone in service.texts) {
            OutlinedButton(onClick = { text(context, phone) }, modifier = Modifier.fillMaxWidth()) {
                Text("Text ${phone.number}")
            }
        }
        for (link in service.links) {
            OutlinedButton(onClick = { openUrl(context, link.url) }, modifier = Modifier.fillMaxWidth()) {
                Text(link.label)
            }
        }
    }
}
