package dev.gangster.game.model.protobuf.equipment

import kotlinx.serialization.Serializable

/**
 * Example request: %xt%getarmamentpresetstatus%1%-1%CAIQARjQDw==%
 */
@Serializable
data class PBEquipmentGetArmamentPresetStatusResponse(
    val unlockedArmaments: Int,
    val activeArmament: Int,
    val unlockCost: Int,
) {
    companion object {
        fun newGame(): PBEquipmentGetArmamentPresetStatusResponse {
            return PBEquipmentGetArmamentPresetStatusResponse(
                unlockedArmaments = 2,
                activeArmament = 1,
                unlockCost = 500
            )
        }
    }
}
