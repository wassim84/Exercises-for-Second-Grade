package tn.ibtidai.tania.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerCheckerTest {
    private fun q(vararg a: String) = Question(prompt = "", answers = a.toList())

    @Test fun arabicDigitsAccepted() = assertTrue(AnswerChecker.isCorrect(q("12"), "١٢"))
    @Test fun tashkeelAndHamzaIgnored() = assertTrue(AnswerChecker.isCorrect(q("أحمد"), "احمَد"))
    @Test fun extraSpacesIgnored() = assertTrue(AnswerChecker.isCorrect(q("يوم الأحد"), "  يوم   الاحد "))
    @Test fun wrongAnswerRejected() = assertFalse(AnswerChecker.isCorrect(q("15"), "16"))
    private fun n(vararg a: String) = Question(type = "numbers", prompt = "", answers = a.toList())
    @Test fun numbersAnySeparator() = assertTrue(AnswerChecker.isCorrect(n("25 < 30 < 55"), "٢٥-٣٠-٥٥"))
    @Test fun numbersOrderMatters() = assertFalse(AnswerChecker.isCorrect(n("25 < 30 < 55"), "55 30 25"))
    @Test fun emptyRejected() = assertFalse(AnswerChecker.isCorrect(q("15"), " "))
}
