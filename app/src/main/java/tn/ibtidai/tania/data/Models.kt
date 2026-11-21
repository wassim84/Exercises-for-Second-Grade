package tn.ibtidai.tania.data

import kotlinx.serialization.Serializable

/** المواد الخمس. المعرّف (id) هو اسم المجلد داخل assets/exercises */
enum class Subject(val id: String, val title: String, val emoji: String, val color: Long) {
    MATH("math", "رياضيات", "🔢", 0xFF42A5F5),
    READING("reading", "قراءة", "📖", 0xFFAB47BC),
    WRITING("writing", "إنتاج كتابي", "✏️", 0xFFFFA726),
    SCIENCE("science", "إيقاظ علمي", "🌱", 0xFF66BB6A),
    ISLAMIC("islamic", "تربية إسلامية", "🕌", 0xFF26A69A);
}

/**
 * تمرين واحد = ملف JSON واحد.
 * أنواع الأسئلة:
 *  - "input"  : خانة يكتب فيها التلميذ. answers = الإجابات المقبولة.
 *  - "choice" : اختيار من متعدد. options = الخيارات، answers = نص الخيار (أو الخيارات) الصحيح.
 *  - "numbers": خانة لعدد أو أكثر (ترتيب، تفكيك...). تُقارن الأعداد فقط بترتيبها،
 *               فلا يهمّ إن كتب التلميذ "25 < 30" أو "25 - 30" أو "25 30".
 *  - "truefalse": صحيح / خطأ. answers = ["صحيح"] أو ["خطأ"].
 *  - "open"   : كتابة حرة (إنتاج كتابي) لا تُصحَّح آليًا، answers = نموذج إجابة يُعرض بعد الإنهاء.
 */
@Serializable
data class Exercise(
    val id: String,
    val title: String,
    val instructions: String = "",
    val text: String? = null,          // نص قراءة اختياري يظهر فوق الأسئلة
    val questions: List<Question>,
)

@Serializable
data class Question(
    val type: String = "input",
    val prompt: String,
    val options: List<String> = emptyList(),
    val answers: List<String>,
)
