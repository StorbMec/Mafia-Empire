package dev.gangster.socket.handler

import dev.gangster.socket.core.Connection
import dev.gangster.socket.message.SfMessage
import dev.gangster.utils.Logger

class DefaultSfHandler: MessageHandler<SfMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: SfMessage): Boolean {
        return true
    }

    override suspend fun handle(
        connection: Connection,
        message: SfMessage
    ) {
        Logger.warn { "XT handler not implemented for msg: $message" }
    }
}
