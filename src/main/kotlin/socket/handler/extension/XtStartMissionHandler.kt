package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.model.protobuf.common.missionTime
import dev.gangster.game.model.protobuf.mission.PBMissionStartRequest
import dev.gangster.game.model.protobuf.mission.PBMissionStartResponse
import dev.gangster.game.model.user.MafiaProgressData
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.task.TaskTemplate
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.io.encoding.Base64
import kotlin.math.acos
import kotlin.time.Duration.Companion.seconds

class XtStartMissionHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 0

    private val APPROVED = 1
    private val BUSY = 2
    private val NOT_ENOUGH_ENERGY = 3
    private val MISSION_NOT_AVAILABLE = 4

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_START_MISSION
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val request = ProtoBuf.decodeFromByteArray<PBMissionStartRequest>(message.pbPayload)
        val services = serverContext.requirePlayerContext(connection.playerId).services

        val status = services.ping.getPlayerStatus()
        val energy = services.misc.getMafiaUserData().userMissionEnergy

        // NOTE: tutorial mission and opponent is fixed in the client-side, so it can be unexpected
        // the server now generates random mission
        val (missionGiver, activeMission) = services.mission.getMissionById(request.missionNumber)

        // TO-DO I think energy was fixed and affected by player level and booster
        val requiredEnergy = 3

        val result = if (status != PBPlayerStatusConstants.FREE) {
            BUSY
        } else if (energy < requiredEnergy) {
            NOT_ENOUGH_ENERGY
        } else if (activeMission == null) {
            MISSION_NOT_AVAILABLE
        } else {
            APPROVED
        }

        val pbResponse = PBMissionStartResponse(
            result = result,
            playerStatus = status,
            progressTime = 0, // because mission start, progress start from 0
            remainingTime = activeMission?.time,
            missionGiver = missionGiver + 1, // missionGiver is index of activeMission so plus one
            missionId = activeMission?.id
        )

        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_START_MISSION,
            reqId = message.reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )

        connection.sendRaw(xtRes)

        if (activeMission != null) {
            // updates MafiaProgressData to globally reflect a mission is ongoing
            services.ping.signalStartMission(getTimeMillis(), activeMission.id, activeMission.time, missionGiver)

            // register the task on mission complete
            serverContext.taskDispatcher.runTask(
                connection = connection,
                taskTemplateKey = TaskTemplate.Timer,
                cfgBuilder = {
                    targetTask = ""
                    initialRunDelay = activeMission.time.seconds
                    repeatDelay = null
                    extra = mapOf("info" to "Mission completed for mission: $activeMission")
                },
                onComplete = {
                    services.ping.signalMissionComplete()
                }
            )
        }
    }
}
