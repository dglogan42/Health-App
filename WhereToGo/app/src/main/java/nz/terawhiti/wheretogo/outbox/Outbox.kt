package nz.terawhiti.wheretogo.outbox

import java.util.Base64

data class OutboxItem(
    val id: Long,
    val title: String,
    val body: String,
    val createdAtEpochMs: Long,
)

/**
 * Notes the user prepared to share later. Nothing in here is delivered on its own.
 */
object OutboxCodec {
    fun encode(items: List<OutboxItem>): String =
        items.joinToString("\n") { item ->
            listOf(
                item.id.toString(),
                item.createdAtEpochMs.toString(),
                b64(item.title),
                b64(item.body),
            ).joinToString("\t")
        }

    fun decode(raw: String): List<OutboxItem> {
        if (raw.isBlank()) return emptyList()
        return raw.lineSequence().mapNotNull { line ->
            val parts = line.split('\t')
            if (parts.size != 4) return@mapNotNull null
            val id = parts[0].toLongOrNull() ?: return@mapNotNull null
            val created = parts[1].toLongOrNull() ?: return@mapNotNull null
            OutboxItem(id, unb64(parts[2]), unb64(parts[3]), created)
        }.toList()
    }

    fun add(
        existing: List<OutboxItem>,
        title: String,
        body: String,
        nowMs: Long,
        id: Long = nowMs,
        limit: Int = 20,
    ): List<OutboxItem> {
        val next = OutboxItem(id, title.trim(), body.trim(), nowMs)
        return (listOf(next) + existing).take(limit)
    }

    private fun b64(text: String): String =
        Base64.getEncoder().encodeToString(text.toByteArray(Charsets.UTF_8))

    private fun unb64(text: String): String =
        String(Base64.getDecoder().decode(text), Charsets.UTF_8)
}
