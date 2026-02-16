package dev.gangster.game.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBReward(
    val cash: Int,
    val gold: Int,
    val xp: Int,
    val activity: Int,
    val glory: Int,
    val item: PBItem?
)
