package dev.gangster.game.model.protobuf.fight

import dev.gangster.game.model.protobuf.common.PBSide
import kotlinx.serialization.Serializable

@Serializable
data class PBFight(
    val leftFighter: PBFighter,
    val rightFighter: PBFighter,
    val winner: PBSide,
    val turns: List<PBFightTurn>,
    val leftAmmunition: List<PBFightItem>,
    val rightAmmunition: List<PBFightItem>
)
