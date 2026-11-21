package tn.ibtidai.tania.data

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** مكان التمارين على GitHub. يكفي إضافة ملف JSON وتحديث manifest.json هناك ليصل للتلاميذ. */
object ContentConfig {
    const val BASE_URL =
        "https://raw.githubusercontent.com/wassim84/Exercises-for-Second-Grade/main/app/src/main/assets/exercises/"
}

class ContentSync(private val context: Context, private val baseUrl: String = ContentConfig.BASE_URL) {
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheDir = File(context.filesDir, "exercises")
    private val manifestFile = File(cacheDir, "manifest.json")

    /** نتيجة: عدد الملفات الجديدة أو المعدّلة أو المحذوفة. 0 = لا جديد. يرمي استثناء إن لم يوجد إنترنت. */
    fun sync(): Int {
        val remoteBytes = download("manifest.json")
        val remote = json.decodeFromString(Manifest.serializer(), remoteBytes.decodeToString())
        var changes = 0

        for ((path, hash) in remote.files) {
            if (!isSafe(path)) continue
            if (localHash(path) == hash) continue
            val bytes = download(path)
            if (sha256(bytes) != hash) continue
            json.decodeFromString(Exercise.serializer(), bytes.decodeToString()) // يتأكد أن الملف صالح
            val target = File(cacheDir, path)
            target.parentFile?.mkdirs()
            val tmp = File(target.path + ".tmp")
            tmp.writeBytes(bytes)
            tmp.renameTo(target)
            changes++
        }
        // ملفات حُذفت من GitHub
        val old = readLocalManifest()?.files?.keys.orEmpty()
        for (path in old - remote.files.keys) {
            if (isSafe(path)) File(cacheDir, path).delete()
            changes++
        }
        cacheDir.mkdirs()
        manifestFile.writeBytes(remoteBytes)
        return changes
    }

    /** آخر manifest تمّ تحميله، أو null إن لم يتمّ أيّ تحديث بعد. */
    fun readLocalManifest(): Manifest? = runCatching {
        json.decodeFromString(Manifest.serializer(), manifestFile.readText())
    }.getOrNull()

    fun cachedFile(path: String): File? = File(cacheDir, path).takeIf { isSafe(path) && it.isFile }

    private fun localHash(path: String): String? {
        cachedFile(path)?.let { return sha256(it.readBytes()) }
        return runCatching { context.assets.open("exercises/$path").use { sha256(it.readBytes()) } }.getOrNull()
    }

    private fun isSafe(path: String) =
        path.endsWith(".json") && !path.contains("..") && !path.startsWith("/") && path != "manifest.json"

    private fun download(path: String): ByteArray {
        val conn = URL(baseUrl + path.split('/').joinToString("/") { java.net.URLEncoder.encode(it, "UTF-8").replace("+", "%20") })
            .openConnection() as HttpURLConnection
        conn.connectTimeout = 8000
        conn.readTimeout = 15000
        conn.useCaches = false
        try {
            if (conn.responseCode != 200) error("HTTP ${conn.responseCode} for $path")
            return conn.inputStream.use { it.readBytes() }
        } finally {
            conn.disconnect()
        }
    }
}
