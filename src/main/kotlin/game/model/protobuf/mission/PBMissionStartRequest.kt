package dev.gangster.game.model.protobuf.mission

import kotlinx.serialization.Serializable

@Serializable
data class PBMissionStartRequest(
    val missionNumber: Int,
)