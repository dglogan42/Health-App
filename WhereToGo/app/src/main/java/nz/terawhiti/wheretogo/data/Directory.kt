package nz.terawhiti.wheretogo.data

data class Phone(val label: String, val number: String)

data class Link(val label: String, val url: String)

data class Service(
    val name: String,
    val category: String,
    val summary: String,
    val phones: List<Phone> = emptyList(),
    val texts: List<Phone> = emptyList(),
    val links: List<Link> = emptyList(),
    val hours: String? = null,
    val urgent: Boolean = false,
)

object Directory {
    const val CLINIC = "Te Rawhiti Community Mental Health Centre"
    const val ADDRESS = "15 Aberfeldy Avenue, Highland Park, Auckland 2143"
    const val LAT = -36.89973
    const val LON = 174.90884
    const val SOURCE = "https://sites.google.com/view/terawhiti-wheretogo"

    const val TALK = "Talk now"
    const val CLINIC_CAT = "Te Rawhiti"
    const val APPS = "On your phone"
    const val CARERS = "Whānau and carers"
    const val HARM = "Family harm"
    const val RAINBOW = "Rainbow"
    const val CULTURE = "Cultural"
    const val AOD = "Alcohol and other drugs"
    const val NEURO = "Neurodiverse"
    const val BODY = "Physical health"
    const val PRACTICAL = "Practical support"
    const val LINK = "Connection"

    val categories = listOf(
        TALK, CLINIC_CAT, APPS, CARERS, HARM, RAINBOW, CULTURE, AOD, NEURO, BODY, PRACTICAL, LINK,
    )

    val services: List<Service> = listOf(
        Service(
            name = "Emergency services",
            category = TALK,
            summary = "Police, fire, or ambulance. Use this when someone is in immediate danger, seriously hurt, or needs urgent medical help such as trouble breathing, chest pain, unconsciousness, or severe bleeding.",
            phones = listOf(Phone("Call", "111")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Need to talk? 1737",
            category = TALK,
            summary = "Call or text 1737 any time to speak with a counsellor or peer support worker.",
            phones = listOf(Phone("Call", "1737")),
            texts = listOf(Phone("Text", "1737")),
            links = listOf(Link("Website", "https://1737.org.nz")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Te Rawhiti",
            category = CLINIC_CAT,
            summary = "Free mental health support for the eastern Auckland area. People arrive by GP referral, from crisis services, or by walking in. The team provides medical, clinical, and social support.",
            phones = listOf(Phone("Clinic", "09 538 0700")),
            hours = "Monday to Friday, 8am to 5pm",
            urgent = true,
        ),
        Service(
            name = "After-hours mental health",
            category = CLINIC_CAT,
            summary = "The number Te Rawhiti gives for urgent help outside clinic hours.",
            phones = listOf(Phone("After hours", "0800 775 222")),
            hours = "Outside clinic hours",
            urgent = true,
        ),
        Service(
            name = "Youthline",
            category = TALK,
            summary = "Free crisis support and counselling for young people, and for people supporting a young person. Webchat is also on the website. The where-to-go page shortened the phone number. This app uses Youthline’s published number, 0800 376 633.",
            phones = listOf(
                Phone("Call", "0800 376 633"),
                Phone("WhatsApp", "09 886 5696"),
            ),
            texts = listOf(Phone("Text", "234")),
            links = listOf(Link("Website", "https://youthline.co.nz")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Lifeline",
            category = TALK,
            summary = "Confidential counselling by phone or text when you are distressed or thinking about suicide. Text 4357 spells HELP. The where-to-go page printed 4537.",
            phones = listOf(Phone("Call", "0800 543 354")),
            texts = listOf(Phone("Text HELP", "4357")),
            links = listOf(Link("Website", "https://www.lifeline.org.nz")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Suicide crisis line",
            category = TALK,
            summary = "0508 TAUTOKO. For people thinking about suicide, and for people worried about someone else.",
            phones = listOf(Phone("Call", "0508 828 865")),
            links = listOf(Link("Lifeline", "https://www.lifeline.org.nz")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Samaritans",
            category = TALK,
            summary = "A free, confidential listening line run by trained volunteers. For loneliness, distress, or thoughts of suicide.",
            phones = listOf(Phone("Call", "0800 726 666")),
            links = listOf(Link("Website", "https://www.samaritans.org.nz")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Depression Helpline",
            category = TALK,
            summary = "Talk to a counsellor about depression or anxiety, or ask what to do next.",
            phones = listOf(Phone("Call", "0800 111 757")),
            texts = listOf(Phone("Text", "4202")),
            links = listOf(Link("Website", "https://www.depression.org.nz/get-help")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Anxiety NZ",
            category = TALK,
            summary = "Helpline for stress, panic, phobias, or OCD, plus therapy and peer groups.",
            phones = listOf(Phone("Call", "0800 269 4389")),
            links = listOf(Link("Website", "https://anxiety.org.nz")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Healthline",
            category = TALK,
            summary = "Free advice from nurses and paramedics about symptoms and where to get healthcare. Run with Health New Zealand.",
            phones = listOf(Phone("Call", "0800 611 116")),
            links = listOf(Link("Healthline", "https://www.healthnz.govt.nz/online-phone-healthcare/healthline")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Outline",
            category = RAINBOW,
            summary = "Rainbow mental health support for takatāpui, MVPFAFF+, rainbow communities, whānau, and people questioning. Phone and online chat.",
            phones = listOf(Phone("Call", "0800 688 5463")),
            links = listOf(Link("Website", "https://outline.org.nz/")),
            hours = "Every evening, 6pm to 9pm",
            urgent = true,
        ),
        Service(
            name = "Groups at Te Rawhiti",
            category = CLINIC_CAT,
            summary = "Groups run through the year on referral. Past topics include dealing with distress, memory and planning after psychosis, nutrition and movement, acceptance and commitment therapy, and wellness recovery action plans. Some groups have been in Mandarin. Ask your clinician what is running.",
        ),
        Service(
            name = "Manawa",
            category = APPS,
            summary = "A survival-planning app named by Te Rawhiti. It helps you make a safety plan, talk to someone, and find ways to cope right now. Search your app store for Manawa.",
            links = listOf(
                Link(
                    "Search Google Play",
                    "https://play.google.com/store/search?q=Manawa%20safety%20plan&c=apps",
                ),
            ),
        ),
        Service(
            name = "Workwise",
            category = CLINIC_CAT,
            summary = "Employment support for people facing personal or health challenges. Consultants help people choose, get, and keep a job.",
            links = listOf(Link("Website", "https://www.workwise.org.nz/")),
        ),
        Service(
            name = "Pathways",
            category = CLINIC_CAT,
            summary = "Community mental health, alcohol, and other drug support in the Wise Group. Support to live a full life connected to what matters.",
            links = listOf(Link("Website", "https://www.pathways.co.nz/about-us")),
        ),
        Service(
            name = "Solace Support Auckland",
            category = CLINIC_CAT,
            summary = "Monthly peer meetings for people bereaved by suicide. Facilitators have lived experience of that loss.",
            links = listOf(
                Link("Healthpoint", "https://www.healthpoint.co.nz/mental-health-addictions/mental-health/solace-support-auckland/"),
                Link("Facebook", "https://www.facebook.com/solaceauckland/"),
            ),
        ),
        Service(
            name = "Finch",
            category = APPS,
            summary = "A self-care app that uses a virtual pet to support small healthy habits. Listed on the where-to-go page.",
            links = listOf(
                Link("Android", "https://play.google.com/store/apps/details?id=com.finch.finch&hl=en_NZ"),
                Link("Apple", "https://apps.apple.com/us/app/finch-self-care-pet/id1528595748"),
            ),
        ),
        Service(
            name = "Smiling Mind",
            category = APPS,
            summary = "A mental health toolkit for different ages, with mindfulness and meditation.",
            links = listOf(
                Link("Android", "https://play.google.com/store/apps/details?id=com.smilingmind.app&hl=en_NZ"),
                Link("Apple", "https://apps.apple.com/nz/app/smiling-mind-mental-wellbeing/id560442518"),
            ),
        ),
        Service(
            name = "Voda",
            category = APPS,
            summary = "A self-care app made for LGBTQIA+ people.",
            links = listOf(
                Link("Android", "https://play.google.com/store/apps/details?id=co.voda.android&hl=en_NZ"),
                Link("Apple", "https://apps.apple.com/us/app/voda-self-care-for-lgbtqia/id1609960860"),
            ),
        ),
        Service(
            name = "Kalda",
            category = APPS,
            summary = "A self-guided mental wellbeing app for LGBTQIA+ people.",
            links = listOf(
                Link("Android", "https://play.google.com/store/apps/details?id=com.flutterflow.kalda&hl=en_NZ"),
                Link("Apple", "https://apps.apple.com/us/app/kalda-lgbtqia-mental-health/id1616677264"),
            ),
        ),
        Service(
            name = "Mana Restore",
            category = APPS,
            summary = "Wellbeing information for the gaming community, written with Pacific and Māori young people and their whānau in mind. Topics include healthy gaming, sleep, depression, anxiety, and time away from screens.",
            links = listOf(Link("Website", "https://www.mentalwealth.nz/manarestore/")),
        ),
        Service(
            name = "Just a Thought and Just a Pause",
            category = APPS,
            summary = "Free online cognitive behavioural courses for anxiety, depression, health anxiety, insomnia, OCD, panic, social anxiety, and stress. A New Zealand Sign Language option is available. Just a Pause is the short mindfulness course on the same site.",
            links = listOf(
                Link("Courses", "https://www.justathought.co.nz/"),
                Link("Mindfulness course", "https://www.justathought.co.nz/courses/mindfulness"),
            ),
        ),
        Service(
            name = "Next Steps",
            category = APPS,
            summary = "A digital finder for mental health and addiction services in Aotearoa.",
            links = listOf(Link("Website", "https://www.nextsteps.org.nz/")),
        ),
        Service(
            name = "Yellow Brick Road",
            category = CARERS,
            summary = "Free, confidential, independent support, education, and advocacy for families and whānau who have a loved one with mental health challenges. Formerly Supporting Families.",
            links = listOf(Link("Website", "https://yellowbrickroad.org.nz/")),
        ),
        Service(
            name = "LifeKeepers",
            category = CARERS,
            summary = "Free suicide-prevention training for people 18 and over, including whānau and front-line workers. It covers recognising distress, asking about suicide, and connecting someone to professional support.",
            links = listOf(Link("Website", "https://www.lifekeepers.nz/")),
        ),
        Service(
            name = "Women's Refuge",
            category = HARM,
            summary = "Support for women and children experiencing family violence: safe housing, visits, advocacy, and referrals. If you are in immediate danger, call 111 first.",
            phones = listOf(Phone("Crisis line", "0800 733 843")),
            links = listOf(Link("Find a refuge", "https://womensrefuge.org.nz/contact-us/find-your-local-refuge/")),
            hours = "24 hours",
            urgent = true,
        ),
        Service(
            name = "Shakti",
            category = HARM,
            summary = "Safe housing, advocacy, and a multilingual crisis line for migrant and refugee women from Asian, African, and Middle Eastern backgrounds.",
            phones = listOf(Phone("Crisis line", "0800 742 584")),
            links = listOf(Link("Website", "https://www.shakti.org.nz/")),
            hours = "24 hours",
        ),
        Service(
            name = "Auckland Women's Refuge",
            category = HARM,
            summary = "Te Whare Ahuru-Mōwai. Crisis support, temporary accommodation, legal advocacy, and safety planning across Tāmaki Makaurau.",
            phones = listOf(Phone("Helpline", "09 378 7635")),
            links = listOf(Link("Website", "https://www.awrefuge.org.nz/")),
            hours = "24 hours",
        ),
        Service(
            name = "Shine",
            category = HARM,
            summary = "Short-term refuge and safety services for women and children leaving domestic abuse, including shelter, food, clothing, and a plan toward permanent housing.",
            phones = listOf(Phone("Helpline", "0508 744 633")),
            links = listOf(Link("Safe housing", "https://2shine.org.nz/what-we-do/safe-housing")),
        ),
        Service(
            name = "Wana",
            category = HARM,
            summary = "A Takanini trust with safe housing and support for tāne and their tamariki affected by family violence.",
            links = listOf(Link("Website", "https://www.wana.services/")),
        ),
        Service(
            name = "Eastern Refuge Society",
            category = HARM,
            summary = "Pakuranga service for domestic violence intervention, safe housing, and community social work for women, children, and whānau.",
            links = listOf(Link("Website", "https://www.easternrefugesociety.org.nz")),
        ),
        Service(
            name = "Safe to Talk",
            category = HARM,
            summary = "Confidential support after sexual harm. Listed with ACC sensitive claims on the where-to-go page.",
            phones = listOf(Phone("Call", "0800 044 334")),
            links = listOf(Link("Website", "https://www.safetotalk.nz/")),
        ),
        Service(
            name = "HELP Auckland",
            category = HARM,
            summary = "Support related to sexual harm, including for whānau who are not themselves eligible for an ACC sensitive claim. Based in Balmoral, with a Pasifika team. The directory says six sessions at no charge.",
            links = listOf(Link("Website", "https://helpauckland.org.nz/")),
        ),
        Service(
            name = "Counselling Services Centre",
            category = HARM,
            summary = "Papatoetoe service for counselling, crisis response, and practical support after trauma. The directory says six sessions at no charge for people affected by sexual harm.",
            links = listOf(Link("Contact", "https://cscnz.org.nz/contact-us/")),
        ),
        Service(
            name = "RainbowYOUTH",
            category = RAINBOW,
            summary = "Peer support, drop-in, resources, and advocacy with rainbow and takatāpui young people, whānau, and communities.",
            links = listOf(Link("Website", "https://ry.org.nz/")),
        ),
        Service(
            name = "Gender Minorities Aotearoa",
            category = RAINBOW,
            summary = "Advocacy and peer support by and for transgender people, including binary and non-binary, irawhiti takatāpui, and intersex trans people.",
            links = listOf(Link("Peer support", "https://genderminorities.com/services/peer-support-2/")),
        ),
        Service(
            name = "InsideOUT Kōaro",
            category = RAINBOW,
            summary = "Resources, workshops, advice, and support to make Aotearoa safer for rainbow and takatāpui people.",
            links = listOf(Link("Website", "https://insideout.org.nz/")),
        ),
        Service(
            name = "Intersex Aotearoa",
            category = RAINBOW,
            summary = "Intersex-led affirmation, referral, and advocacy for people with intersex variations.",
            links = listOf(Link("Website", "https://www.intersexaotearoa.org/")),
        ),
        Service(
            name = "Rainbow Path",
            category = RAINBOW,
            summary = "Peer support and advocacy led by rainbow refugees and asylum seekers.",
            links = listOf(Link("Website", "https://rainbowpathnz.com/")),
        ),
        Service(
            name = "Raukura Hauora",
            category = CULTURE,
            summary = "Kaupapa Māori health and social wellbeing across Waikato and Counties Manukau, including community health, clinical services, and addiction support.",
            links = listOf(Link("Website", "https://www.raukura.org.nz")),
        ),
        Service(
            name = "Vagus Centre",
            category = CULTURE,
            summary = "Counselling and family support aimed at Chinese and Asian communities. The directory says free counselling for Chinese people dealing with relationship, family violence, and parenting difficulties, and lists www.cmh.org.nz.",
            links = listOf(Link("Website listed", "https://www.cmh.org.nz")),
        ),
        Service(
            name = "Fonua Ola",
            category = CULTURE,
            summary = "Social work, counselling, youth programmes, money skills, family violence support, Whānau Ora, and parenting groups across Auckland. The Ōtāhuhu service offers eight free counselling sessions in English, Samoan, or Tongan.",
            links = listOf(Link("Website", "https://www.fonuaola.org.nz/")),
        ),
        Service(
            name = "Asian Family Services",
            category = CULTURE,
            summary = "Mental health counselling, social work, and gambling-harm support for Asian communities, in several languages.",
            phones = listOf(Phone("Call", "0800 862 342")),
            links = listOf(Link("Website", "https://www.asianfamilyservices.nz")),
        ),
        Service(
            name = "Refugees As Survivors",
            category = CULTURE,
            summary = "Mental health and wellbeing care for people from refugee backgrounds, asylum seekers, and survivors of torture. Mostly in Auckland.",
            links = listOf(Link("Website", "https://www.rasnz.co.nz")),
        ),
        Service(
            name = "Diversity Counselling",
            category = CULTURE,
            summary = "Multilingual counselling and psychotherapy for migrants, former refugees, and ethnic communities.",
            links = listOf(Link("Website", "https://www.diversitycounselling.org.nz")),
        ),
        Service(
            name = "Kāhui Tū Kaha",
            category = CULTURE,
            summary = "Māori organisation offering respite, mobile mental health support, counselling, and culturally tailored care, plus social, transitional, and youth housing.",
            links = listOf(Link("Website", "https://kahuitukaha.co.nz/")),
        ),
        Service(
            name = "Mahitahi Trust",
            category = CULTURE,
            summary = "Kaupapa Māori mental health and addiction services, including primary mental health, housing, and employment.",
            links = listOf(Link("Website", "https://www.mahitahi.co.nz/")),
        ),
        Service(
            name = "Pakuranga Counselling",
            category = CULTURE,
            summary = "Non-profit counselling for individuals, couples, and families.",
            links = listOf(Link("Website", "https://www.pakcc.org.nz/")),
        ),
        Service(
            name = "Emerge Aotearoa — Ka Puta Ka Ora",
            category = CULTURE,
            summary = "Mental health and disability support, including respite, day programmes, employment, residential, and mobile community support.",
            links = listOf(Link("Website", "https://emergeaotearoa.org.nz/")),
        ),
        Service(
            name = "Quitline",
            category = AOD,
            summary = "Free support to stop smoking, or to stop vaping.",
            phones = listOf(Phone("Call", "0800 778 778")),
            texts = listOf(Phone("Text", "4006")),
            links = listOf(Link("Website", "https://quit.org.nz/")),
            hours = "24 hours",
        ),
        Service(
            name = "CADS",
            category = AOD,
            summary = "Community Alcohol and Drug Services. Free harm-reduction support for people, families, and whānau affected by alcohol and other drugs.",
            links = listOf(
                Link(
                    "Health New Zealand",
                    "https://www.healthnz.govt.nz/hospitals-services/hospitals/auckland/community-alcohol-drug-services",
                ),
            ),
        ),
        Service(
            name = "Auckland City Mission social detox",
            category = AOD,
            summary = "A social detox with 24-hour support, usually for one to two weeks. You can ask for yourself or for someone you care about.",
            phones = listOf(Phone("Call", "09 303 3008")),
            links = listOf(
                Link("Website", "https://aucklandcitymission.org.nz/get-help/addiction-withdrawal-services/"),
                Link("Email", "mailto:detox@aucklandcitymission.org.nz"),
            ),
        ),
        Service(
            name = "Phoenix Centre",
            category = AOD,
            summary = "Community alcohol and drug support in Papakura, part of Emerge Aotearoa. Assessment, peer support, and mobile support. You can refer yourself, or a GP, court, police, or another service can refer you. The directory spells the email pheonix@.",
            phones = listOf(Phone("Call", "09 296 0983")),
            links = listOf(
                Link("Website", "https://emergeaotearoa.org.nz/our-services/addiction-support/phoenix-centre/"),
                Link("Email on the directory", "mailto:pheonix@emergaeaotearoa.org.nz"),
            ),
        ),
        Service(
            name = "Rainbow House",
            category = NEURO,
            summary = "Support for neurodiverse children, adolescents, and their families, with workers who have lived experience.",
            links = listOf(Link("Website", "https://www.rainbowhouse.co.nz/")),
        ),
        Service(
            name = "Autism NZ",
            category = NEURO,
            summary = "Support for autistic people and whānau, and education for the wider community.",
            phones = listOf(Phone("Call", "0800 288 476")),
            links = listOf(Link("Website", "https://autismnz.org.nz/")),
            hours = "Office hours",
        ),
        Service(
            name = "Neurocompass",
            category = NEURO,
            summary = "A searchable directory of neurodiverse support across Aotearoa. You can filter by place, service, and age.",
            links = listOf(Link("Directory", "https://neurocompass.org.nz/directory/")),
        ),
        Service(
            name = "Attainable Trust",
            category = NEURO,
            summary = "Employment and community participation for neurodiverse people.",
            links = listOf(Link("Website", "https://attainabletrust.org.nz/")),
        ),
        Service(
            name = "KidsLink",
            category = NEURO,
            summary = "A way for schools and families to find specialist support for a child or adolescent’s emotional and learning needs.",
            links = listOf(Link("Website", "https://kidslink.co.nz/")),
        ),
        Service(
            name = "Start with your GP",
            category = BODY,
            summary = "The where-to-go page says your GP is the first point of contact for physical health needs.",
        ),
        Service(
            name = "Health subsidies",
            category = BODY,
            summary = "Citizens and residents have fully or partly subsidised healthcare. Health New Zealand and Work and Income explain further help.",
            links = listOf(
                Link("Eligibility", "https://www.healthnz.govt.nz/hospitals-services/eligibility-subsidies"),
                Link("Work and Income", "https://www.workandincome.govt.nz/eligibility/health-and-disability/index.html"),
            ),
        ),
        Service(
            name = "Green Prescription",
            category = BODY,
            summary = "A free referral service that supports adults, children, young people, and whānau to be more active.",
            links = listOf(
                Link(
                    "Healthpoint",
                    "https://www.healthpoint.co.nz/community-health-and-social-services/community-health/green-prescription-sport-auckland/",
                ),
            ),
        ),
        Service(
            name = "Citizens Advice Bureau",
            category = PRACTICAL,
            summary = "Free, confidential information and advice about rights, services, and everyday problems. Some bureaus can book free legal information sessions.",
            links = listOf(Link("Website", "https://www.cab.org.nz/")),
        ),
        Service(
            name = "Ember",
            category = PRACTICAL,
            summary = "Ember Korowai Takitini. Mental health, addiction, intellectual disability, and wellbeing services that mix lived experience with professional support.",
            links = listOf(Link("Website", "https://www.ember.org.nz/")),
        ),
        Service(
            name = "ACC",
            category = PRACTICAL,
            summary = "No-fault cover for personal injury, including some injuries that are not only physical. Sensitive claims can include free therapy.",
            links = listOf(Link("What ACC covers", "https://www.acc.co.nz/im-injured/what-we-cover")),
        ),
        Service(
            name = "Work and Income",
            category = PRACTICAL,
            summary = "Income support and help to find work. The directory links to a tool that checks what you might be able to get.",
            links = listOf(Link("Check what you can get", "https://check.msd.govt.nz/")),
        ),
        Service(
            name = "Foodbank New Zealand",
            category = PRACTICAL,
            summary = "A directory for finding a food bank near you.",
            links = listOf(Link("Website", "https://www.foodbank.co.nz/")),
        ),
        Service(
            name = "Penina Trust",
            category = PRACTICAL,
            summary = "Pacific-led holistic support with the person, their aiga, and other community services.",
            links = listOf(Link("Website", "https://penina.org.nz/")),
        ),
        Service(
            name = "Pakuranga and Howick budgeting",
            category = PRACTICAL,
            summary = "Free, confidential financial mentoring.",
            links = listOf(Link("Website", "https://phbs.org.nz/")),
        ),
        Service(
            name = "Firstport",
            category = PRACTICAL,
            summary = "National disability information, funded through Whaikaha and run with Enable New Zealand. For disabled people, whānau, and workers. If you need help and you are not in immediate danger, the Disability Helpline is 24 hours. Immediate danger is still 111. You can text 111 if you are Deaf, hard of hearing, or speech impaired, after you register. Firstport explains that.",
            phones = listOf(Phone("Disability Helpline", "0800 11 12 13")),
            texts = listOf(Phone("Text", "8988")),
            links = listOf(
                Link("Firstport", "https://firstport.co.nz/"),
                Link("Emergencies", "https://firstport.co.nz/get-support/wellbeing/tinana/in-an-emergency"),
            ),
            hours = "Helpline 24 hours",
            urgent = true,
        ),
        Service(
            name = "Claybeds",
            category = PRACTICAL,
            summary = "A family business in Aotearoa making enclosed pine beds with padded corners for children who need a calmer, safer place to sleep or settle. This is equipment for home, not an emergency health service. The saved SimplyAble page was the same Claybeds page, so it is listed once.",
            links = listOf(Link("Website", "https://www.claybeds.co.nz/")),
        ),
        Service(
            name = "Health New Zealand",
            category = LINK,
            summary = "Te Whatu Ora. Hospitals, health topics, Healthline, and pages for health professionals. The saved Health NZ page was the public website.",
            links = listOf(
                Link("Home", "https://www.healthnz.govt.nz/"),
                Link("For health professionals", "https://www.healthnz.govt.nz/health-professionals"),
            ),
        ),
        Service(
            name = "OpenStreetMap",
            category = LINK,
            summary = "Free map used for the clinic pin and for sharing where you are. Map data is © OpenStreetMap contributors.",
            links = listOf(
                Link("Clinic on the map", "https://www.openstreetmap.org/?mlat=-36.89973&mlon=174.90884#map=17/-36.89973/174.90884"),
                Link("Copyright", "https://www.openstreetmap.org/copyright"),
            ),
        ),
        Service(
            name = "Starlink",
            category = LINK,
            summary = "Satellite internet, from the saved Starlink page. Use it only as a way to get a connection back when the phone or landline internet is down. It is not a health service. This app’s directory and handout stay on the phone and open without internet.",
            links = listOf(Link("Starlink", "https://www.starlink.com/")),
        ),
    )

    fun search(query: String, category: String?): List<Service> {
        val q = query.trim().lowercase()
        return services.filter { service ->
            val categoryOk = category == null || service.category == category
            val text = buildString {
                append(service.name)
                append(' ')
                append(service.summary)
                append(' ')
                append(service.category)
                service.phones.forEach { append(' ').append(it.label).append(' ').append(it.number) }
                service.texts.forEach { append(' ').append(it.number) }
            }.lowercase()
            categoryOk && (q.isEmpty() || text.contains(q))
        }
    }

    fun named(name: String): Service = services.first { it.name == name }

    val emergency: Service get() = named("Emergency services")
    val clinic: Service get() = named("Te Rawhiti")
    val afterHours: Service get() = named("After-hours mental health")
    val needToTalk: Service get() = named("Need to talk? 1737")
    val healthline: Service get() = named("Healthline")

    fun phonePathways(): List<Service> =
        services.filter { it.phones.isNotEmpty() || it.texts.isNotEmpty() }
}
