package nz.terawhiti.wheretogo.afterhours

/**
 * After-hours contact uses the number already on Te Rawhiti’s list.
 * No district inbox is invented.
 */
object AfterHoursPathway {
    const val TITLE = "After hours"
    const val DANGER = "If you or someone else is in danger, call 111."
    const val MISSING_CHANNEL =
        "Not in the clinic materials: a text line, email, or secure inbox for a district mental health service. Former district health boards are now part of Health New Zealand (Te Whatu Ora). This app is not a Health New Zealand product. It does not deliver a message to them. Use the after-hours phone already on Te Rawhiti’s list, or call 111."

    fun draft(
        name: String,
        callback: String,
        situation: String,
        locationSummary: String?,
        clinicPhone: String,
        clinicHours: String,
        afterHoursPhone: String,
        talkPhone: String,
    ): String = buildString {
        appendLine("Where to go — after-hours note (prototype)")
        appendLine("This has not been sent to Health New Zealand, a clinic, or anyone else.")
        appendLine(DANGER)
        appendLine("Former district health boards are now part of Health New Zealand. This app is not a Health New Zealand product.")
        appendLine()
        appendLine("Clinic $clinicPhone ($clinicHours)")
        appendLine("After hours $afterHoursPhone — the number Te Rawhiti gives for urgent help outside clinic hours.")
        appendLine("You can also call or text $talkPhone.")
        appendLine()
        if (name.isNotBlank()) appendLine("Name: ${name.trim()}")
        if (callback.isNotBlank()) appendLine("Callback: ${callback.trim()}")
        appendLine("What is happening:")
        appendLine(if (situation.isBlank()) "(nothing written)" else situation.trim())
        if (!locationSummary.isNullOrBlank()) {
            appendLine()
            appendLine("Location I chose to include:")
            appendLine(locationSummary.trim())
        }
    }
}
