package dev.gangster.game.mission

import dev.gangster.game.PlayerService
import dev.gangster.game.model.protobuf.common.PBMission
import dev.gangster.game.model.protobuf.mission.PBMissionViewResponse

class MissionService(private val missionRepository: MissionRepository): PlayerService {
    private var playerId: Int = 0
    private val missions = mutableListOf<PBMission>()
    // may need to refactor to split PlayerData from response
    private lateinit var missionViewResponse: PBMissionViewResponse

    fun getMissionById(missionId: Int): Pair<Int, PBMission?> {
        val idx = this.missions.indexOfFirst { it.id == missionId }
        return idx to this.missions.getOrNull(idx)
    }

    fun getAllMissions(): List<PBMission> = missions

    fun getMissionResponse(): PBMissionViewResponse = missionViewResponse

    override suspend fun init(playerId: Int): Result<Unit> {
        return runCatching {
            this.playerId = playerId
            this.missions.addAll(missionRepository.getMissions(playerId).getOrThrow())
            this.missionViewResponse = missionRepository.getMissionViewResponse(playerId).getOrThrow()
        }
    }

    override suspend fun close(playerId: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
