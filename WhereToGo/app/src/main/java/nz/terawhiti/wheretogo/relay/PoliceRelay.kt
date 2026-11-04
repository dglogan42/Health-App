package nz.terawhiti.wheretogo.relay

/**
 * Prototype relay screen. 10-5 is the New Zealand Police radio code for "relay the message".
 * 105 is the public Police non-emergency number. Neither comes from the clinic poster.
 * This does not contact Police and does not file a report.
 */
object PoliceRelay {
    const val TITLE = "Relay a message"
    const val EMERGENCY_NUMBER = "111"
    const val NON_EMERGENCY_NUMBER = "105"
    const val EXPLANATION =
        "In New Zealand Police radio codes, 10-5 means relay or transmit a message. This screen is a prototype. It does not contact Police, it does not file a report, and it is not a Police product."
    const val NON_EMERGENCY =
        "105 is the public Police non-emergency number. The dialler opens so you can call. This app still does not send the note."

    fun draft(note: String, locationSummary: String?): String = buildString {
        appendLine("Where to go — message to relay (prototype)")
        appendLine("This has not been sent to Police or to anyone else.")
        appendLine("If someone is in danger, call $EMERGENCY_NUMBER.")
        appendLine("For Police non-emergency, call $NON_EMERGENCY_NUMBER. This note is not a Police report.")
        appendLine()
        appendLine(if (note.isBlank()) "(nothing written)" else note.trim())
        if (!locationSummary.isNullOrBlank()) {
            appendLine()
            appendLine("Location I chose to include:")
            appendLine(locationSummary.trim())
        }
    }
}
