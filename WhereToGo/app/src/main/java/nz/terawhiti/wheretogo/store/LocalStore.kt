package nz.terawhiti.wheretogo.store

import android.content.Context
import nz.terawhiti.wheretogo.data.Handout
import nz.terawhiti.wheretogo.location.FixSource
import nz.terawhiti.wheretogo.location.StoredFix
import nz.terawhiti.wheretogo.outbox.OutboxCodec
import nz.terawhiti.wheretogo.outbox.OutboxItem

/**
 * On-device preferences. Location is a single saved fix, not a trail.
 * Guide answers are kept apart from settings so clearing them leaves the role alone.
 */
class LocalStore(context: Context) {
    private val appContext = context.applicationContext
    private val settings = appContext.getSharedPreferences("wheretogo_settings", Context.MODE_PRIVATE)
    private val guide = appContext.getSharedPreferences("wheretogo_guide", Context.MODE_PRIVATE)
    private val location = appContext.getSharedPreferences("wheretogo_location", Context.MODE_PRIVATE)
    private val outbox = appContext.getSharedPreferences("wheretogo_outbox", Context.MODE_PRIVATE)
    private val legacy = appContext.getSharedPreferences("wheretogo", Context.MODE_PRIVATE)

    init {
        migrate()
    }

    var supportRole: String
        get() = settings.getString("role", "patient") ?: "patient"
        set(value) {
            settings.edit().putString("role", value).apply()
        }

    var largeText: Boolean
        get() = settings.getBoolean("large", false)
        set(value) {
            settings.edit().putBoolean("large", value).apply()
        }

    var botEnabled: Boolean
        get() = settings.getBoolean("bot", false)
        set(value) {
            settings.edit().putBoolean("bot", value).apply()
        }

    fun guideAnswers(): Map<String, String> =
        Handout.questions.associate { it.id to (guide.getString(it.id, "") ?: "") }

    fun putGuideAnswer(id: String, value: String) {
        guide.edit().putString(id, value).apply()
    }

    fun clearGuide() {
        val editor = guide.edit()
        Handout.questions.forEach { editor.remove(it.id) }
        editor.apply()
    }

    fun readFix(): StoredFix? {
        if (!location.contains("lat")) return null
        val lat = location.getString("lat", null)?.toDoubleOrNull() ?: return null
        val lon = location.getString("lon", null)?.toDoubleOrNull() ?: return null
        val accuracy = location.getString("acc", null)?.toFloatOrNull()
        val source = if (location.getString("source", "") == FixSource.LAST_KNOWN.name) {
            FixSource.LAST_KNOWN
        } else {
            FixSource.CURRENT
        }
        return StoredFix(
            latitude = lat,
            longitude = lon,
            accuracyMetres = accuracy,
            capturedAtEpochMs = location.getLong("time", 0L),
            source = source,
        )
    }

    fun writeFix(fix: StoredFix) {
        val editor = location.edit()
            .putString("lat", fix.latitude.toString())
            .putString("lon", fix.longitude.toString())
            .putLong("time", fix.capturedAtEpochMs)
            .putString("source", fix.source.name)
        if (fix.accuracyMetres == null) editor.remove("acc")
        else editor.putString("acc", fix.accuracyMetres.toString())
        editor.apply()
    }

    fun clearFix() {
        location.edit().clear().apply()
    }

    fun readOutbox(): List<OutboxItem> = OutboxCodec.decode(outbox.getString("items", "") ?: "")

    fun enqueue(title: String, body: String): List<OutboxItem> {
        val current = readOutbox()
        val now = System.currentTimeMillis()
        var id = now
        if (current.any { it.id == id }) id += 1
        val next = OutboxCodec.add(current, title, body, now, id)
        outbox.edit().putString("items", OutboxCodec.encode(next)).apply()
        return next
    }

    fun removeOutbox(id: Long): List<OutboxItem> {
        val next = readOutbox().filterNot { it.id == id }
        outbox.edit().putString("items", OutboxCodec.encode(next)).apply()
        return next
    }

    fun clearOutbox(): List<OutboxItem> {
        outbox.edit().clear().apply()
        return emptyList()
    }

    private fun migrate() {
        if (guide.all.isEmpty()) {
            val editor = guide.edit()
            var moved = false
            for (question in Handout.questions) {
                if (!legacy.contains(question.id)) continue
                editor.putString(question.id, legacy.getString(question.id, "") ?: "")
                moved = true
            }
            if (moved) {
                editor.apply()
                val legacyEdit = legacy.edit()
                Handout.questions.forEach { legacyEdit.remove(it.id) }
                legacyEdit.apply()
            }
        }
        if (!settings.contains("role") && legacy.contains("role")) {
            settings.edit().putString("role", legacy.getString("role", "patient")).apply()
        }
        if (!settings.contains("large") && legacy.contains("large")) {
            settings.edit().putBoolean("large", legacy.getBoolean("large", false)).apply()
        }
    }
}
