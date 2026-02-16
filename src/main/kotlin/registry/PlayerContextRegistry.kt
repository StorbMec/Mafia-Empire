package dev.gangster.registry

import com.mongodb.kotlin.client.coroutine.MongoCollection
import dev.gangster.context.PlayerContext
import dev.gangster.context.PlayerServices
import dev.gangster.game.data.collection.PlayerData
import dev.gangster.db.CollectionName
import dev.gangster.db.Database
import dev.gangster.game.fight.FightService
import dev.gangster.game.misc.MiscRepositoryMongo
import dev.gangster.game.misc.MiscService
import dev.gangster.game.mission.MissionRepositoryMongo
import dev.gangster.game.mission.MissionService
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.ping.PingRepositoryMongo
import dev.gangster.game.ping.PingService
import dev.gangster.socket.core.Connection
import dev.gangster.utils.Logger
import io.ktor.util.date.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks each active player's context.
 */
class PlayerContextRegistry {
    val players = ConcurrentHashMap<Int, PlayerContext>()

    /**
     * Create context for a player.
     */
    suspend fun createContext(
        connection: Connection,
        db: Database,
        useMongo: Boolean
    ) {
        val playerId = connection.playerId
        val playerAccount = db.loadPlayerAccount(playerId)
        if (playerAccount.isFailure) {
            Logger.error { "Error when creating context for playerId=$playerId: ${playerAccount.exceptionOrNull()}" }
        }

        val context = PlayerContext(
            playerId = playerId,
            connection = connection,
            onlineSince = getTimeMillis(),
            playerAccount = playerAccount.getOrThrow(),
            services = initializeServices(playerId, db, useMongo, playerAccount.getOrThrow().lastLogin)
        )
        players[playerId] = context
    }

    private suspend fun initializeServices(
        playerId: Int,
        db: Database,
        useMongo: Boolean,
        lastLogin: Long
    ): PlayerServices {
        // if (useMongo)

        val dataCollection = db.getCollection<MongoCollection<PlayerData>>(CollectionName.PLAYER_DATA_COLLECTION)

        val playerData = db.loadPlayerData(playerId)
        if (playerData.isFailure) {
            Logger.error { "Error when initializing services for playerId=$playerId: ${playerData.exceptionOrNull()}" }
        }

        val misc = MiscService(MiscRepositoryMongo(dataCollection)).also { it.init(playerId) }
        val mission = MissionService(MissionRepositoryMongo(dataCollection)).also { it.init(playerId) }
        val ping = PingService(PingRepositoryMongo(dataCollection)).also { it.init(playerId) }
        val fight = FightService().also { it.init(playerId) }

        // calculate timer, such as updating if mission has finished while player is logged off
        if (ping.getPlayerStatus() == PBPlayerStatusConstants.MISSION) {
            if (ping.didMissionFinish(lastLogin)) {
                ping.signalMissionComplete()
            } else {
                ping.updateMissionProgressFromOffline(lastLogin)
            }
        }

        return PlayerServices(
            misc = misc,
            mission = mission,
            ping = ping,
            fight = fight
        )
    }

    /**
     * Get context of [playerId].
     *
     * @return null if context isn't found.
     */
    fun getContext(playerId: Int): PlayerContext? {
        return players[playerId]
    }

    /**
     * Update the context of a player with a lambda function.
     *
     * The [update] method pass the current context and expects to return the updated context.
     */
    fun updateContext(playerId: Int, update: (PlayerContext) -> PlayerContext) {
        val context = players.get(playerId) ?: return
        players[playerId] = update(context)
    }

    /**
     * Remove player to free-up memory.
     */
    fun removePlayer(playerId: Int) {
        players.remove(playerId)
    }

    fun close() {
        players.values.forEach {
            it.connection.close()
        }
        players.clear()
    }
}
