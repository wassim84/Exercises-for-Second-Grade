package tn.ibtidai.tania.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** يتأكد أن كل ملفات التمارين صالحة قبل بناء التطبيق */
class ExerciseFilesTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val types = setOf("input", "numbers", "choice", "truefalse", "open")

    @Test fun allExerciseFilesAreValid() {
        val root = listOf(File("src/main/assets/exercises"), File("app/src/main/assets/exercises")).first { it.exists() }
        val files = root.walkTopDown().filter { it.extension == "json" }.toList()
        assertTrue("no exercise files", files.isNotEmpty())
        for (f in files) {
            val ex = json.decodeFromString(Exercise.serializer(), f.readText())
            for (q in ex.questions) {
                assertTrue("${f.name}: bad type ${q.type}", q.type in types)
                assertTrue("${f.name}: no answer for ${q.prompt}", q.answers.isNotEmpty())
                if (q.type == "choice") assertTrue(
                    "${f.name}: answer not in options for ${q.prompt}",
                    q.answers.all { a -> q.options.any { AnswerChecker.normalize(it) == AnswerChecker.normalize(a) } },
                )
            }
        }
    }
}
