package dev.gangster.task

import dev.gangster.socket.core.Connection
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class DefaultTaskScheduler: TaskScheduler {
    override suspend fun schedule(
        task: ServerTask,
        connection: Connection,
        cfg: TaskConfig
    ) {
        delay(cfg.initialRunDelay)

        val shouldRunInfinitely = cfg.repeatDelay != null
        if (shouldRunInfinitely) {
            while (currentCoroutineContext().isActive) {
                delay(cfg.repeatDelay!!)
                task.run(connection, cfg)
            }
        } else {
            task.run(connection, cfg)
        }
    }
}
