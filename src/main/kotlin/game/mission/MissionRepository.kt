package dev.gangster.game.mission

import dev.gangster.game.model.protobuf.common.PBMission
import dev.gangster.game.model.protobuf.mission.PBMissionViewResponse

interface MissionRepository {
    suspend fun getMissionViewResponse(playerId: Int): Result<PBMissionViewResponse>
    suspend fun updateMissionViewResponse(playerId: Int, viewResponse: PBMissionViewResponse): Result<Unit>

    suspend fun getMissions(playerId: Int): Result<List<PBMission>>
    suspend fun updateMissions(playerId: Int, missions: List<PBMission>): Result<Unit>
}
