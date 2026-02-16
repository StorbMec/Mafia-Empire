package dev.gangster.socket.handler.extension

import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.model.user.toPayload
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString

/**
 * OIO, which gets player's info.
 */
class XtPlayerInfoHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA || message.command == XtConstants.COMMAND_PLAYER_INFO
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val services = serverContext.requirePlayerContext(connection.playerId).services

        val oioRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_INFO,
            reqId = message.reqId,
            statusCode = 0,
            mode = XtMode.Nothing,
            services.misc.getPlayerInfo().toPayload()
        )
        connection.sendRaw(oioRes)
    }
}
