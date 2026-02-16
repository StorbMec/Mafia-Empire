package dev.gangster.socket.handler.extension

import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.model.constants.toCity
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.model.response.PngResponse
import dev.gangster.game.model.response.toResponse
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay
import kotlin.math.max

/**
 * png, which loads player's ping data (mission progress, duel cooldown, police, etc)
 *
 * this handler is static, it only handles png when apd (on game load) and png if game request themself.
 * the rest of ping data is sent by PingTask.
 */
class XtGamePingHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 0

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA || message.command == XtConstants.COMMAND_PING
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val services = serverContext.requirePlayerContext(connection.playerId).services
        val profile = services.misc.getProfile()

        // update timer data on each ping
        // TO-DO update other timer than mission
        if (services.ping.getPlayerStatus() == PBPlayerStatusConstants.MISSION) {
            services.ping.tickMissionProgress()
        }

        val response = PngResponse(
            playerStatus = services.ping.getPlayerStatus() - 1,
            city = profile.cityId.toCity(),
            progressData = services.ping.getMissionProgress(),
            messageDataAmount = 0, // TO-DO only send the amount of new messages
            policeData = services.ping.getPoliceData(),
            duelCooldown = max(0, (getTimeMillis() - services.ping.getNextDuelTime()).toInt() / 1000),
            clanStatus = null
        )
        val pngXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PING,
            reqId = message.reqId,
            statusCode = 0,
            mode = XtMode.Nothing,
            *response.toResponse().toTypedArray()
        )

        // delay a second to prevent race condition
        // allowing services to update timer
        delay(1000)

        connection.sendRaw(pngXtResponse)
    }
}
