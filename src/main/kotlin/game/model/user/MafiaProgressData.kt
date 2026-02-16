package dev.gangster.game.model.user

import kotlinx.serialization.Serializable

@Serializable
data class MafiaProgressData(
    val progress: Int,
    val missionTime: Int,
    val missionGiverId: Int,

    // contextual data. for mission, it selects the mission id. for travel, it selects the target city id
    val actionId: Int,
) {
    companion object {
        fun noMission(): MafiaProgressData {
            return MafiaProgressData(
                progress = 0,
                missionTime = 0,
                missionGiverId = 0,
                actionId = 1
            )
        }
    }
}

fun MafiaProgressData.toPngResponsePart(): String {
    return "$progress+$missionTime+$missionGiverId+$actionId"
}
