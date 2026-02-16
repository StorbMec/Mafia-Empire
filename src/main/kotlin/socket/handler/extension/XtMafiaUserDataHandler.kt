package dev.gangster.socket.handler.extension

import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.model.request.LreRequest
import dev.gangster.game.model.request.OudRequest
import dev.gangster.game.model.user.toOudResponse
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString

/**
 * OUD, which load mafia user data (kind of player's metadata).
 */
class XtMafiaUserDataHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA || message.command == XtConstants.COMMAND_MAFIA_USER_DATA
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val services = serverContext.requirePlayerContext(connection.playerId).services

        // request payload only appear during gameplay request
        val request = if (message.command == XtConstants.COMMAND_MAFIA_USER_DATA) {
            SmartFoxString.parseObjXt<OudRequest>(message)
        } else {
            null
        }

        // if request don't appear, then it's player itself
        val reqPlayerId = request?.playerId ?: connection.playerId

        val mafiaUserData = services.misc.getOtherMafiaUserData(reqPlayerId)

        val oudXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_MAFIA_USER_DATA,
            reqId = message.reqId,
            statusCode = 0,
            mode = XtMode.Nothing,
            connection.playerId,
            mafiaUserData.toOudResponse()
        )
        connection.sendRaw(oudXtResponse)
    }
}
