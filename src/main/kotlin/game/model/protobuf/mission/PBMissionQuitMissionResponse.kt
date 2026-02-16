package dev.gangster.game.model.protobuf.mission

import dev.gangster.game.model.protobuf.common.PBReward
import dev.gangster.game.model.protobuf.fight.PBFight
import kotlinx.serialization.Serializable

/**
 * result:
 *   OK = 1 (continue to fight)
 *   NOT_ENOUGH_GOLD = 2
 *   CHECK_STATUS = 3
 *   INVALID_MISSION = 4 (presumably means abandon mission)
 */
@Serializable
data class PBMissionQuitMissionResponse(
    val result: Int,
    val fight: PBFight?,
    val reward: PBReward?
)
