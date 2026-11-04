package nz.terawhiti.wheretogo.data

import nz.terawhiti.wheretogo.afterhours.AfterHoursPathway
import nz.terawhiti.wheretogo.bot.BotTelehandling
import nz.terawhiti.wheretogo.connectivity.NetworkSnapshot
import nz.terawhiti.wheretogo.connectivity.NoStarlinkUplink
import nz.terawhiti.wheretogo.connectivity.StarlinkApi
import nz.terawhiti.wheretogo.connectivity.TransportKind
import nz.terawhiti.wheretogo.connectivity.interpret
import nz.terawhiti.wheretogo.location.FixSource
import nz.terawhiti.wheretogo.location.Geo
import nz.terawhiti.wheretogo.location.LocationSummary
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.outbox.OutboxCodec
import nz.terawhiti.wheretogo.relay.PoliceRelay
import nz.terawhiti.wheretogo.rural.RuralPathways
import nz.terawhiti.wheretogo.telehealth.TelehealthCopy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId

class FeaturesTest {
    @Test
    fun guideSoftCopyStaysOffTheSafetyBullets() {
        assertTrue(Handout.INTRO.contains("no right or wrong answers"))
        assertTrue(Handout.INTRO.contains("skip the rest"))
        assertEquals("Go gently. This is reflection, not a test.", Handout.SOFT_LEAD_IN)
        assertEquals(3, Handout.kindnesses.size)
        assertTrue(Handout.KINDNESS_BODY.contains("not treatment"))
        assertTrue(Handout.workerConsiderations.any { it.contains("111") })
        assertTrue(Handout.workerConsiderations.none { it.contains("Go gently") })
        assertTrue(Handout.SKILLS_LIMIT.contains("not a reason to delay a call for help"))
    }

    @Test
    fun knownClinicNumbersStayPut() {
        assertEquals("111", Directory.emergency.phones.first().number)
        assertEquals("095380700", digits(Directory.clinic.phones.first().number))
        assertEquals("0800775222", digits(Directory.afterHours.phones.first().number))
        assertEquals("1737", digits(Directory.needToTalk.phones.first().number))
        assertEquals("0800611116", digits(Directory.healthline.phones.first().number))
    }

    @Test
    fun policeNonEmergencyIsNotAddedToTheClinicList() {
        val numbers = Directory.services.flatMap { it.phones + it.texts }.map { digits(it.number) }
        assertTrue(numbers.none { it == "105" })
    }

    @Test
    fun locatorSummaryIsAHandoffNotACall() {
        val southOfClinic = StoredFix(
            latitude = Directory.LAT - 0.01,
            longitude = Directory.LON,
            accuracyMetres = 12f,
            capturedAtEpochMs = 1_700_000_000_000L,
            source = FixSource.CURRENT,
        )
        val text = LocationSummary.plainText(southOfClinic, ZoneId.of("Pacific/Auckland"))
        assertTrue(text.contains("111"))
        assertTrue(text.contains("Not sent"))
        assertTrue(text.contains("-36.90973"))
        assertTrue(text.contains("north"))
        assertFalse(text.contains("Treating Myself Well"))
        val metres = Geo.distanceMetres(southOfClinic.latitude, southOfClinic.longitude, Directory.LAT, Directory.LON)
        assertTrue(metres in 1_000.0..1_300.0)
    }

    @Test
    fun uplinkDoesNotPretendToReachStarlink() {
        assertFalse(NoStarlinkUplink.integrated)
        val offline = interpret(NetworkSnapshot(false, false, false, emptySet()))
        assertFalse(offline.internetLikely)
        assertEquals(StarlinkApi.NOT_INTEGRATED, offline.starlinkApi)
        assertTrue(offline.summary.contains("Calls still work"))
        assertTrue(offline.summary.contains("not a health service"))
        val wifi = interpret(
            NetworkSnapshot(true, true, true, setOf(TransportKind.WIFI)),
        )
        assertTrue(wifi.internetLikely)
        assertTrue(wifi.summary.contains("cannot tell"))
        assertEquals(StarlinkApi.NOT_INTEGRATED, wifi.starlinkApi)
    }

    @Test
    fun relayAndAfterHoursDoNotInventAChannel() {
        val relay = PoliceRelay.draft("Please pass this on", null)
        assertTrue(relay.contains("111"))
        assertTrue(relay.contains("105"))
        assertTrue(relay.contains("has not been sent"))
        assertFalse(AfterHoursPathway.MISSING_CHANNEL.contains("0800"))
        assertTrue(AfterHoursPathway.DANGER.contains("111"))
        val note = AfterHoursPathway.draft(
            name = "",
            callback = "",
            situation = "Need a call back",
            locationSummary = null,
            clinicPhone = Directory.clinic.phones.first().number,
            clinicHours = "Monday to Friday, 8am to 5pm",
            afterHoursPhone = Directory.afterHours.phones.first().number,
            talkPhone = "1737",
        )
        assertTrue(note.contains("0800 775 222"))
        assertTrue(note.contains("has not been sent"))
        assertTrue(note.contains("111"))
    }

    @Test
    fun ruralPlaceholdersUseExistingLinesOnly() {
        val lines = RuralPathways.phoneFirst()
        assertEquals(RuralPathways.phoneFirstNames, lines.map { it.name })
        val gapText = RuralPathways.gaps.joinToString(" ") { it.title + " " + it.body }
        assertFalse(gapText.contains("0800"))
        assertTrue(gapText.contains("Not in the clinic materials"))
        assertTrue(TelehealthCopy.BOUNDARY.contains("does not run the appointment"))
        assertFalse(TelehealthCopy.VIDEO_GAP.contains("0800"))
    }

    @Test
    fun botIsChipsOnlyAndNamesTheRedFlag() {
        assertEquals(
            listOf("Call 111", "1737", "Clinic", "After hours", "Open Find"),
            BotTelehandling.chips.map { it.label },
        )
        assertTrue(BotTelehandling.RED_FLAG.contains("111"))
        assertTrue(BotTelehandling.RED_FLAG.contains("1737"))
        assertTrue(BotTelehandling.DISCLAIMER.contains("not a doctor"))
        assertTrue(BotTelehandling.DISCLAIMER.contains("dose"))
        val handoff = BotTelehandling.handoff("Call 111", null)
        assertFalse(handoff.contains("Treating Myself Well"))
        assertTrue(handoff.contains("has not been sent"))
    }

    @Test
    fun outboxRoundTripKeepsTheNoteOnDevice() {
        val stored = OutboxCodec.add(emptyList(), "After hours", "Line one\nTe Rawhiti — note", 10L, 10L)
        val again = OutboxCodec.decode(OutboxCodec.encode(stored))
        assertEquals(stored, again)
        val trimmed = (1..25).fold(emptyList<nz.terawhiti.wheretogo.outbox.OutboxItem>()) { acc, n ->
            OutboxCodec.add(acc, "t$n", "body", n.toLong(), n.toLong(), limit = 20)
        }
        assertEquals(20, trimmed.size)
        assertEquals(25L, trimmed.first().id)
    }

    private fun digits(number: String) = number.filter { it.isDigit() }
}
