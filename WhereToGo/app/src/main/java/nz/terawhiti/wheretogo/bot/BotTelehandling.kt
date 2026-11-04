package nz.terawhiti.wheretogo.bot

/**
 * On-device signpost. Chips only. No free-text counselling, no doses, no diagnosis.
 * Guide answers are never read here.
 */
data class BotChip(val id: String, val label: String)

object BotTelehandling {
    const val TITLE = "Bot telehandling"
    const val DISCLAIMER =
        "This is a guide to the clinic’s where-to-go list. It is not a counsellor and not a doctor. It will not diagnose, suggest a dose, or decide treatment. It is not a Health New Zealand service."
    const val RED_FLAG =
        "If you or someone else is in danger, call 111 now. If you want someone to talk to, call or text 1737."
    const val ENABLE =
        "Bot telehandling stays off until you turn it on. The replies are fixed on this phone. Nothing is sent unless you prepare a note and share it yourself."

    val chips = listOf(
        BotChip("111", "Call 111"),
        BotChip("1737", "1737"),
        BotChip("clinic", "Clinic"),
        BotChip("after_hours", "After hours"),
        BotChip("find", "Open Find"),
    )

    fun handoff(chipLabel: String, locationSummary: String?): String = buildString {
        appendLine("Where to go — bot handoff (prototype)")
        appendLine("This has not been sent. It is not medical advice.")
        appendLine(RED_FLAG)
        appendLine()
        appendLine("Chosen step: $chipLabel")
        if (!locationSummary.isNullOrBlank()) {
            appendLine()
            appendLine("Location I chose to include:")
            appendLine(locationSummary.trim())
        }
    }
}
