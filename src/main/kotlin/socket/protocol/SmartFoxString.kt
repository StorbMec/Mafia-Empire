package dev.gangster.socket.protocol

import dev.gangster.context.GlobalContext
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.reflect.KParameter

/**
 * Always have trailing `\u0000` (null byte)
 *
 * Request structure:
 * - `%xt%<zone>%<command>%<reqId>%<payload>%`
 *
 * Response structure:
 * - protobuf: `%xt%<command>%<reqId>%-1%<payload>%`
 * - raw message: `%xt%<reqId>%<status_code>%<args_strings>%` (structure of args_strings depend on each message)
 */
object SmartFoxString {

    /**
     * Utility to make Xt message
     *
     * @param command
     * @param reqId
     * @param statusCode only inserted if provided non-null code
     * @param mode if not protobuf, nothing will be inserted.
     * @param msg the payload which will be inserted after everything
     */
    fun makeXt(
        command: String, reqId: Int,
        statusCode: Int? = null, mode: XtMode = XtMode.Nothing, vararg msg: Any?
    ): String {
        return buildString {
            append("%xt")
            append("%$command")
            append("%$reqId")
            if (statusCode != null) {
                append("%$statusCode")
            }
            if (mode == XtMode.Protobuf) {
                append("%-1")
            }
            msg.forEach {
                if (it == null || it == "") {
                    append("%")
                } else {
                    append("%$it")
                }
            }
            append("%\u0000")
        }
    }

    /**
     * Parse XT message generically into string parts
     * Example: %xt%MafiaEx%lre%1%foo%bar%baz%
     */
    fun parseXt(raw: ByteArray): XtMessage {
        val s = raw.toString(Charsets.UTF_8).trimEnd('%', '\u0000')
        val parts = s.split('%').filter { it.isNotEmpty() } // only removes leading/trailing blanks
        require(parts.size >= 4) { "Invalid XT message: $s" }

        // parts[0] = xt
        val zone = parts[1]
        val command = parts[2]
        val reqId = parts[3].toInt()
        val afterReqId = parts.drop(4)

        return when {
            afterReqId.isEmpty() -> {
                // no params, no payload
                XtMessage(zone, command, reqId, XtMode.Nothing, emptyList())
            }

            afterReqId.size == 1
                    && afterReqId[0].length % 4 == 0
                    && afterReqId[0].any { it in "+/=" } -> {
                // looks like protobuf payload
                val payload = Base64.decode(afterReqId[0])
                XtMessage(zone, command, reqId, XtMode.Protobuf, emptyList(), payload)
            }

            else -> {
                // plain XT with var number of params
                val params = afterReqId
                    .map { if (it == "<RoundHouseKick>") "" else it.substringBefore(";;;;;;;;;;;") } // ; is repeating junk in LRE message
                    .dropLastWhile { it.isEmpty() || it == "0" }

                XtMessage(zone, command, reqId, XtMode.Nothing, params)
            }
        }
    }

    /**
     * Parse XT message with JSON object payload
     * Example: %xt%MafiaEx%lre%1%{"mail":"x","pw":"y"}%
     */
    inline fun <reified T> parseJsonXt(xtMessage: XtMessage, json: Json = GlobalContext.json): T {
        require(xtMessage.stringParts.isNotEmpty()) { "XT object payload missing" }

        val objStr = xtMessage.stringParts.first()
        return json.decodeFromString(objStr)
    }

    /**
     * Parse XT message from string parts type and transform it into an object type
     * Example: %xt%MafiaEx%lre%1%foo%bar%baz% the payload becomes data class
     */
    inline fun <reified T> parseObjXt(
        xtMessage: XtMessage,
        json: Json = Json { ignoreUnknownKeys = true }
    ): T {
        val ctor = T::class.constructors.first()

        val allParams = ctor.parameters
            .filter { it.name != null && it.name != "seen0" && it.name != "serializationConstructorMarker" }

        val paramsForObject = xtMessage.stringParts.take(allParams.size)

        val jsonPairs = allParams.zip(paramsForObject).map { (p, v) ->
            "\"${p.name}\":${toJsonLiteral(p, v)}"
        }

        val objStr = "{${jsonPairs.joinToString(",")}}"
        return json.decodeFromString(objStr)
    }

    fun toJsonLiteral(param: KParameter, raw: String): String {
        if (param.type.isMarkedNullable && (raw.isEmpty() || raw == "null")) return "null"

        val classifier = param.type.classifier
        return when (classifier) {
            Boolean::class -> {
                val b = when (raw.lowercase()) {
                    "1", "true"  -> "true"
                    "0", "false" -> "false"
                    else         -> "false"
                }
                b
            }
            Int::class -> raw.toIntOrNull()?.toString() ?: "0"
            Long::class -> raw.toLongOrNull()?.toString() ?: "0"
            Float::class -> raw.toFloatOrNull()?.toString() ?: "0"
            Double::class -> raw.toDoubleOrNull()?.toString() ?: "0"
            else -> {
                // enums & strings: emit as JSON string
                "\"${escapeJson(raw)}\""
            }
        }
    }

    fun escapeJson(s: String): String = buildString {
        for (c in s) {
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(c)
            }
        }
    }
}
