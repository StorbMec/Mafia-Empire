package dev.gangster.game.model.request

import kotlinx.serialization.Serializable

@Serializable
data class OudRequest(
    val playerId: Int
)
