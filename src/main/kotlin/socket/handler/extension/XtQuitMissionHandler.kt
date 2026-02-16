package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.fight.FightService
import dev.gangster.game.model.protobuf.common.Difficulty
import dev.gangster.game.model.protobuf.common.PBCharacterClass
import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.model.protobuf.common.PBReward
import dev.gangster.game.model.protobuf.common.toFightReward
import dev.gangster.game.model.protobuf.fight.PBFight
import dev.gangster.game.model.protobuf.mission.PBMissionQuitMissionResponse
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.utils.Logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlin.io.encoding.Base64
import kotlin.random.Random

class XtQuitMissionHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    private val CONTINUE_MISSION_TO_FIGHT = 1
    private val NOT_ENOUGH_GOLD_TO_SKIP = 2
    private val ABANDON_MISSION = 4

    override val priority: Int?
        get() = null

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_QUIT_MISSION
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        val services = serverContext.requirePlayerContext(connection.playerId).services
        val playerProfile = services.misc.getProfile()

        val missionId = services.ping.getMissionProgress().actionId
        val activeMission =
            requireNotNull(services.mission.getMissionById(missionId).second) { "activeMission null during quitmission handling" }

        val result = if (services.ping.getPlayerStatus() == PBPlayerStatusConstants.MISSION) {
            // mission still in progress
            // two possibility: skip timer with gold or abandon mission
            // TO-DO
            ABANDON_MISSION

        } else if (services.ping.getPlayerStatus() == PBPlayerStatusConstants.FINISH_MISSION) {
            // mission completed normally, continue to fight
            CONTINUE_MISSION_TO_FIGHT
        } else {
            Logger.warn { "Invalid state during QuitMission handling, status: ${services.ping.getPlayerStatus()}" }
            ABANDON_MISSION
        }

        val fightResult =
            continueMissionToFight(connection.playerId, playerProfile, activeMission.opponentClass, services.fight)

        // supposedly:
        // - update achievement
        // - update player level, stats

        // 20% chance of obtaining 1-100 gold
        val goldChance = 0.2
        val goldAmount = if (Random.nextDouble() < goldChance) Random.nextInt(1, 100) else 0

        // reward from mission data
        val reward = activeMission.toFightReward(gold = goldAmount)

        val response = PBMissionQuitMissionResponse(
            result = result,
            fight = fightResult,
            reward = reward
        )

        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_QUIT_MISSION,
            reqId = message.reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(response))
        )

        connection.sendRaw(xtRes)
    }

    fun continueMissionToFight(
        playerId: Int,
        playerProfile: PBPlayerProfile,
        opponentClass: PBCharacterClass,
        fightService: FightService
    ): PBFight {
        val playerLevel = playerProfile.level
        val playerFighter = fightService.createPlayerFighter(
            id = playerId,
            weaponDmgRange = (3 * playerLevel)..(8 * playerLevel),
            playerProfile = playerProfile
        )

        val npcProfile = PBPlayerProfile.generateProfileRelativeToPlayer(
            characterClass = opponentClass,
            playerProfile = playerProfile,
            difficulty = Difficulty.VERY_EASY,
        )
        val npcFighter = fightService.createNPCFighter(
            weaponDmgRange = (1 * playerLevel)..(4 * playerLevel),
            npcProfile = npcProfile
        )

        val fightResult = fightService.beginFight(
            leftFighter = playerFighter,
            rightFighter = npcFighter
        )

        return fightResult
    }
}
