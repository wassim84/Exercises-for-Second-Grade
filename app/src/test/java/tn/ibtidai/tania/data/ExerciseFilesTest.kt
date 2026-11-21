package tn.ibtidai.tania.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** يتأكد أن كل ملفات التمارين صالحة قبل بناء التطبيق */
class ExerciseFilesTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val types = setOf("input", "numbers", "choice", "truefalse", "open")

    @Test fun allExerciseFilesAreValid() {
        val root = listOf(File("src/main/assets/exercises"), File("app/src/main/assets/exercises")).first { it.exists() }
        val files = root.walkTopDown().filter { it.extension == "json" && it.name != "manifest.json" }.toList()
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

    /** إن فشل هذا الاختبار: شغّل python3 tools/build_manifest.py */
    @Test fun manifestIsUpToDate() {
        val root = listOf(File("src/main/assets/exercises"), File("app/src/main/assets/exercises")).first { it.exists() }
        val manifest = json.decodeFromString(Manifest.serializer(), File(root, "manifest.json").readText())
        val actual = root.walkTopDown()
            .filter { it.isFile && it.extension == "json" && it.name != "manifest.json" }
            .associate { it.relativeTo(root).invariantSeparatorsPath to sha256(it.readBytes()) }
        assertEquals("manifest.json is stale: run python3 tools/build_manifest.py", actual, manifest.files)
    }
}
