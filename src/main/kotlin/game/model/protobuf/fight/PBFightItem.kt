package dev.gangster.game.model.protobuf.fight

import dev.gangster.game.model.protobuf.common.PBItemQuality
import dev.gangster.game.model.protobuf.common.PBItemType
import kotlinx.serialization.Serializable

@Serializable
data class PBFightItem(
    val type: PBItemType,
    val itemId: Int,
    val quality: PBItemQuality,
    val subtype: Int?
)
