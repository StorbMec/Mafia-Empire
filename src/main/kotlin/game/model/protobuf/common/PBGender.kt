package dev.gangster.game.model.protobuf.common

import dev.gangster.utils.toInt
import kotlin.random.Random

typealias PBGender = Int

object PBGenderConstants {
    const val MALE = 1
    const val FEMALE = 2

    fun random(): Int {
        return Random.nextBoolean().toInt()
    }
}
