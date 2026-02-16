package dev.gangster.game.model.user

import dev.gangster.utils.toInt
import kotlinx.serialization.Serializable

/**
 * OIO (player info)
 * [example: %xt%oio%1%0%anemail@email.com%0%1%]
 *
 * structure is <email>%<email_verified>%<tutorial_completed>
 */
@Serializable
data class PlayerInfo(
    val email: String,
    val emailVerified: Boolean = false,
    val tutorialCompleted: Boolean = false,
) {
    companion object {
        fun tutorialCompleted(email: String, verified: Boolean = false): PlayerInfo {
            return PlayerInfo(
                email = email,
                emailVerified = verified,
                tutorialCompleted = true
            )
        }

        fun tutorialNotCompleted(email: String, verified: Boolean = false): PlayerInfo {
            return PlayerInfo(
                email = email,
                emailVerified = verified,
                tutorialCompleted = false
            )
        }
    }
}

fun PlayerInfo.toPayload(): String {
    return "$email%${emailVerified.toInt()}%${tutorialCompleted.toInt()}"
}
