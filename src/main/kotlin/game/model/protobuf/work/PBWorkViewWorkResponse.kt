package dev.gangster.game.model.protobuf.work

import kotlinx.serialization.Serializable

/**
 * for viewwork command
 *
 * example: %xt%viewwork%1%-1%CAEQ6AcYAA==%
 *
 * result: ok = 1, busy = 2
 */
@Serializable
data class PBWorkViewWorkResponse(
    val result: Int = 1,
    val moneyPerHour: Int,
    val currentWorkDuration: Int,
) {
    companion object {
        fun dummy(): PBWorkViewWorkResponse {
            return PBWorkViewWorkResponse(
                result = 1,
                moneyPerHour = 345,
                currentWorkDuration = 980
            )
        }

        // result code not sure
        fun noWork(): PBWorkViewWorkResponse {
            return PBWorkViewWorkResponse(
                result = 2,
                moneyPerHour = 0,
                currentWorkDuration = 0
            )
        }
    }
}
