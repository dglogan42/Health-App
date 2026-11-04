package nz.terawhiti.wheretogo.ui

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import nz.terawhiti.wheretogo.connectivity.ConnectionCheck
import nz.terawhiti.wheretogo.connectivity.UplinkReport
import nz.terawhiti.wheretogo.connectivity.interpret
import nz.terawhiti.wheretogo.data.Service
import nz.terawhiti.wheretogo.location.FixAttempt
import nz.terawhiti.wheretogo.location.FixSource
import nz.terawhiti.wheretogo.location.OneShotLocation
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.location.hasLocationPermission
import nz.terawhiti.wheretogo.outbox.OutboxItem
import nz.terawhiti.wheretogo.store.LocalStore
import nz.terawhiti.wheretogo.ui.components.EmergencyStrip
import nz.terawhiti.wheretogo.ui.theme.CardWhite
import nz.terawhiti.wheretogo.ui.theme.Paper
import nz.terawhiti.wheretogo.ui.theme.WhereToGoTheme
import java.util.concurrent.atomic.AtomicBoolean

private enum class Tab(val label: String) { NOW("Now"), FIND("Find"), GUIDE("Guide"), LOCATE("Locate") }

private sealed interface Overlay {
    data object None : Overlay
    data class ServiceDetail(val service: Service) : Overlay
    data object About : Overlay
    data object Telehealth : Overlay
    data object Rural : Overlay
    data object Relay : Overlay
    data object AfterHours : Overlay
    data object Bot : Overlay
}

@Composable
fun WhereToGoApp() {
    val context = LocalContext.current
    val store = remember { LocalStore(context.applicationContext) }
    val locator = remember { OneShotLocation(context.applicationContext) }
    val connection = remember { ConnectionCheck(context.applicationContext) }
    var role by remember {
        mutableStateOf(if (store.supportRole == "worker") SupportRole.WORKER else SupportRole.PATIENT)
    }
    var largeText by remember { mutableStateOf(store.largeText) }
    var botOn by remember { mutableStateOf(store.botEnabled) }
    var tab by remember { mutableStateOf(Tab.NOW) }
    var stack by remember { mutableStateOf(listOf<Overlay>(Overlay.None)) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var answers by remember { mutableStateOf(store.guideAnswers()) }
    var fix by remember { mutableStateOf(store.readFix()) }
    var outbox by remember { mutableStateOf(store.readOutbox()) }
    var locateStatus by remember { mutableStateOf<String?>(null) }
    var locating by remember { mutableStateOf(false) }
    var uplink by remember { mutableStateOf<UplinkReport?>(null) }
    var probeNote by remember { mutableStateOf<String?>(null) }
    var checkingLink by remember { mutableStateOf(false) }
    var pendingFix by remember { mutableStateOf<FixSource?>(null) }
    val alive = remember { AtomicBoolean(true) }
    DisposableEffect(Unit) {
        alive.set(true)
        onDispose { alive.set(false) }
    }

    fun fetch(source: FixSource) {
        locating = true
        locateStatus = if (source == FixSource.CURRENT) {
            "Looking for a fresh GPS fix…"
        } else {
            "Reading the last location stored on this phone…"
        }
        locator.fetch(source) { attempt ->
            if (!alive.get()) return@fetch
            locating = false
            when (attempt) {
                is FixAttempt.Ready -> {
                    store.writeFix(attempt.fix)
                    fix = attempt.fix
                    locateStatus = null
                }
                FixAttempt.NeedsPermission -> {
                    locateStatus = "Location permission is off. A fix already saved on this phone can still be shown."
                }
                is FixAttempt.Failed -> locateStatus = attempt.message
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        val mode = pendingFix
        pendingFix = null
        if (granted.values.any { it } && mode != null) fetch(mode)
        else locateStatus = "Location permission is off. A fix already saved on this phone can still be shown."
    }

    fun requestFix(source: FixSource) {
        if (hasLocationPermission(context)) fetch(source)
        else {
            pendingFix = source
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    fun prepare(title: String, body: String) {
        outbox = store.enqueue(title, body)
        tab = Tab.LOCATE
        stack = listOf(Overlay.None)
    }

    fun open(overlay: Overlay) {
        stack = stack + overlay
    }

    fun pop() {
        if (stack.size > 1) stack = stack.dropLast(1)
    }

    fun goTab(next: Tab) {
        tab = next
        stack = listOf(Overlay.None)
    }

    BackHandler(enabled = stack.size > 1) { pop() }

    WhereToGoTheme(largeText) {
        Scaffold(
            containerColor = Paper,
            topBar = { EmergencyStrip() },
            bottomBar = {
                NavigationBar(containerColor = CardWhite) {
                    Tab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = tab == item && stack.last() is Overlay.None,
                            onClick = { goTab(item) },
                            icon = {
                                Icon(
                                    when (item) {
                                        Tab.NOW -> Icons.Filled.Home
                                        Tab.FIND -> Icons.Filled.Search
                                        Tab.GUIDE -> Icons.Filled.Info
                                        Tab.LOCATE -> Icons.Filled.Place
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = { Text(item.label) },
                        )
                    }
                }
            },
        ) { padding ->
            when (val overlay = stack.last()) {
                Overlay.None -> TabContent(
                    padding = padding,
                    tab = tab,
                    role = role,
                    largeText = largeText,
                    query = query,
                    category = category,
                    answers = answers,
                    fix = fix,
                    outbox = outbox,
                    locateStatus = locateStatus,
                    locating = locating,
                    uplink = uplink,
                    probeNote = probeNote,
                    checkingLink = checkingLink,
                    onRole = { next ->
                        role = next
                        store.supportRole = if (next == SupportRole.WORKER) "worker" else "patient"
                    },
                    onLargeText = { on ->
                        largeText = on
                        store.largeText = on
                    },
                    onQuery = { query = it },
                    onCategory = { category = it },
                    onAnswer = { id, value ->
                        answers = answers + (id to value)
                        store.putGuideAnswer(id, value)
                    },
                    onClearGuide = {
                        store.clearGuide()
                        answers = store.guideAnswers()
                    },
                    onOpen = { open(it) },
                    onGoTab = { goTab(it) },
                    onFresh = { requestFix(FixSource.CURRENT) },
                    onLastKnown = { requestFix(FixSource.LAST_KNOWN) },
                    onDeleteFix = {
                        store.clearFix()
                        fix = null
                        locateStatus = "Saved location deleted from this phone."
                    },
                    onPrepare = { title, body -> prepare(title, body) },
                    onRefreshLink = { uplink = interpret(connection.snapshot()) },
                    onProbe = {
                        checkingLink = true
                        uplink = interpret(connection.snapshot())
                        connection.probe { detail ->
                            if (!alive.get()) return@probe
                            checkingLink = false
                            probeNote = detail
                        }
                    },
                    onRemoveQueued = { id -> outbox = store.removeOutbox(id) },
                    onClearQueued = { outbox = store.clearOutbox() },
                )
                Overlay.About -> AboutScreen(padding) { pop() }
                is Overlay.ServiceDetail -> ServiceScreen(overlay.service, padding) { pop() }
                Overlay.Telehealth -> TelehealthScreen(
                    padding = padding,
                    onBack = { pop() },
                    onOpenAfterHours = { open(Overlay.AfterHours) },
                    onOpenService = { open(Overlay.ServiceDetail(it)) },
                )
                Overlay.Rural -> RuralScreen(
                    padding = padding,
                    fix = fix,
                    onBack = { pop() },
                    onOpenLocate = { goTab(Tab.LOCATE) },
                    onOpenService = { open(Overlay.ServiceDetail(it)) },
                )
                Overlay.Relay -> RelayScreen(
                    padding = padding,
                    fix = fix,
                    onBack = { pop() },
                    onPrepare = { title, body -> prepare(title, body) },
                )
                Overlay.AfterHours -> AfterHoursScreen(
                    padding = padding,
                    fix = fix,
                    onBack = { pop() },
                    onPrepare = { title, body -> prepare(title, body) },
                )
                Overlay.Bot -> BotScreen(
                    padding = padding,
                    enabled = botOn,
                    fix = fix,
                    onEnabled = { on ->
                        botOn = on
                        store.botEnabled = on
                    },
                    onBack = { pop() },
                    onFind = { goTab(Tab.FIND) },
                    onAfterHours = { open(Overlay.AfterHours) },
                    onPrepare = { title, body -> prepare(title, body) },
                )
            }
        }
    }
}

@Composable
private fun TabContent(
    padding: PaddingValues,
    tab: Tab,
    role: SupportRole,
    largeText: Boolean,
    query: String,
    category: String?,
    answers: Map<String, String>,
    fix: StoredFix?,
    outbox: List<OutboxItem>,
    locateStatus: String?,
    locating: Boolean,
    uplink: UplinkReport?,
    probeNote: String?,
    checkingLink: Boolean,
    onRole: (SupportRole) -> Unit,
    onLargeText: (Boolean) -> Unit,
    onQuery: (String) -> Unit,
    onCategory: (String?) -> Unit,
    onAnswer: (String, String) -> Unit,
    onClearGuide: () -> Unit,
    onOpen: (Overlay) -> Unit,
    onGoTab: (Tab) -> Unit,
    onFresh: () -> Unit,
    onLastKnown: () -> Unit,
    onDeleteFix: () -> Unit,
    onPrepare: (String, String) -> Unit,
    onRefreshLink: () -> Unit,
    onProbe: () -> Unit,
    onRemoveQueued: (Long) -> Unit,
    onClearQueued: () -> Unit,
) {
    when (tab) {
        Tab.NOW -> NowScreen(
            padding = padding,
            role = role,
            largeText = largeText,
            waitingNotes = outbox.size,
            onRole = onRole,
            onLargeText = onLargeText,
            onOpenGuide = { onGoTab(Tab.GUIDE) },
            onOpenLocate = { onGoTab(Tab.LOCATE) },
            onOpenAfterHours = { onOpen(Overlay.AfterHours) },
            onOpenTelehealth = { onOpen(Overlay.Telehealth) },
            onOpenRural = { onOpen(Overlay.Rural) },
            onOpenRelay = { onOpen(Overlay.Relay) },
            onOpenBot = { onOpen(Overlay.Bot) },
            onOpenService = { onOpen(Overlay.ServiceDetail(it)) },
            onAbout = { onOpen(Overlay.About) },
        )
        Tab.FIND -> FindScreen(
            padding = padding,
            query = query,
            category = category,
            onQuery = onQuery,
            onCategory = onCategory,
            onOpen = { onOpen(Overlay.ServiceDetail(it)) },
        )
        Tab.GUIDE -> GuideScreen(
            padding = padding,
            worker = role == SupportRole.WORKER,
            answers = answers,
            onAnswer = onAnswer,
            onClear = onClearGuide,
        )
        Tab.LOCATE -> LocateScreen(
            padding = padding,
            fix = fix,
            status = locateStatus,
            busy = locating,
            uplink = uplink,
            probeNote = probeNote,
            checkingLink = checkingLink,
            outbox = outbox,
            onFresh = onFresh,
            onLastKnown = onLastKnown,
            onDeleteFix = onDeleteFix,
            onPrepare = onPrepare,
            onRefreshLink = onRefreshLink,
            onProbe = onProbe,
            onRemoveQueued = onRemoveQueued,
            onClearQueued = onClearQueued,
        )
    }
}
