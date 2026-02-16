package dev.gangster.game.model.protobuf.fight

import dev.gangster.game.model.protobuf.common.PBSide
import kotlinx.serialization.Serializable

@Serializable
data class PBFightTurn(
    val attacker: PBSide,
    val actions: List<PBFightAction>
)
