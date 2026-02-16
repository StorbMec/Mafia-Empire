package dev.gangster.socket.handler.extension

import dev.gangster.context.GlobalContext
import dev.gangster.context.ServerContext
import dev.gangster.context.requirePlayerContext
import dev.gangster.game.data.collection.PlayerAccount
import dev.gangster.game.data.collection.PlayerData
import dev.gangster.game.model.components.AttributeCostsData
import dev.gangster.game.model.components.GoldConstantsData
import dev.gangster.game.model.components.toAucResponse
import dev.gangster.game.model.components.toPayload
import dev.gangster.game.model.constants.toCity
import dev.gangster.game.model.protobuf.common.missionTime
import dev.gangster.game.model.protobuf.equipment.PBEquipmentViewFoodResponse
import dev.gangster.game.model.protobuf.equipment.PBEquipmentViewGearResponse
import dev.gangster.game.model.protobuf.misc.PBMiscNewAchievementsResponse
import dev.gangster.game.model.protobuf.misc.PBMiscPlayerProfileResponse
import dev.gangster.game.model.protobuf.mission.PBMissionBoosterGetPlayerBoosterResponse
import dev.gangster.game.model.protobuf.shop.PBShopViewItemsResponse
import dev.gangster.game.model.response.PngResponse
import dev.gangster.game.model.response.SaeResponse
import dev.gangster.game.model.response.toResponse
import dev.gangster.game.model.user.MafiaGangData
import dev.gangster.game.model.user.toOudResponse
import dev.gangster.game.model.user.toPayload
import dev.gangster.game.model.user.toResponse
import dev.gangster.game.model.vo.toPayload
import dev.gangster.socket.core.Connection
import dev.gangster.socket.handler.MessageHandler
import dev.gangster.socket.message.XtConstants
import dev.gangster.socket.message.XtMessage
import dev.gangster.socket.message.XtMode
import dev.gangster.socket.protocol.SmartFoxString
import dev.gangster.task.TaskTemplate
import io.ktor.util.date.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.seconds

/**
 * apd or get all player data handler.
 *
 * It handles apd message by sending all player's data to client.
 * After all data is sent, it sent back the apd command.
 *
 * This is only used during game init, not intended to be used in the middle of gameplay.
 * Instead, you should use services class.
 *
 * Each expected data is separated to their own method (e.g., loadAchievements, loadShop),
 * where each method send message to client directly. The [handle] method should wait until all
 * method finishes.
 */
@OptIn(ExperimentalSerializationApi::class)
class XtAllPlayerDataHandler(private val serverContext: ServerContext) : MessageHandler<XtMessage> {
    override val priority: Int
        get() = 99 // last

    private var reqId: Int = 1
    private lateinit var connection: Connection
    private val SUCCESS = 0 // typically PB responses do not have status code
    private lateinit var playerData: PlayerData
    private lateinit var playerAccount: PlayerAccount

    override fun match(message: XtMessage): Boolean {
        return message.command == XtConstants.COMMAND_ALL_PLAYER_DATA
    }

    override suspend fun handle(
        connection: Connection,
        message: XtMessage
    ) {
        // Initialize local variables instead of passing them to each method.
        this.connection = connection
        this.reqId = message.reqId

        // Use data directly from DB to send to client
        this.playerAccount = serverContext.db.loadPlayerAccount(connection.playerId).getOrThrow()
        this.playerData = serverContext.db.loadPlayerData(connection.playerId).getOrThrow()

        // load each data and send them to client
        // TO-DO create handler for each data instead of loading here
        // use priority to ensure this handler which respond apd is last, after everything else
        loadAchievements()
        loadGoldConstantsData()
        loadNewAchievements()
        loadPaymentInfo()
        loadPlayerCurrency()
        loadArmament()
        loadArmamentPresetStatus()
        loadGear()
        loadFood()
        loadInventory()
        loadShopItems()
        loadAttributeCostsData()
        loadPlayerBooster()
        loadMissionBooster()
        loadWork()
        loadSpecialEventData()
        loadLoginFeaturesData()
        loadPaymentHash()
        loadForumHash()
        loadActiveQuests()
        loadGangShop()
        loadPlayerGang()

        // send apd (ready message)
        val apdXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_ALL_PLAYER_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
        )
        connection.sendRaw(apdXtResponse)
    }

    /**
     * OGA, also known as avatar achievements, which load current player's achievements.
     */
    private suspend fun loadAchievements() {
        val ogaRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_AVATAR_ACHIEVEMENTS,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            connection.playerId, // can change to other's playerId
            playerData.achievements.toPayload()
        )
        connection.sendRaw(ogaRes)
    }

    /**
     * SGC, also known as gold constants data, corresponds to game's gold prices.
     */
    private suspend fun loadGoldConstantsData() {
        val sgcRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GOLD_CONSTANTS_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            GoldConstantsData().toPayload() // TO-DO make table
        )
        connection.sendRaw(sgcRes)
    }


    /**
     * newachievements, which load player's newly obtained achievements (which triggered when they are offline)
     */
    private suspend fun loadNewAchievements() {
        val pbResponse = PBMiscNewAchievementsResponse(achievements = playerData.newAchievements)
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_NEW_ACHIEVEMENTS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * paymentinfo, which load player's payment information.
     *
     * not planning to implement.
     */
    private suspend fun loadPaymentInfo() {
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PAYMENT_INFO,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.paymentInfo))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * playercurrency, which loads player's resources like gold and cash.
     */
    private suspend fun loadPlayerCurrency() {
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_CURRENCY,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.playerCurrency))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewarmament, which loads player's armament or preset of equipment.
     */
    private suspend fun loadArmament() {
        val usedArmamentIdx = playerData.armamentStatus.activeArmament - 1
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ARMAMENT,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.armaments[usedArmamentIdx]))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * getarmamentpresetstatus, which loads preset status of player's equipment.
     */
    private suspend fun loadArmamentPresetStatus() {
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GET_ARMAMENT_PRESET_STATUS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.armamentStatus))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewgear, which loads player's gear (the 8 items that increases player's attributes)
     */
    private suspend fun loadGear() {
        val pbResponse = PBEquipmentViewGearResponse(connection.playerId, playerData.gears)
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_GEAR,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewfood, which loads player's active food as boosters.
     */
    private suspend fun loadFood() {
        val pbResponse = PBEquipmentViewFoodResponse(connection.playerId, playerData.foods)
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_FOOD,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewinventory, which loads player's inventory.
     */
    private suspend fun loadInventory() {
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_INVENTORY,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.inventory))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * viewitems, which loads shop items (black market, consumable, kiosk)
     */
    private suspend fun loadShopItems() {
        val blackMarketPbResponse = PBShopViewItemsResponse.dummyBlackMarket() // TO-DO make table
        val blackMarketXtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ITEMS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(blackMarketPbResponse))
        )
        connection.sendRaw(blackMarketXtRes)

        val consumablePbResponse = PBShopViewItemsResponse.dummyConsumables() // TO-DO make table
        val consumableXtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ITEMS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(consumablePbResponse))
        )
        connection.sendRaw(consumableXtRes)

        val kioskPbResponse = PBShopViewItemsResponse.dummyKiosk() // TO-DO make table
        val kioskXtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_ITEMS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(kioskPbResponse))
        )
        connection.sendRaw(kioskXtRes)
    }

    /**
     * auc, which loads attributes (e.g., attack power, endurance) costs data.
     */
    private suspend fun loadAttributeCostsData() {
        val aucXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_ATTRIBUTE_COSTS_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            AttributeCostsData().toAucResponse() // TO-DO make table
        )
        connection.sendRaw(aucXtResponse)
    }

    /**
     * getplayerbooster, which loads player's booster.
     */
    private suspend fun loadPlayerBooster() {
        val pbResponse =
            PBMissionBoosterGetPlayerBoosterResponse(
                result = 1,
                playerId = connection.playerId,
                boosters = playerData.boosters
            )
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GET_PLAYER_BOOSTER,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(pbResponse))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * showmissionbooster, which loads mission booster.
     *
     * kind of unsure the difference with getplayerbooster.
     */
    private suspend fun loadMissionBooster() {
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_SHOW_MISSION_BOOSTER,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.missionBoosters))
        )
        connection.sendRaw(xtRes)
    }


    /**
     * viewwork, which loads player's work progress.
     */
    private suspend fun loadWork() {
        val xtRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_VIEW_WORK,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.work))
        )
        connection.sendRaw(xtRes)
    }

    /**
     * sae, which loads special event data.
     */
    private suspend fun loadSpecialEventData() {
        val saeData = SaeResponse.noEvent()
        val saeXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_SPECIAL_EVENT_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *saeData.toResponse().toTypedArray()
        )
        connection.sendRaw(saeXtResponse)
    }

    /**
     * lfe, which loads login features data.
     *
     * unsure what features are.
     */
    private suspend fun loadLoginFeaturesData() {
        val lfeXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_LOGIN_FEATURES_DATA,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *playerData.loginFeaturesData.toResponse().toTypedArray()
        )
        connection.sendRaw(lfeXtResponse)
    }

    /**
     * gch, which generate payment hash (possibly unique code for payment).
     *
     * no need to implement.
     */
    private suspend fun loadPaymentHash() {
        val gchXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PAYMENT_HASH,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            playerData.paymentHash
        )
        connection.sendRaw(gchXtResponse)
    }

    /**
     * gfl, which generate crypted forum hash.
     *
     * no need to implement.
     */
    private suspend fun loadForumHash() {
        val gflXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_CRYPTED_FORUM_HASH,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            playerData.forumHash
        )
        connection.sendRaw(gflXtResponse)
    }

    /**
     * getactivequests, which loads player's active quests.
     */
    private suspend fun loadActiveQuests() {
        val getActiveQuestsRes = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GET_ACTIVE_QUESTS,
            reqId = reqId,
            statusCode = null,
            mode = XtMode.Protobuf,
            Base64.encode(GlobalContext.pb.encodeToByteArray(playerData.quests)) // TO-DO make table
        )
        connection.sendRaw(getActiveQuestsRes)
    }

    /**
     * sgs, which loads game's gang shop, place where you hire one of five gang in character panel.
     */
    private suspend fun loadGangShop() {
        val sgsData = MafiaGangData.empty() // TO-DO make table
        val sgsXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_GANG_SHOP,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            *sgsData.toResponse().toTypedArray()
        )
        connection.sendRaw(sgsXtResponse)
    }

    /**
     * sga, which loads gang owned by player.
     */
    private suspend fun loadPlayerGang() {
        val sgaXtResponse = SmartFoxString.makeXt(
            command = XtConstants.COMMAND_PLAYER_GANG,
            reqId = reqId,
            statusCode = SUCCESS,
            mode = XtMode.Nothing,
            connection.playerId,
            *playerData.gangs.toResponse().toTypedArray()
        )
        connection.sendRaw(sgaXtResponse)
    }
}
