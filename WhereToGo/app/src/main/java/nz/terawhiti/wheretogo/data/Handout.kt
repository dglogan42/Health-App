package nz.terawhiti.wheretogo.data

data class HandoutQuestion(val id: String, val section: String, val prompt: String)

data class Skill(val title: String, val body: String)

object Handout {
    const val TITLE = "Treating Myself Well"
    const val SUBTITLE = "A gentle discussion guide for our mental health group"
    const val INTRO =
        "There are no right or wrong answers — just honest reflection. Pick the questions that feel okay today, and skip the rest."
    const val PERSON_HINT = "You can answer on this phone. Every question is optional."
    const val WORKER_HINT = "Read the considerations before you use the questions with someone."
    const val SOFT_LEAD_IN = "Go gently. This is reflection, not a test."
    const val SKILLS_OPENER =
        "If you want something small to try while you wait or settle, here are the short skills from the handout."
    const val SKILLS_LIMIT =
        "These are the short skills named on the where-to-go page. They are not treatment, and they are not a reason to delay a call for help."
    const val KINDNESS_TITLE = "A small kindness, if you want one"
    const val KINDNESS_BODY =
        "These are comforts for an ordinary moment — not treatment, and not a reason to delay a call for help."
    val kindnesses = listOf(
        "Warm drink, two quiet minutes",
        "Slow shoulder stretch",
        "A small snack from the cupboard, only if eating feels okay",
    )
    const val ASSET = "treating-myself-well.pdf"

    val personConsiderations = listOf(
        "This is the group handout, for reflection. It is not a test, a diagnosis, or a message to the clinic.",
        "Skip any question. The handout says to choose the ones that speak to you.",
        "What you type stays on this phone until you clear it. Nothing is uploaded.",
        "If you feel unsafe, or someone else is in danger, leave this page and call 111.",
        "If you want someone to talk to, call or text 1737. The clinic is 09 538 0700 on weekdays from 8am to 5pm. After hours, call 0800 775 222.",
    )

    val workerConsiderations = listOf(
        "Use this as the discussion guide it says it is. Do not treat the answers as an assessment, a risk form, or a note for the clinical file.",
        "Offer the questions. Do not require them. People can answer one, or none.",
        "Do not ask anyone to read an answer aloud.",
        "On a shared work phone, tap Clear answers before the next person. Answers are stored only on the phone that is open.",
        "If the conversation becomes immediate danger, stop the handout and call 111. For urgent mental health help outside clinic hours, call 0800 775 222.",
        "The original two-page scan can be opened for someone who wants paper. The questions below follow that scan.",
        "A faint footer on the scan could not be read reliably, so it is not quoted here.",
    )

    val questions = listOf(
        HandoutQuestion(
            "q1",
            "Reflecting on self-kindness",
            "What does \"treating yourself well\" look like on an average day?",
        ),
        HandoutQuestion(
            "q2",
            "Reflecting on self-kindness",
            "If you began treating yourself like someone you were deeply responsible for loving and protecting, what is the very first daily habit that would have to change?",
        ),
        HandoutQuestion(
            "q3",
            "Reflecting on self-kindness",
            "When you make a mistake, what does your inner voice sound like? Is it a voice you would ever permit someone else to use when speaking to you?",
        ),
        HandoutQuestion(
            "q4",
            "Reflecting on self-kindness",
            "Think of the qualities you value most in a close friend or partner (for example patience, loyalty, forgiveness). How consistently do you offer those exact qualities to yourself?",
        ),
        HandoutQuestion(
            "q5",
            "Reflecting on self-kindness",
            "In what areas of your life do you notice the biggest gap between how you treat others and how you treat yourself? (for example work, body image, emotional slip-ups).",
        ),
        HandoutQuestion(
            "q6",
            "Boundaries and expectations",
            "If an acquaintance consistently made the same demands on your time, energy, and mental health that you make on yourself, how long would you keep them in your life?",
        ),
        HandoutQuestion(
            "q7",
            "Boundaries and expectations",
            "How does setting healthy boundaries with other people influence your ability to respect and take care of your own needs?",
        ),
        HandoutQuestion(
            "q8",
            "Boundaries and expectations",
            "What unrealistic standards or expectations do you place on yourself that you would never expect a loved one to meet?",
        ),
        HandoutQuestion(
            "q9",
            "Boundaries and expectations",
            "Do you find it harder to give kindness to yourself, or to open up and receive genuine care and help from others when they offer it?",
        ),
        HandoutQuestion(
            "q10",
            "Shifting to self-compassion",
            "Think of a recent challenge or failure. If a friend came to you with this exact issue, what would you say to them? How can you say those exact words to yourself right now?",
        ),
        HandoutQuestion(
            "q11",
            "Shifting to self-compassion",
            "What is one practical thing you want others to do for you when you are feeling sad or overwhelmed? How can you proactively provide that comfort to yourself?",
        ),
        HandoutQuestion(
            "next",
            "My small next step",
            "This week, one kind thing I will do for myself is:",
        ),
    )

    val skills = listOf(
        Skill(
            "TIPP",
            "A short way to turn the volume down on distress. Temperature: if cold is safe for you, splash cool water on your face or hold a cool cloth. Skip this if cold is a health problem for you. Intense movement: a brief burst your body already tolerates, such as walking. Paced breathing: make the breath out longer than the breath in. Paired relaxation: tense a muscle group gently, then let it go. Stop if you feel faint, in pain, or unwell.",
        ),
        Skill(
            "Five senses",
            "Name five things you can see, four you can feel, three you can hear, two you can smell, and one you can taste. Go slowly. This is for grounding, not for pushing through danger.",
        ),
        Skill(
            "Breathing",
            "Breathe in for a count of about four and out for a count of about six. A few rounds is enough. Sit down if you feel light-headed.",
        ),
        Skill(
            "Leaves on a stream",
            "The where-to-go page calls this “leaves on a steam”. Notice a thought, picture setting it on a leaf, and let the leaf move on. You do not have to argue with the thought.",
        ),
        Skill(
            "ACCEPTS",
            "Activities you can do now. Contributing a small kindness. Comparisons with a harder time you got through, used gently. Emotions, such as a piece of music that shifts the feeling. Pushing the problem away for a short, named time. Thoughts, such as counting or a puzzle. Sensations, such as a warm drink or a cool cloth.",
        ),
    )
}
