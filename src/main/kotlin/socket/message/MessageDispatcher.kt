package dev.gangster.socket.message

import dev.gangster.socket.handler.DefaultSfHandler
import dev.gangster.socket.handler.DefaultXtHandler
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.utils.Logger

/**
 * Dispatch client message to a registered [MessageHandler].
 */
class MessageDispatcher() {
    private val sfHandlers = mutableListOf<MessageHandler<SfMessage>>()
    private val xtHandlers = mutableListOf<MessageHandler<XtMessage>>()

    private val defaultSf = DefaultSfHandler()
    private val defaultXt = DefaultXtHandler()

    fun registerSf(handler: MessageHandler<SfMessage>) = sfHandlers.add(handler)
    fun registerXt(handler: MessageHandler<XtMessage>) = xtHandlers.add(handler)

    fun findSfHandler(msg: SfMessage): List<MessageHandler<SfMessage>> {
        Logger.info { "Finding SF handler for msg: $msg" }
        val matches = sfHandlers.filter { it.match(msg) }
        Logger.info { "Handlers matched: $matches" }
        return matches.ifEmpty { listOf(defaultSf) }
    }

    fun findXtHandler(msg: XtMessage): List<MessageHandler<XtMessage>> {
        Logger.info { "Finding XT handler for msg: $msg" }
        val matches = xtHandlers.filter { it.match(msg) }
        Logger.info { "Handlers matched: $matches" }
        return matches.ifEmpty { listOf(defaultXt) }
    }

    fun close() {
        sfHandlers.clear()
        xtHandlers.clear()
    }
}
