package dev.gangster.game.model.protobuf.common

import kotlin.random.Random

typealias PBCharacterClass = Int

object PBCharacterClassConstants {
    const val BULLY = 1
    const val ROGUE = 2
    const val TACTICIAN = 3

    fun random(): Int {
        return Random.nextInt(1, 3)
    }
}
