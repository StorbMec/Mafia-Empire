package dev.gangster.game.model.protobuf.common

import dev.gangster.game.data.AdminData
import dev.gangster.game.data.RandomData
import dev.gangster.game.model.protobuf.avatar.PBCreateAvatarRequest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * playerprofile command
 * [example: %xt%playerprofile%1%-1%CAEQsOSlAiKCAQoKSmVubnlIYXplXxACGikyITB+Mn4wfjJ+MX4yfjB+MCEwfjN+Mn4wfjR+NH4wfjJ+M34wfjJ+NiACKgoIlQIQ5gIYQSBaMhYIwg0QzMwBHRsbP0ElA7A0QS0AAMhCSg8KBQgeEPZEEgYIpDcQ3XBYtwFgTmgAcBJ4AYgB/////wc=%]
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PBPlayerProfile(
    @ProtoNumber(1) val name: String,
    @ProtoNumber(2) val gender: PBGender,
    @ProtoNumber(3) val picString: String,
    @ProtoNumber(4) val characterClass: PBCharacterClass,
    @ProtoNumber(5) val attributes: PBAttributes,
    @ProtoNumber(6) val combatStats: PBCombatStats,
    @ProtoNumber(9) val highscore: PBHighscore, // 7, 8 is skipped in client
    @ProtoNumber(11) val wonDuels: Int,
    @ProtoNumber(12) val lostDuels: Int,
    @ProtoNumber(13) val isIgnored: Boolean,
    @ProtoNumber(14) val level: Int,
    @ProtoNumber(15) val cityId: PBCity,
    @ProtoNumber(16) val clanName: String?,
    @ProtoNumber(17) val clanId: Int?,
    @ProtoNumber(18) val rankInClan: PBFamilyRank?,
) {
    companion object {
        fun dummy(): PBPlayerProfile {
            return PBPlayerProfile(
                name = AdminData.USERNAME,
                gender = AdminData.GENDER,
                picString = RandomData.randomMalePortrait(),
                characterClass = AdminData.CHARACTER_CLASS,
                attributes = PBAttributes.hacker(),
                combatStats = PBCombatStats.dummy(),
                highscore = PBHighscore.dummy(),
                wonDuels = 0,
                lostDuels = 0,
                isIgnored = false,
                level = 1,
                cityId = PBCityConstants.NEW_YORK,
                clanName = null,
                clanId = null,
                rankInClan = null
            )
        }

        fun newGame(username: String, avatarData: PBCreateAvatarRequest): PBPlayerProfile {
            return PBPlayerProfile(
                name = username,
                gender = avatarData.gender,
                picString = avatarData.portrait,
                characterClass = avatarData.characterClass,
                attributes = PBAttributes.newGame(),
                combatStats = PBCombatStats.newGame(avatarData.characterClass),
                highscore = PBHighscore.dummy(), // TO-DO create global highscore service
                wonDuels = 0,
                lostDuels = 0,
                isIgnored = false,
                level = 1,
                cityId = PBCityConstants.NEW_YORK,
                clanName = null,
                clanId = null,
                rankInClan = null
            )
        }

        /**
         * TO-DO enhance generation
         * make separate generation function that is relative to level instead of player
         */
        fun generateProfileRelativeToPlayer(
            name: String? = null,
            gender: PBGender? = null,
            picString: String? = null,
            characterClass: PBCharacterClass? = null,
            attributes: PBAttributes? = null,
            combatStats: PBCombatStats? = null,
            highscore: PBHighscore? = null,
            wonDuels: Int? = null,
            lostDuels: Int? = null,
            isIgnored: Boolean? = null,
            level: Int? = null,
            cityId: Int? = null,
            clanName: String? = null,
            clanId: Int? = null,
            rankInClan: Int? = null,

            // scaling relative to player
            difficulty: Difficulty = Difficulty.EASY,
            playerProfile: PBPlayerProfile,
        ): PBPlayerProfile {
            val chosenClass = characterClass ?: PBCharacterClassConstants.random()
            val baseAttributes = attributes
                ?: PBAttributes.randomRelativeToPlayer(playerProfile.attributes, chosenClass, difficulty)
            val stats = combatStats ?: baseAttributes.toCombatStats(
                weapon = 3..10,
                level = level ?: playerProfile.level,
                characterClass = chosenClass
            )

            val mCity = cityId ?: PBCityConstants.NEW_YORK
            val mGender = gender ?: PBGenderConstants.random()

            val portrait = picString ?: if (mGender == PBGenderConstants.MALE) {
                RandomData.randomMalePortrait()
            } else {
                RandomData.randomFemalePortrait()
            }

            return PBPlayerProfile(
                name = name ?: RandomData.randomNPCname(mGender, mCity),
                gender = mGender,
                picString = portrait,
                characterClass = chosenClass,
                attributes = baseAttributes,
                combatStats = stats,
                highscore = highscore ?: PBHighscore(PBHighscoreEntry.dummy(), PBHighscoreEntry.dummy()),
                wonDuels = wonDuels ?: 0,
                lostDuels = lostDuels ?: 0,
                isIgnored = isIgnored ?: false,
                level = level ?: 1,
                cityId = cityId ?: 0,
                clanName = clanName,
                clanId = clanId,
                rankInClan = rankInClan
            )
        }
    }
}
