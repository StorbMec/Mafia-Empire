package dev.gangster.game.model.protobuf.fight

import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionConsume
import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionEffect
import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionHit
import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionType
import kotlinx.serialization.Serializable

@Serializable
data class PBFightAction(
    val type: PBFightActionType,
    val hit: PBFightActionHit?,
    val consume: PBFightActionConsume?,
    val effect: PBFightActionEffect?
)
