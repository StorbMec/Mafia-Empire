package dev.gangster.game.model.protobuf.fight

import dev.gangster.game.model.protobuf.common.PBAttributes
import dev.gangster.game.model.protobuf.common.PBCharacterClass
import dev.gangster.game.model.protobuf.common.PBGender
import kotlinx.serialization.Serializable

@Serializable
data class PBFighter(
    val id: Int,
    val type: PBFighterType,
    val gender: PBGender,
    val characterClass: PBCharacterClass,
    val level: Int,
    val totalHp: Int,
    val currentHp: Int,
    val attributes: PBAttributes,
    val name: String?,
    val picString: String?
)
