package dev.gangster.game.model.protobuf.common

import kotlinx.serialization.Serializable

@Serializable
data class PBItem(
    val id: Int,
    val quality: PBItemQuality,
    val type: PBItemType,
    val costs: PBCosts,
    val isGoldPrice: Boolean,
    val levels: PBLevels,
    val subType: Int?,
    val attributes: PBAttributes?,
    val damage: PBDamage?,
    val shape: PBShape?,
    val duration: PBDuration?,
    val amount: Int?,
    val rounds: Int?,
    val effects: List<PBItemEffect>,
    val dependencies: List<PBWeaponType>,
    val charge: Int?,
) {
    companion object {
        fun dummyFood(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = PBItemQualityConstants.NORMAL,
                type = PBItemTypeConstants.FOOD,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = PBAttributes.newGame(),
                damage = null,
                shape = null,
                duration = PBDuration.dummy(),
                amount = 5,
                rounds = null,
                effects = listOf(PBItemEffect.dummy(id = 1)),
                dependencies = emptyList(),
                charge = null
            )
        }

        fun dummyWeapon(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = PBItemQualityConstants.NORMAL,
                type = PBItemTypeConstants.WEAPON,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = null,
                damage = PBDamage.dummy(),
                shape = PBShape.dummy(),
                duration = null,
                amount = 1,
                rounds = null,
                effects = emptyList(),
                dependencies = emptyList(),
                charge = null
            )
        }

        fun dummyAmmo(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = PBItemQualityConstants.NORMAL,
                type = PBItemTypeConstants.CONSUMABLE,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = null,
                damage = null,
                shape = PBShape.dummy(),
                duration = null,
                amount = 1,
                rounds = 140,
                effects = emptyList(),
                dependencies = listOf(PBWeaponTypeConstants.PISTOL),
                charge = null
            )
        }

        fun dummyGear(id: Int): PBItem {
            return PBItem(
                id = id,
                quality = PBItemQualityConstants.NORMAL,
                type = PBItemTypeConstants.GEAR,
                costs = PBCosts.dummy(),
                isGoldPrice = false,
                levels = PBLevels.dummy(),
                subType = 1,
                attributes = PBAttributes.newGame(),
                damage = null,
                shape = null,
                duration = null,
                amount = 1,
                rounds = null,
                effects = emptyList(),
                dependencies = emptyList(),
                charge = null
            )
        }

        /**
         * Give the 8 default gears to new player, where each stats are 3
         *
         * TO-DO update id and others
         */
        fun newGame(): List<PBItem> {
            val gear1 = PBItem.dummyGear(id = 1)
            val gear2 = PBItem.dummyGear(id = 2)
            val gear3 = PBItem.dummyGear(id = 3)
            val gear4 = PBItem.dummyGear(id = 4)
            val gear5 = PBItem.dummyGear(id = 5)
            val gear6 = PBItem.dummyGear(id = 6)
            val gear7 = PBItem.dummyGear(id = 7)
            val gear8 = PBItem.dummyGear(id = 8)
            return listOf(
                gear1, gear2, gear3, gear4, gear5, gear6, gear7, gear8
            )
        }
    }
}
