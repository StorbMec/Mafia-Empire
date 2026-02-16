package dev.gangster.socket.handler.extension

import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString

/**
 * Handle version check (again)
 */
class XtVersionCheckHandler : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_VCK
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val buildNumberServer = 2040 // same with game client
        val unknown = 0
        connection.sendRaw(
            SmartFoxString.makeXt(
                command = XtConstants.COMMAND_VCK,
                reqId = message.reqId,
                statusCode = 0,
                mode = XtMode.Nothing,
                buildNumberServer,
                unknown
            )
        )
    }
}
