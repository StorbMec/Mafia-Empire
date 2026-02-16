package dev.gangster.game.ping

import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.user.MafiaPoliceData
import dev.gangster.game.model.user.MafiaProgressData

interface PingRepository {
    suspend fun getMissionProgress(playerId: Int): Result<MafiaProgressData>
    suspend fun updateMissionProgress(playerId: Int, progress: MafiaProgressData): Result<Unit>

    suspend fun getPoliceData(playerId: Int): Result<MafiaPoliceData>
    suspend fun updatePoliceData(playerId: Int, policeData: MafiaPoliceData): Result<Unit>

    suspend fun getNextDuelTime(playerId: Int): Result<Long>
    suspend fun updateNextDuelTime(playerId: Int, time: Long): Result<Unit>

    suspend fun getPlayerStatus(playerId: Int): Result<PBPlayerStatus>
    suspend fun updatePlayerStatus(playerId: Int, status: PBPlayerStatus): Result<Unit>
}
