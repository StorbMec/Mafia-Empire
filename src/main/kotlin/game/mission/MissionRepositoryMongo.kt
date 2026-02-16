package dev.gangster.game.mission

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import dev.gangster.db.runMongoCatching
import dev.gangster.game.data.collection.PlayerData
import dev.gangster.game.model.protobuf.common.PBMission
import dev.gangster.game.model.protobuf.mission.PBMissionViewResponse
import dev.gangster.game.model.user.MafiaUserData
import kotlinx.coroutines.flow.firstOrNull

class MissionRepositoryMongo(private val data: MongoCollection<PlayerData>): MissionRepository {
    override suspend fun getMissionViewResponse(playerId: Int): Result<PBMissionViewResponse> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            data.find(filter)
                .firstOrNull()
                ?.missions
        }
    }

    override suspend fun updateMissionViewResponse(
        playerId: Int,
        viewResponse: PBMissionViewResponse
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("missions", viewResponse)

            val result = data.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify view missions response for playerId=$playerId with missions=$viewResponse")
            }

            Unit
        }
    }

    override suspend fun getMissions(playerId: Int): Result<List<PBMission>> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            data.find(filter)
                .firstOrNull()
                ?.missions
                ?.missions
        }
    }

    override suspend fun updateMissions(
        playerId: Int,
        missions: List<PBMission>
    ): Result<Unit> {
        return runMongoCatching {
            val filter = Filters.eq("playerId", playerId)
            val update = Updates.set("missions.missions", missions)

            val result = data.updateOne(filter, update)

            if (result.matchedCount != 1L) {
                throw NoSuchElementException("No player found with id=$playerId")
            }

            if (result.modifiedCount != 1L) {
                throw NoSuchElementException("Failed to modify missions.missions for playerId=$playerId with missions=${missions.joinToString("")}")
            }

            Unit
        }
    }
}
