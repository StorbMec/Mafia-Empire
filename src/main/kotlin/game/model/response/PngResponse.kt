package dev.gangster.game.model.response

import dev.gangster.game.model.constants.City
import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.model.user.MafiaClanData
import dev.gangster.game.model.user.MafiaPoliceData
import dev.gangster.game.model.user.MafiaProgressData
import dev.gangster.game.model.user.toPngResponsePart
import kotlinx.serialization.Serializable

/**
 * png (ping) response
 *
 * must encode [progressData] and [policeData] with `toPngResponsePart`
 *          %xt%png%1%0%2%0%270+0+1+72%0%0+0+0+0%-29360402%%
 * example: %xt%png%1%0%0%0%0+0+0+0%0%0+0+0+0%-28421048%%
 */
@Serializable
data class PngResponse(
    val playerStatus: PBPlayerStatus,
    val city: City,
    val progressData: MafiaProgressData,
    val messageDataAmount: Int,     // see PBMessageShowNewsMessage for actual message
    val policeData: MafiaPoliceData,
    val duelCooldown: Int,          // below or equal to 0 means no cooldown
    val clanStatus: MafiaClanData?, // no clan = null
) {
    companion object {
        fun empty(): PngResponse {
            return PngResponse(
                playerStatus = PBPlayerStatusConstants.FREE,
                city = City.NewYork,
                progressData = MafiaProgressData.noMission(),
                messageDataAmount = 0,
                policeData = MafiaPoliceData.noPolice(),
                duelCooldown = 0,
                clanStatus = null
            )
        }
    }
}

fun PngResponse.toResponse(): List<Any?> {
    return listOf(
        playerStatus,
        city.ordinal,
        progressData.toPngResponsePart(),
        messageDataAmount,
        policeData.toPngResponsePart(),
        duelCooldown,
        clanStatus?.toPngResponsePart()
    )
}
