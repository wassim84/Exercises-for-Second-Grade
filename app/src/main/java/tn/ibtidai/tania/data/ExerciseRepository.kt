package tn.ibtidai.tania.data

import android.content.Context
import kotlinx.serialization.json.Json

class ExerciseRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun list(subject: Subject): List<Exercise> {
        val dir = "exercises/${subject.id}"
        val files = context.assets.list(dir).orEmpty().filter { it.endsWith(".json") }.sorted()
        return files.mapNotNull { name ->
            runCatching {
                context.assets.open("$dir/$name").bufferedReader(Charsets.UTF_8).use {
                    json.decodeFromString(Exercise.serializer(), it.readText())
                }
            }.getOrNull()
        }
    }
}
