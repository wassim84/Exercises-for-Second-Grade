package tn.ibtidai.tania.data

import kotlinx.serialization.Serializable
import java.security.MessageDigest

/** manifest.json: لكل ملف تمرين بصمته (sha256)، فيعرف التطبيق ما تغيّر دون رقم نسخة يدويّ. */
@Serializable
data class Manifest(val files: Map<String, String>)

fun sha256(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
