package tn.ibtidai.tania.data

object AnswerChecker {
    private val tashkeel = Regex("[\\u064B-\\u0652\\u0670\\u0640]")
    private val spaces = Regex("\\s+")
    private val trailingPunct = Regex("[.،,؛;!؟?]+$")

    /** توحيد الكتابة حتى لا يُحسب خطأ بسبب الشكل أو الهمزة أو نوع الأرقام */
    fun normalize(s: String): String {
        var t = s.trim()
        t = tashkeel.replace(t, "")
        val sb = StringBuilder()
        for (c in t) {
            sb.append(
                when (c) {
                    in '٠'..'٩' -> '0' + (c - '٠')
                    in '۰'..'۹' -> '0' + (c - '۰')
                    'أ', 'إ', 'آ', 'ٱ' -> 'ا'
                    'ى' -> 'ي'
                    else -> c
                }
            )
        }
        t = spaces.replace(sb.toString(), " ")
        t = trailingPunct.replace(t, "")
        return t.lowercase()
    }

    private val number = Regex("\\d+")

    /** الأعداد فقط بالترتيب: "25 < 30 < 55" و "25-30-55" و "25 30 55" كلها متساوية */
    fun numbersOf(s: String): List<String> =
        number.findAll(normalize(s)).map { it.value.trimStart('0').ifEmpty { "0" } }.toList()

    fun isCorrect(q: Question, given: String): Boolean {
        if (q.type == "open") return given.isNotBlank()
        if (q.type == "numbers") {
            val g = numbersOf(given)
            return g.isNotEmpty() && q.answers.any { numbersOf(it) == g }
        }
        val g = normalize(given)
        if (g.isEmpty()) return false
        return q.answers.any { normalize(it) == g }
    }
}
