package dev.gangster.task.impl

import dev.gangster.socket.core.Connection
import dev.gangster.task.ServerTask
import dev.gangster.task.TaskConfig
import dev.gangster.task.TaskScheduler
import dev.gangster.task.TaskTemplate
import dev.gangster.utils.Logger
import kotlin.time.Duration.Companion.seconds

/**
 * Task suitable for simple timer on server.
 * It will run once after the specified initialRunDelay has passed.
 * It does nothing when running (beside logging).
 * The purpose is solely making use of onComplete called by dispatcher after task is complete.
 * Usually not used to interact with client.
 */
class ServerTimerTask: ServerTask {
    override val key: TaskTemplate
        get() = TaskTemplate.Timer
    override val defaultConfig: TaskConfig
        get() = TaskConfig(
            targetTask = "",
            initialRunDelay = 0.seconds,
            repeatDelay = null,
            extra = emptyMap()
        )
    override val scheduler: TaskScheduler?
        get() = null

    override suspend fun run(connection: Connection, finalConfig: TaskConfig) {
        Logger.info { "ServerTimerTask has completed for: ${finalConfig.extra["info"]}" }
    }
}
