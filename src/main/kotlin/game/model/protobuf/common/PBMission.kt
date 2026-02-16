package dev.gangster.game.model.protobuf.common

import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
data class PBMission(
    // mission id is to refer mission title and text (cosmetic purpose only)
    // it's guarded by modulo in client-side, so its valid to send any number
    val id: Int,
    val cash: Int,
    val xp: Int,
    val activity: Int,
    val time: Int,
    val opponentClass: PBCharacterClass,
    val item: PBItem?
) {
    companion object {
        fun dummy(id: Int = Random.nextInt(1, 25)): PBMission {
            return PBMission(
                id = id,
                cash = 1234,
                xp = 4321,
                activity = 23,
                time = 10,
                opponentClass = PBCharacterClassConstants.BULLY,
                item = null,
            )
        }
    }
}

fun PBMission.toFightReward(gold: Int): PBReward {
    return PBReward(
        cash = cash,
        gold = gold,
        xp = xp,
        activity = activity,
        glory = 12, // TO-DO unknown what for
        item = item
    )
}

/**
 * Convert mission time in seconds to milliseconds of duration type
 */
fun PBMission.missionTime(): Duration {
    return this.time.seconds.inWholeMilliseconds.toDuration(DurationUnit.MILLISECONDS)
}
