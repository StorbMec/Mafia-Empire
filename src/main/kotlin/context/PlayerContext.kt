package dev.gangster.context

import dev.gangster.game.data.collection.PlayerAccount
import dev.gangster.game.fight.FightService
import dev.gangster.game.misc.MiscService
import dev.gangster.game.mission.MissionService
import dev.gangster.game.ping.PingService
import dev.gangster.socket.core.Connection

data class PlayerContext(
    val playerId: Int,
    val connection: Connection,
    val onlineSince: Long,
    val playerAccount: PlayerAccount,
    val services: PlayerServices
)

data class PlayerServices(
    val misc: MiscService,
    val mission: MissionService,
    val ping: PingService,
    val fight: FightService
)
