package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
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
 * viewmissions, which loads player's available missions.
 *
 */
class XtViewMissionsHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA || message.command == XtConstants.COMMAND_VIEW_MISSIONS
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        // request is empty
        val mission = serverContext.requirePlayerContext(connection.playerId).services.mission

        // NOTE: tutorial mission and opponent is fixed in the client-side, so it can be unexpected
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_MISSIONS,
            reqId = message.reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(mission.getMissionResponse()))
        )
        connection.sendRaw(xtRes)
    }
}
