package nz.terawhiti.wheretogo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentTest {
    @Test
    fun youthlineUsesTheFullPublishedNumber() {
        val youth = Directory.services.first { it.name == "Youthline" }
        assertEquals("0800376633", youth.phones.first().number.filter { it.isDigit() })
        assertEquals("234", youth.texts.first().number.filter { it.isDigit() })
    }

    @Test
    fun lifelineTextSpellsHelp() {
        val life = Directory.services.first { it.name == "Lifeline" }
        assertTrue(life.texts.any { it.number.filter { ch -> ch.isDigit() } == "4357" })
    }

    @Test
    fun handoutKeepsEveryQuestionFromTheScan() {
        assertEquals(12, Handout.questions.size)
        assertEquals("q1", Handout.questions.first().id)
        assertEquals("next", Handout.questions.last().id)
        assertTrue(Handout.INTRO.contains("no right or wrong answers"))
        assertTrue(Handout.workerConsiderations.any { it.contains("111") })
        assertTrue(Handout.personConsiderations.any { it.contains("this phone") })
    }

    @Test
    fun everyNumberCanBeDialled() {
        Directory.services.forEach { service ->
            (service.phones + service.texts).forEach { phone ->
                val digits = phone.number.filter { it.isDigit() }
                assertTrue("${service.name} ${phone.number}", digits.length in 3..12)
            }
        }
    }

    @Test
    fun namesAreUniqueSoListsStayStable() {
        val names = Directory.services.map { it.name }
        assertEquals(names.size, names.toSet().size)
    }
}
