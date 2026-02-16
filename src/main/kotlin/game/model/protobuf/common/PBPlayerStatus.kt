package dev.gangster.game.model.protobuf.common

typealias PBPlayerStatus = Int

object PBPlayerStatusConstants {
    const val FREE = 1
    const val MISSION = 2
    const val FINISH_MISSION = 3
    const val WORKING = 4
    const val FINISH_WORKING = 5
    const val FLYING = 6
    const val FINISH_FLYING = 7
}
