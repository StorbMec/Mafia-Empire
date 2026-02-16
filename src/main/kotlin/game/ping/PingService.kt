package dev.gangster.game.ping

import dev.gangster.game.PlayerService
import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.model.user.MafiaPoliceData
import dev.gangster.game.model.user.MafiaProgressData
import dev.gangster.utils.Logger
import io.ktor.util.date.getTimeMillis
import kotlin.math.min
import kotlin.runCatching
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class PingService(private val pingRepository: PingRepository) : PlayerService {
    private var playerId: Int = 0
    private lateinit var missionProgress: MafiaProgressData
    private lateinit var policeData: MafiaPoliceData
    private var nextDuelTime: Long = 0
    private var playerStatus: PBPlayerStatus = PBPlayerStatusConstants.FREE
    private var doingMissionSince: Long? = null

    fun getMissionProgress() = missionProgress
    fun getPoliceData() = policeData
    fun getNextDuelTime() = nextDuelTime
    fun getPlayerStatus() = playerStatus

    suspend fun updatePlayerStatus(status: PBPlayerStatus) {
        val result = pingRepository.updatePlayerStatus(playerId, status)
        result.onFailure {
            Logger.error { "Error on updatePlayerStatus: ${it.message}" }
        }
        result.onSuccess {
            this.playerStatus = status
        }
    }

    suspend fun updateMissionProgress(updateAction: suspend (MafiaProgressData) -> MafiaProgressData) {
        val update = updateAction(missionProgress)
        val result = pingRepository.updateMissionProgress(playerId, update)
        result.onFailure {
            Logger.error { "Error on updateMissionProgress: ${it.message}" }
        }
        result.onSuccess {
            this.missionProgress = update
        }
    }

    /**
     * Update the mission timer with [progress] in seconds.
     */
    suspend fun updateMissionTimer(progress: Int) {
        updateMissionProgress {
            it.copy(
                progress = progress,
                missionTime = missionProgress.missionTime
            )
        }
    }

    /**
     * Reflect mission timer progress to the present time, according to [lastLogin].
     */
    suspend fun updateMissionProgressFromOffline(lastLogin: Long) {
        val now = getTimeMillis().milliseconds.inWholeSeconds
        val timePassedInSeconds = (now - lastLogin).milliseconds.inWholeSeconds
        doingMissionSince =
            (lastLogin.milliseconds.inWholeSeconds - missionProgress.progress.seconds.inWholeSeconds)
                .milliseconds
                .toLong(DurationUnit.MILLISECONDS)
        updateMissionProgress {
            it.copy(
                progress = (missionProgress.progress + timePassedInSeconds).toInt(),
                missionTime = missionProgress.missionTime
            )
        }
    }

    /**
     * Advance mission progress by [deltaSeconds] while the player is online.
     * Typically called every second from the PingTask.
     */
    suspend fun tickMissionProgress(deltaSeconds: Int = 1) {
        if (playerStatus != PBPlayerStatusConstants.MISSION) return

        updateMissionProgress {
            val newProgress = (missionProgress.progress + deltaSeconds)
                .coerceAtMost(missionProgress.missionTime)
            it.copy(progress = newProgress)
        }
    }

    suspend fun signalStartMission(startTime: Long, missionId: Int, missionTime: Int, missionGiver: Int) {
        doingMissionSince = startTime
        updatePlayerStatus(PBPlayerStatusConstants.MISSION)
        updateMissionProgress {
            MafiaProgressData(
                progress = 0,
                missionTime = missionTime,
                missionGiverId = missionGiver,
                actionId = missionId
            )
        }
    }

    suspend fun signalMissionComplete() {
        updateMissionTimer(missionProgress.missionTime)
        doingMissionSince = null
        updatePlayerStatus(PBPlayerStatusConstants.FINISH_MISSION)
    }

    /**
     * Whether mission was finished while player is logged off.
     */
    fun didMissionFinish(lastLogin: Long): Boolean {
        val now = getTimeMillis()
        val timePassedInSeconds = (now - lastLogin).milliseconds.inWholeSeconds
        return missionProgress.progress + timePassedInSeconds >= missionProgress.missionTime
    }

    override suspend fun init(playerId: Int): Result<Unit> {
        return runCatching {
            this.playerId = playerId
            this.missionProgress = pingRepository.getMissionProgress(playerId).getOrThrow()
            this.policeData = pingRepository.getPoliceData(playerId).getOrThrow()
            this.nextDuelTime = pingRepository.getNextDuelTime(playerId).getOrThrow()
            this.playerStatus = pingRepository.getPlayerStatus(playerId).getOrThrow()
        }
    }

    override suspend fun close(playerId: Int): Result<Unit> {
        return runCatching {
            if (doingMissionSince != null) {
                // player was doing mission
                val start = doingMissionSince?.milliseconds?.inWholeSeconds
                val now = getTimeMillis().milliseconds.inWholeSeconds
                val missionTime = missionProgress.missionTime
                start?.let {
                    val timePassedInSeconds = (now - it).toInt()
                    if (timePassedInSeconds >= missionTime) {
                        // mission completed
                        updatePlayerStatus(PBPlayerStatusConstants.FINISH_MISSION)
                    }
                    // update timer: if mission is not complete it would be timePassedInSeconds
                    updateMissionTimer(min(missionTime, timePassedInSeconds))
                }
            }
        }
    }
}
