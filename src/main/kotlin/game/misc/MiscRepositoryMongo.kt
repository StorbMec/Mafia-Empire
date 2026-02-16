package dev.gangster.game.misc

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import dev.gangster.db.runMongoCatching
import dev.gangster.game.data.collection.PlayerData
import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.user.MafiaUserData
import dev.gangster.game.model.user.PlayerInfo
import kotlinx.coroutines.flow.firstOrNull

class MiscRepositoryMongo(private val collection: MongoCollection<PlayerData>) : MiscRepository {
    override suspend fun getProfile(playerId: Int): Result<PBPlayerProfile> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.profile
        }
    }

    override suspend fun updateProfile(
        playerId: Int,
        profile: PBPlayerProfile
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("profile", profile)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify profile for playerId=$playerId with profile=$profile")
            }

            Unit
        }
    }

    override suspend fun getPlayerInfo(playerId: Int): Result<PlayerInfo> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.playerInfo
        }
    }

    override suspend fun updatePlayerInfo(
        playerId: Int,
        playerInfo: PlayerInfo
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("playerInfo", playerInfo)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify playerInfo for playerId=$playerId with playerInfo=$playerInfo")
            }

            Unit
        }
    }

    override suspend fun getMafiaUserData(playerId: Int): Result<MafiaUserData> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            collection
                .find(filter)
                .firstOrNull()
                ?.mafiaUserData
        }
    }

    override suspend fun updateMafiaUserData(
        playerId: Int,
        mafiaUserData: MafiaUserData
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("mafiaUserData", mafiaUserData)

            val result = collection.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify mafiaUserData for playerId=$playerId with mafiaUserData=$mafiaUserData")
            }

            Unit
        }
    }
}
