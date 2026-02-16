package dev.gangster.game.model.protobuf.fight.fightaction

import dev.gangster.game.model.protobuf.fight.PBFightItem
import kotlinx.serialization.Serializable

@Serializable
data class PBFightActionConsume(
    val item: PBFightItem,
    val damage: Int?,
    val heal: Int?
)
