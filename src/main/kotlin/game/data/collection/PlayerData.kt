package dev.gangster.game.data.collection

import dev.gangster.game.data.AdminData
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarRequest
import dev.gangster.game.model.protobuf.common.PBAchievement
import dev.gangster.game.model.protobuf.common.PBItem
import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.model.protobuf.common.PBPlayerStatus
import dev.gangster.game.model.protobuf.common.PBPlayerStatusConstants
import dev.gangster.game.model.protobuf.equipment.*
import dev.gangster.game.model.protobuf.misc.PBMiscNewAchievementsResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPaymentInfoResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPlayerCurrencyResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPlayerProfileResponse
import dev.gangster.game.model.protobuf.mission.PBMissionBoosterGetPlayerBoosterBooster
import dev.gangster.game.model.protobuf.mission.PBMissionBoosterGetPlayerBoosterResponse
import dev.gangster.game.model.protobuf.mission.PBMissionBoosterShowMissionBoosterResponse
import dev.gangster.game.model.protobuf.mission.PBMissionViewResponse
import dev.gangster.game.model.protobuf.quest.PBQuestGetActiveQuestsResponse
import dev.gangster.game.model.protobuf.shop.PBShopViewItemsResponse
import dev.gangster.game.model.protobuf.work.PBWorkViewWorkResponse
import dev.gangster.game.model.response.LfeResponse
import dev.gangster.game.model.response.PngResponse
import dev.gangster.game.model.response.SaeResponse
import dev.gangster.game.model.user.*
import dev.gangster.game.model.vo.AchievementVO
import io.ktor.util.date.getTimeMillis

/**
 * Represents the player's game data. This model is a custom definition created by aggregating the game's data requested during APD.
 * It stores the information necessary to respond to APD.
 *
 * The order of fields is aligned with the APD response. Some static data, such as the price table and constants, are not included here.
 *
 * The model uses types that can typically be sent to the client or stored directly in the database.
 * This approach simplifies data handling without introducing redundant data models.
 * Although the models are prefixed with "PB," they aren't encoded in PB binary.
 * Some models may contain unnecessary data, such as duplicate player IDs and result status codes.
 *
 * For any data that has an intermediate model, there should be a `toResponse` or `toPayload` method to convert it into a response string.
 *
 */
data class PlayerData(
    val playerId: Int,

    /**
     * 10 achievements, id is 1-10 (see Mafia_en.xml).
     *
     * response: `achievements.map { toPayload() }`
     */
    val achievements: List<AchievementVO>,

    /**
     * response: `playerInfo.toPayload()`
     */
    val playerInfo: PlayerInfo,

    /**
     * response: [PBMiscPlayerProfileResponse]
     */
    val profile: PBPlayerProfile,

    /**
     * newly acquired achievements will trigger an alert upon login.
     *
     * achievements should be synced with newAchievements.
     * this data is redundant because used by the client to identify new achievements.
     *
     * response: [PBMiscNewAchievementsResponse]
     */
    val newAchievements: List<PBAchievement>,

    /**
     * response: directly.
     */
    val paymentInfo: PBMiscPaymentInfoResponse,

    /**
     * more detail related to level of player.
     *
     * response: `mafiaUserData.toOudResponse()`
     */
    val mafiaUserData: MafiaUserData,

    /**
     * response: directly.
     */
    val playerCurrency: PBMiscPlayerCurrencyResponse,

    /**
     * 1-4 armaments.
     *
     * response: directly but choose the active one from [armamentStatus].
     */
    val armaments: List<PBEquipmentViewArmamentResponse>,

    /**
     * response: directly
     */
    val armamentStatus: PBEquipmentGetArmamentPresetStatusResponse,

    /**
     * 0-8 gears.
     *
     * response: [PBEquipmentViewGearResponse]
     */
    val gears: List<PBItem>,

    /**
     * 0-3 foods.
     *
     * response: [PBEquipmentViewFoodResponse]
     */
    val foods: List<PBItem>,

    /**
     * response: directly.
     */
    val inventory: PBEquipmentViewInventoryResponse,

    /**
     * all three below response: directly.
     */
    val shopBlackMarket: PBShopViewItemsResponse,
    val shopConsumable: PBShopViewItemsResponse,
    val shopKiosk: PBShopViewItemsResponse,

    /**
     * response: [PBMissionBoosterGetPlayerBoosterResponse].
     */
    val boosters: List<PBMissionBoosterGetPlayerBoosterBooster>,

    /**
     * response: directly.
     */
    val missionBoosters: PBMissionBoosterShowMissionBoosterResponse,

    /**
     * response: directly.
     *
     * may need to refactor to split PlayerData from response
     */
    val missions: PBMissionViewResponse,

    /**
     * response: directly.
     */
    val work: PBWorkViewWorkResponse,

    /**
     * below are used for ping response.
     *
     * response: [PngResponse]
     */
    val playerStatus: PBPlayerStatus,
    val missionProgress: MafiaProgressData,
    val messages: List<String> = emptyList(), // TO-DO create PBMessage with new message flag
    val policeData: MafiaPoliceData,
    val nextDuel: Long,                       // next time player can duel in epoch millis

    /**
     * response: `specialEventData.toResponse()`
     */
    val specialEventData: SaeResponse,

    /**
     * response: `loginFeaturesData.toResponse()`
     */
    val loginFeaturesData: LfeResponse,

    /**
     * unused, response directly.
     */
    val paymentHash: String = "payment-hash-123",
    val forumHash: String = "forum-hash-123",

    /**
     * response: directly.
     *
     * change newQuestsAvailable appropriately.
     */
    val quests: PBQuestGetActiveQuestsResponse,

    /**
     * response: `gangs.toResponse()`
     */
    val gangs: MafiaGangData,
) {
    companion object {
        fun admin(): PlayerData {
            return PlayerData(
                playerId = AdminData.PLAYER_ID_NUMBER,
                achievements = AchievementVO.newGame(),
                playerInfo = PlayerInfo.tutorialNotCompleted(AdminData.EMAIL),
                profile = PBPlayerProfile.dummy(),
                newAchievements = emptyList(),
                paymentInfo = PBMiscPaymentInfoResponse.dummy(),
                mafiaUserData = MafiaUserData.dummy(),
                playerCurrency = PBMiscPlayerCurrencyResponse.dummy(),
                armaments = listOf(PBEquipmentViewArmamentResponse.dummy(AdminData.PLAYER_ID_NUMBER)),
                armamentStatus = PBEquipmentGetArmamentPresetStatusResponse.newGame(),
                gears = listOf(PBItem.dummyGear(1), PBItem.dummyGear(2), PBItem.dummyGear(3), PBItem.dummyGear(4)),
                foods = emptyList(),
                inventory = PBEquipmentViewInventoryResponse.empty(),
                shopBlackMarket = PBShopViewItemsResponse.dummyBlackMarket(),
                shopConsumable = PBShopViewItemsResponse.dummyConsumables(),
                shopKiosk = PBShopViewItemsResponse.dummyKiosk(),
                boosters = listOf(PBMissionBoosterGetPlayerBoosterBooster.bike()),
                missionBoosters = PBMissionBoosterShowMissionBoosterResponse.empty(),
                missions = PBMissionViewResponse.dummy(),
                work = PBWorkViewWorkResponse.dummy(),
                playerStatus = PBPlayerStatusConstants.FREE,
                missionProgress = MafiaProgressData.noMission(),
                messages = emptyList(),
                policeData = MafiaPoliceData.noPolice(),
                nextDuel = getTimeMillis(),
                specialEventData = SaeResponse.noEvent(),
                loginFeaturesData = LfeResponse.empty(),
                quests = PBQuestGetActiveQuestsResponse.dummy(),
                gangs = MafiaGangData.empty(),
            )
        }

        fun newGame(pid: Int, username: String, email: String, avatarData: PBCreateAvatarRequest): PlayerData {
            return PlayerData(
                playerId = pid,
                achievements = AchievementVO.newGame(),
                playerInfo = PlayerInfo.tutorialCompleted(email = email),
                profile = PBPlayerProfile.newGame(username, avatarData),
                newAchievements = emptyList(),
                paymentInfo = PBMiscPaymentInfoResponse(false, 0),
                mafiaUserData = MafiaUserData.newGame(avatarData.characterClass),
                playerCurrency = PBMiscPlayerCurrencyResponse.newGame(),
                armaments = PBEquipmentViewArmamentResponse.newGame(pid),
                armamentStatus = PBEquipmentGetArmamentPresetStatusResponse.newGame(),
                gears = PBItem.newGame(),
                foods = emptyList(),
                inventory = PBEquipmentViewInventoryResponse.newGame(),
                shopBlackMarket = PBShopViewItemsResponse.dummyBlackMarket(),
                shopConsumable = PBShopViewItemsResponse.dummyConsumables(),
                shopKiosk = PBShopViewItemsResponse.dummyKiosk(),
                boosters = emptyList(),
                missionBoosters = PBMissionBoosterShowMissionBoosterResponse.empty(),
                missions = PBMissionViewResponse.newGame(),
                work = PBWorkViewWorkResponse.noWork(),
                playerStatus = PBPlayerStatusConstants.FREE,
                missionProgress = MafiaProgressData.noMission(),
                messages = emptyList(),
                policeData = MafiaPoliceData.noPolice(),
                nextDuel = 0,
                specialEventData = SaeResponse.noEvent(),
                loginFeaturesData = LfeResponse.empty(),
                quests = PBQuestGetActiveQuestsResponse.newGame(),
                gangs = MafiaGangData.empty(),
            )
        }
    }
}
