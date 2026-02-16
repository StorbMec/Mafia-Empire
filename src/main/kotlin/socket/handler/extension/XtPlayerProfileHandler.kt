package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.model.protobuf.misc.PBMiscPlayerProfileResponse
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlin.io.encoding.Base64

/**
 * playerprofile, which gets player's profile.
 */
class XtPlayerProfileHandler(private val serverContext: ServerContext): MessageHandler<XtMessage> {
    override val priority: Int
        get() = -1

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA || message.command == XtConstants.COMMAND_PLAYER_PROFILE
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val services = serverContext.requirePlayerContext(connection.playerId).services

        val pbResponse = PBMiscPlayerProfileResponse(
            result = 1,
            playerId = connection.playerId,
            playerName = services.misc.getProfile().name,
            profile = services.misc.getProfile(),
        )
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_PROFILE,
            reqId = message.reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }
}
