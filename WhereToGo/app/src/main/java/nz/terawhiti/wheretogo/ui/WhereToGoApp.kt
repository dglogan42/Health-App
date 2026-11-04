package nz.terawhiti.wheretogo.ui

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Handout
import nz.terawhiti.wheretogo.data.Service

private val Ink = Color(0xFF1A2423)
private val Teal = Color(0xFF0E4F4C)
private val Paper = Color(0xFFF6F1E8)
private val CardWhite = Color(0xFFFFFCF7)
private val Emergency = Color(0xFF8E1B2C)
private val EmergencySoft = Color(0xFFF8E8EB)

private enum class Tab(val label: String) { NOW("Now"), FIND("Find"), GUIDE("Guide"), PLACE("Place") }
private enum class Role { PATIENT, WORKER }

@Composable
fun WhereToGoApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("wheretogo", Context.MODE_PRIVATE) }
    var role by remember {
        mutableStateOf(if (prefs.getString("role", "patient") == "worker") Role.WORKER else Role.PATIENT)
    }
    var largeText by remember { mutableStateOf(prefs.getBoolean("large", false)) }
    var tab by remember { mutableStateOf(Tab.NOW) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<Service?>(null) }
    var showAbout by remember { mutableStateOf(false) }

    BackHandler(enabled = selected != null || showAbout) {
        selected = null
        showAbout = false
    }

    val density = LocalDensity.current
    val scaled = if (largeText) Density(density.density, density.fontScale * 1.2f) else density

    CompositionLocalProvider(LocalDensity provides scaled) {
        MaterialTheme(colorScheme = lightColorScheme(
            primary = Teal,
            onPrimary = Color.White,
            background = Paper,
            surface = CardWhite,
            onBackground = Ink,
            onSurface = Ink,
            error = Emergency,
        )) {
            Scaffold(
                containerColor = Paper,
                topBar = { EmergencyStrip() },
                bottomBar = {
                    NavigationBar(containerColor = CardWhite) {
                        Tab.entries.forEach { item ->
                            NavigationBarItem(
                                selected = tab == item && selected == null && !showAbout,
                                onClick = {
                                    selected = null
                                    showAbout = false
                                    tab = item
                                },
                                icon = {
                                    androidx.compose.material3.Icon(
                                        when (item) {
                                            Tab.NOW -> Icons.Filled.Home
                                            Tab.FIND -> Icons.Filled.Search
                                            Tab.GUIDE -> Icons.Filled.Info
                                            Tab.PLACE -> Icons.Filled.Place
                                        },
                                        contentDescription = item.label,
                                    )
                                },
                                label = { Text(item.label) },
                            )
                        }
                    }
                },
            ) { padding ->
                when {
                    showAbout -> AboutScreen(padding) { showAbout = false }
                    selected != null -> ServiceScreen(selected!!, padding) { selected = null }
                    tab == Tab.NOW -> NowScreen(
                        padding = padding,
                        role = role,
                        largeText = largeText,
                        onRole = { next ->
                            role = next
                            prefs.edit().putString("role", if (next == Role.WORKER) "worker" else "patient").apply()
                        },
                        onLargeText = { on ->
                            largeText = on
                            prefs.edit().putBoolean("large", on).apply()
                        },
                        onOpenGuide = { tab = Tab.GUIDE },
                        onOpenService = { selected = it },
                        onAbout = { showAbout = true },
                    )
                    tab == Tab.FIND -> FindScreen(
                        padding, query, category,
                        onQuery = { query = it },
                        onCategory = { category = it },
                        onOpen = { selected = it },
                    )
                    tab == Tab.GUIDE -> GuideScreen(padding, role == Role.WORKER, prefs)
                    tab == Tab.PLACE -> PlaceScreen(padding)
                }
            }
        }
    }
}

@Composable
private fun EmergencyStrip() {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text("If someone is in danger", color = Emergency, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmergencyButton("Call 111", Modifier.weight(1f)) { dial(context, Directory.services.first().phones.first()) }
            EmergencyButton("Clinic", Modifier.weight(1f)) {
                dial(context, Directory.services.first { it.name == "Te Rawhiti" }.phones.first())
            }
            EmergencyButton("After hours", Modifier.weight(1f)) {
                dial(context, Directory.services.first { it.name == "After-hours mental health" }.phones.first())
            }
        }
    }
}

@Composable
private fun EmergencyButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Emergency, contentColor = Color.White),
    ) { Text(label) }
}

@Composable
private fun NowScreen(
    padding: PaddingValues,
    role: Role,
    largeText: Boolean,
    onRole: (Role) -> Unit,
    onLargeText: (Boolean) -> Unit,
    onOpenGuide: () -> Unit,
    onOpenService: (Service) -> Unit,
    onAbout: () -> Unit,
) {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize().padding(padding),
    ) {
        item {
            Text("Te Rawhiti", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Where to go", style = MaterialTheme.typography.titleLarge, color = Teal)
            Text(
                "A phone copy of the clinic’s where-to-go list, for people using the service and for the people supporting them. It works without internet.",
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = role == Role.PATIENT, onClick = { onRole(Role.PATIENT) }, label = { Text("I need support") })
                FilterChip(selected = role == Role.WORKER, onClick = { onRole(Role.WORKER) }, label = { Text("I am supporting") })
            }
            TextButton(onClick = { onLargeText(!largeText) }) {
                Text(if (largeText) "Use smaller text" else "Use larger text")
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(Handout.TITLE, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(Handout.SUBTITLE)
                    Text(Handout.INTRO)
                    Text(
                        if (role == Role.WORKER) {
                            "Read the handout considerations before you use the questions with someone."
                        } else {
                            "You can answer on this phone. You can also skip every question."
                        },
                    )
                    Button(onClick = onOpenGuide) { Text("Open the handout") }
                }
            }
        }
        if (role == Role.WORKER) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("On shift", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${Directory.CLINIC}. ${Directory.ADDRESS}.")
                        Text("Weekdays 8am–5pm: 09 538 0700. Other times, for urgent mental health help: 0800 775 222.")
                        Text("People may be referred by a GP, come from crisis services, or walk in.")
                        Text("Disability support that is not an emergency: Disability Helpline 0800 11 12 13, or text 8988.")
                        Button(onClick = { openHandout(context) }) { Text("Open the original PDF") }
                    }
                }
            }
        }
        item {
            Text("Talk to someone", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(Directory.services.filter { it.urgent }, key = { it.name }) { service ->
            ServiceCard(service) { onOpenService(service) }
        }
        item {
            TextButton(onClick = onAbout) { Text("About this app and the saved pages") }
        }
    }
}

@Composable
private fun FindScreen(
    padding: PaddingValues,
    query: String,
    category: String?,
    onQuery: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onOpen: (Service) -> Unit,
) {
    val results = Directory.search(query, category)
    Column(Modifier.fillMaxSize().padding(padding)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("Search names, places, or numbers") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(selected = category == null, onClick = { onCategory(null) }, label = { Text("All") })
            }
            items(Directory.categories) { name ->
                FilterChip(selected = category == name, onClick = { onCategory(if (category == name) null else name) }, label = { Text(name) })
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(results, key = { it.name }) { service ->
                ServiceCard(service) { onOpen(service) }
            }
        }
    }
}

@Composable
private fun ServiceCard(service: Service, onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(service.category, color = Teal, style = MaterialTheme.typography.labelLarge)
            Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            service.hours?.let { Text(it) }
            Text(service.summary, maxLines = 3)
        }
    }
}

@Composable
private fun ServiceScreen(service: Service, padding: PaddingValues, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        Text(service.category, color = Teal)
        Text(service.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        service.hours?.let { Text(it, fontWeight = FontWeight.Medium) }
        Text(service.summary)
        for (phone in service.phones) {
            Button(onClick = { dial(context, phone) }, modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Icon(Icons.Filled.Call, contentDescription = null)
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

@Composable
private fun GuideScreen(padding: PaddingValues, worker: Boolean, prefs: android.content.SharedPreferences) {
    val context = LocalContext.current
    var answers by remember {
        mutableStateOf(Handout.questions.associate { it.id to (prefs.getString(it.id, "") ?: "") })
    }
    Column(
        Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(Handout.TITLE, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(Handout.SUBTITLE, color = Teal)
        Text(Handout.INTRO)
        ConsiderationCard(
            title = if (worker) "Before you use this with someone" else "Before you write",
            points = if (worker) Handout.workerConsiderations else Handout.personConsiderations,
        )
        Button(onClick = { openHandout(context) }, modifier = Modifier.fillMaxWidth()) {
            Text("Open the original two-page PDF")
        }
        Text(
            "The scan is the group handout saved with this project. Typing below is optional and stays on this phone.",
            style = MaterialTheme.typography.bodyMedium,
        )
        var lastSection = ""
        for (question in Handout.questions) {
            if (question.section != lastSection) {
                lastSection = question.section
                Text(question.section, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            OutlinedTextField(
                value = answers[question.id] ?: "",
                onValueChange = { value ->
                    answers = answers + (question.id to value)
                    prefs.edit().putString(question.id, value).apply()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(question.prompt) },
                minLines = 3,
            )
        }
        OutlinedButton(
            onClick = {
                val editor = prefs.edit()
                Handout.questions.forEach { editor.remove(it.id) }
                editor.apply()
                answers = Handout.questions.associate { it.id to "" }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Clear answers on this phone") }
        HorizontalDivider()
        Text("If you need something to do right now", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("These are the short skills named on the where-to-go page. They are not treatment, and they are not a reason to delay a call for help.")
        for (skill in Handout.skills) {
            Card(colors = CardDefaults.cardColors(containerColor = CardWhite), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(skill.title, fontWeight = FontWeight.Bold)
                    Text(skill.body)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ConsiderationCard(title: String, points: List<String>) {
    Card(colors = CardDefaults.cardColors(containerColor = EmergencySoft), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Emergency)
            for (point in points) Text("• $point")
        }
    }
}

@Composable
private fun PlaceScreen(padding: PaddingValues) {
    val context = LocalContext.current
    val online = remember { isOnline(context) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        if (granted.values.any { it }) shareCurrentLocation(context)
    }
    Column(
        Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Clinic", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(Directory.CLINIC, fontWeight = FontWeight.Bold)
        Text(Directory.ADDRESS)
        Text("Monday to Friday, 8am to 5pm.")
        if (!online) {
            Text("No internet right now. Calls still work. The map and Starlink page need a connection. The directory and handout are already on this phone.")
        }
        Button(onClick = { clinicMap(context) }, modifier = Modifier.fillMaxWidth()) { Text("Open in OpenStreetMap") }
        OutlinedButton(onClick = { clinicDirections(context) }, modifier = Modifier.fillMaxWidth()) { Text("Directions") }
        OutlinedButton(onClick = {
            dial(context, Directory.services.first { it.name == "Te Rawhiti" }.phones.first())
        }, modifier = Modifier.fillMaxWidth()) { Text("Call the clinic") }
        Button(onClick = {
            if (hasLocationPermission(context)) shareCurrentLocation(context)
            else permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ))
        }, modifier = Modifier.fillMaxWidth()) { Text("Share where I am") }
        Text("The share button asks this phone for its location and lets you send an OpenStreetMap link. It does not store the location in the app. Map data © OpenStreetMap contributors.")
        Text("Starlink, from the saved page, is only a way to restore internet. It is not a health service.", modifier = Modifier.padding(top = 8.dp))
        OutlinedButton(onClick = { openUrl(context, "https://www.starlink.com/") }, modifier = Modifier.fillMaxWidth()) {
            Text("Starlink")
        }
    }
}

@Composable
private fun AboutScreen(padding: PaddingValues, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        Text("About", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("This is a prototype for Te Rawhiti, built from the pages and files saved in the Health App folder and from the QR code on the “Where to go?” poster.")
        Text("The poster opens ${Directory.SOURCE}. That site says it was made in a student practicum and is for information, not clinical care. This app repeats that limit.")
        Text("Included from those files: Health New Zealand, OpenStreetMap, Firstport and the Disability Helpline, Claybeds, and Starlink as a connection backup. The file named SimplyAble was a second copy of the Claybeds page.")
        Text("The Guide tab follows the scanned handout “Treating Myself Well”. Youthline is shown as 0800 376 633, and Lifeline text as 4357, because those are the numbers the organisations publish. Other numbers follow the where-to-go pages.")
        Text("This app does not give medical advice, does not replace the clinic, and does not send what you type anywhere.")
        val context = LocalContext.current
        OutlinedButton(onClick = { openUrl(context, Directory.SOURCE) }, modifier = Modifier.fillMaxWidth()) {
            Text("Open the where-to-go site")
        }
    }
}

private fun isOnline(context: Context): Boolean {
    val manager = context.getSystemService(ConnectivityManager::class.java) ?: return true
    val network = manager.activeNetwork ?: return false
    val caps = manager.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
