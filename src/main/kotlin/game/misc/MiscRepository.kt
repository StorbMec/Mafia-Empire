package dev.gangster.game.misc

import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.user.MafiaUserData
import dev.gangster.game.model.user.PlayerInfo

/**
 * Repository for miscellaneous data in PlayerData
 */
interface MiscRepository {
    suspend fun getProfile(playerId: Int): Result<PBPlayerProfile>
    suspend fun updateProfile(playerId: Int, profile: PBPlayerProfile): Result<Unit>

    suspend fun getPlayerInfo(playerId: Int): Result<PlayerInfo>
    suspend fun updatePlayerInfo(playerId: Int, playerInfo: PlayerInfo): Result<Unit>

    suspend fun getMafiaUserData(playerId: Int): Result<MafiaUserData>
    suspend fun updateMafiaUserData(playerId: Int, mafiaUserData: MafiaUserData): Result<Unit>
}
