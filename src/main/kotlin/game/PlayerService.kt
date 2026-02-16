package dev.gangster.game

import dev.gangster.game.mission.MissionService

/**
 * Represents a player-scoped game service.
 *
 * This service manages data and domain logic related to a specific game domain for a particular player,
 * such as inventory or mission. It is not responsible for low-level database operations,
 * nor should it handle player identification on each operations. Instead, it stores domain data and provides
 * operations to callers.
 *
 * Typically, the service initializes local data through the [init] method.
 * It receives a repository specific to the domain (e.g., [MissionRepository]) to delegates the
 * low-level database work. Each repository is preferred to be wrapped in try-catch
 * and always return a Result<T> type. This is to ensure consistency on error handling across repository.
 *
 * Repository may define CRUD operations only, letting the service define the more complex operations.
 *
 * See examples: [MissionService]
 */
interface PlayerService {
    /**
     * Initializes the service for the specified [playerId].
     *
     * This method should be used to load or prepare all data related to the player
     * in this service's domain.
     *
     * @return An empty result just for denoting success or failure.
     */
    suspend fun init(playerId: Int): Result<Unit>

    /**
     * Closes the service for the specified [playerId].
     *
     * This method is called when the player logs off or disconnects.
     * It should synchronize any in-memory state with persistent storage
     * to ensure no progress or transient data is lost.
     *
     * @return An empty result just for denoting success or failure.
     */
    suspend fun close(playerId: Int): Result<Unit>
}
