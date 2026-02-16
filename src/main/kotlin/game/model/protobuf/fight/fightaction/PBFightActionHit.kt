package dev.gangster.game.model.protobuf.fight.fightaction

import dev.gangster.game.model.protobuf.fight.PBFightItem
import kotlinx.serialization.Serializable

@Serializable
data class PBFightActionHit(
    val weapon: PBFightItem,
    val result: PBFightActionHitResult,
    val firstDmg: Int?,
    val secondDmg: Int?,
    val critical: Boolean?
)
