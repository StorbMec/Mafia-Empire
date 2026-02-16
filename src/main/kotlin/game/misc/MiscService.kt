package dev.gangster.game.misc

import dev.gangster.game.PlayerService
import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.model.user.MafiaUserData
import dev.gangster.game.model.user.PlayerInfo

class MiscService(private val miscRepository: MiscRepository): PlayerService {
    private var playerId: Int = 0
    private lateinit var profile: PBPlayerProfile
    private lateinit var mafiaUserData: MafiaUserData
    private lateinit var playerInfo: PlayerInfo

    fun getProfile(): PBPlayerProfile = profile
    fun getMafiaUserData(): MafiaUserData = mafiaUserData
    fun getPlayerInfo(): PlayerInfo = playerInfo

    fun getOtherMafiaUserData(playerId: Int): MafiaUserData {
        // TO-DO returns other
        return mafiaUserData
    }

    override suspend fun init(playerId: Int): Result<Unit> {
        return runCatching {
            this.playerId = playerId
            this.profile = miscRepository.getProfile(playerId).getOrThrow()
            this.playerInfo = miscRepository.getPlayerInfo(playerId).getOrThrow()
            this.mafiaUserData = miscRepository.getMafiaUserData(playerId).getOrThrow()
        }
    }

    override suspend fun close(playerId: Int): Result<Unit> {
        return Result.success(Unit)
    }
}
