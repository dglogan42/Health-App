package nz.terawhiti.wheretogo.rural

import nz.terawhiti.wheretogo.data.Directory
import nz.terawhiti.wheretogo.data.Service

data class PrototypeGap(val title: String, val body: String)

object RuralPathways {
    const val TITLE = "Rural and low signal"
    const val INTRO =
        "The saved poster and pages do not have a separate rural list. These phone lines are already in the where-to-go list. Hours can change — call before you travel if you can. If someone is in danger, call 111."
    const val OFFLINE = "Saved on this phone. These numbers still dial without internet."

    val phoneFirstNames = listOf(
        "Emergency services",
        "Need to talk? 1737",
        "After-hours mental health",
        "Te Rawhiti",
        "Healthline",
        "Lifeline",
        "Samaritans",
        "Depression Helpline",
        "Youthline",
        "Firstport",
    )

    val gaps = listOf(
        PrototypeGap(
            "Not in the clinic materials — local rural clinics",
            "No rural hospital, PRIME service, or after-hours GP is named in the saved files. This prototype does not add a number. Call your own GP, use the lines above, or call 111 if someone is in danger.",
        ),
        PrototypeGap(
            "Not in the clinic materials — rural video",
            "No rural video clinic is named in the saved files. This app does not start a video call.",
        ),
    )

    fun phoneFirst(services: List<Service> = Directory.services): List<Service> =
        phoneFirstNames.map { name -> services.first { it.name == name } }
}
