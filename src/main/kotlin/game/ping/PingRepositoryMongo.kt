package dev.gangster.game.ping

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import dev.gangster.db.runMongoCatching
import dev.gangster.game.data.collection.PlayerData
import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.user.MafiaPoliceData
import dev.gangster.game.model.user.MafiaProgressData
import kotlinx.coroutines.flow.firstOrNull

class PingRepositoryMongo(private val collection: MongoCollection<PlayerData>): PingRepository {
    override suspend fun getMissionProgress(playerId: Int): Result<MafiaProgressData> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.missionProgress
        }
    }

    override suspend fun updateMissionProgress(
        playerId: Int,
        progress: MafiaProgressData
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("missionProgress", progress)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify missionProgress for playerId=$playerId with missionProgress=$progress")
            }

            Unit
        }
    }

    override suspend fun getPoliceData(playerId: Int): Result<MafiaPoliceData> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.policeData
        }
    }

    override suspend fun updatePoliceData(
        playerId: Int,
        policeData: MafiaPoliceData
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("policeData", policeData)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify policeData for playerId=$playerId with policeData=$policeData")
            }

            Unit
        }
    }

    override suspend fun getNextDuelTime(playerId: Int): Result<Long> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.nextDuel
        }
    }

    override suspend fun updateNextDuelTime(playerId: Int, time: Long): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("nextDuel", time)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify nextDuel for playerId=$playerId with nextDuel=$time")
            }

            Unit
        }
    }

    override suspend fun getPlayerStatus(playerId: Int): Result<PBPlayerStatus> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.playerStatus
        }
    }

    override suspend fun updatePlayerStatus(
        playerId: Int,
        status: PBPlayerStatus
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("playerStatus", status)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify playerStatus for playerId=$playerId with status=$status")
            }

            Unit
        }
    }
}
