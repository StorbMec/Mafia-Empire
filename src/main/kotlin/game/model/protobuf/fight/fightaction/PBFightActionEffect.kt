package dev.gangster.game.model.protobuf.fight.fightaction

import dev.gangster.game.model.protobuf.common.PBSide
import kotlinx.serialization.Serializable

@Serializable
data class PBFightActionEffect(
    val id: Int,
    val duration: Int,
    val target: PBSide,
    val value: Float?,
)
