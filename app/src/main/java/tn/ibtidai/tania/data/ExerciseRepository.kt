package tn.ibtidai.tania.data

import android.content.Context
import kotlinx.serialization.json.Json

/**
 * يجمع التمارين المضمّنة في التطبيق مع التمارين المحمّلة من GitHub.
 * إن وُجد manifest محمّل، فهو الذي يحدّد قائمة التمارين (فيُخفي ما حُذف).
 */
class ExerciseRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    val sync = ContentSync(context)

    fun list(subject: Subject): List<Exercise> {
        val prefix = "${subject.id}/"
        val bundled = context.assets.list("exercises/${subject.id}").orEmpty()
            .filter { it.endsWith(".json") }.map { prefix + it }
        val manifest = sync.readLocalManifest()
        val paths = manifest?.files?.keys?.filter { it.startsWith(prefix) } ?: bundled
        return paths.sorted().mapNotNull { path -> load(path) }
    }

    private fun load(path: String): Exercise? = runCatching {
        val text = sync.cachedFile(path)?.readText(Charsets.UTF_8)
            ?: context.assets.open("exercises/$path").bufferedReader(Charsets.UTF_8).use { it.readText() }
        json.decodeFromString(Exercise.serializer(), text)
    }.getOrNull()
}
