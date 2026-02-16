package dev.gangster.game.model.protobuf.mission

import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import kotlinx.serialization.Serializable

/**
 *
 * used for startmission
 * result:
 *
 * ok = 1
 * player_busy = 2
 * not_enough_energy = 3
 * mission_not_available = 4
 */
@Serializable
data class PBMissionStartResponse(
    val result: Int,
    val playerStatus: PBPlayerStatus?,
    val progressTime: Int?,
    val remainingTime: Int?,
    val missionGiver: Int?,
    val missionId: Int?
)
