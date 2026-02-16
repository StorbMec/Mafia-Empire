package dev.gangster.context

import dev.gangster.game.data.GameDefinitions
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
object GlobalContext {
    lateinit var json: Json
        private set
    lateinit var pb: ProtoBuf
        private set
    lateinit var gameDefinitions: GameDefinitions
        private set

    fun init(json: Json, pb: ProtoBuf, gameDefinitions: GameDefinitions) {
        this.json = json
        this.pb = pb
        this.gameDefinitions = gameDefinitions
    }
}
