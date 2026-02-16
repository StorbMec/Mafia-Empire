package dev.gangster.game.model.protobuf.equipment

import dev.gangster.game.model.protobuf.common.PBItemSlot
import kotlinx.serialization.Serializable

/**
 * example request: %xt%viewarmament%1%-1%CLDkpQIQCBgFInUIABAAGAEibQgxEAQYAyIHCOKcAhCVSSgBMgUIABCPTjgDSgUIURCYAVIECAQQAnIMCCMVcT2qPx0AAAAAcgwIJBWamRm+HQrXIzxyDAgkFZqZGT4dj8L1PHIMCBUVj8L1PB0AAAAAcgwIBxWPwvU8HQAAAAAiMwgFEAAYASIrCAUQARgFIgYIoB8QiA4oADIFCAgQj05SBAgDEANyDAgFFc3MzD0dAAAAACIzCAAQAhgBIisIBxABGAYiBgigHxCIDigAMgUICBCPTlIECAMQA3IMCAUVCtejPR0AAAAAIjMIBRADGAEiKwgCEAEYBiIGCNgEEJAcKAEyBQgAEI9OUgQIAxACcgwICRWPwvU9HQAAAAAoADgTQBY=%
 */
@Serializable
data class PBEquipmentViewArmamentResponse(
    val playerId: Int,
    val width: Int,
    val height: Int,
    val itemSlots: List<PBItemSlot>,
    val isUnlockable: Boolean,
    val unlockCost: Int?,
    val nextPaidUnlockLevel: Int?,
    val nextFreeUnlockLevel: Int?,
) {
    companion object {
        fun dummy(pid: Int): PBEquipmentViewArmamentResponse {
            return PBEquipmentViewArmamentResponse(
                playerId = pid,
                width = 12,
                height = 6,
                itemSlots = emptyList(),
                isUnlockable = false,
                unlockCost = null,
                nextPaidUnlockLevel = null,
                nextFreeUnlockLevel = 4
            )
        }

        /**
         * TO-DO new game information
         *
         * first armament is the default use
         * second armament is empty preset
         */
        fun newGame(pid: Int): List<PBEquipmentViewArmamentResponse> {
            return listOf(
                PBEquipmentViewArmamentResponse(
                    playerId = pid,
                    width = 12,
                    height = 6,
                    itemSlots = emptyList(),
                    isUnlockable = false,
                    unlockCost = null,
                    nextPaidUnlockLevel = null,
                    nextFreeUnlockLevel = 4
                ),
                PBEquipmentViewArmamentResponse(
                    playerId = pid,
                    width = 12,
                    height = 6,
                    itemSlots = emptyList(),
                    isUnlockable = false,
                    unlockCost = null,
                    nextPaidUnlockLevel = null,
                    nextFreeUnlockLevel = 4
                )
            )
        }


    }
}
