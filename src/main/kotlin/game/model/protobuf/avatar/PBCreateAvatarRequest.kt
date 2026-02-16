package dev.gangster.game.model.protobuf.avatar

import dev.gangster.game.data.RandomData
import dev.gangster.game.model.protobuf.common.PBCharacterClass
import dev.gangster.game.model.protobuf.common.PBCharacterClassConstants
import dev.gangster.game.model.protobuf.common.PBGender
import dev.gangster.game.model.protobuf.common.PBGenderConstants
import kotlinx.serialization.Serializable

/**
 * first portrait structure: <gender>!<skinColor>~<hairColor>~<eyeColor>~<eyebrowColor>~<shirtColor>~<lipstickColor>~<extraTopColor>~<extraUnderColor>!
 * second portrait structure for male: backhair~body~jaw~beard~extraUnder~eyes~eyebrows~nose~mouth~extraTop~fronthair~background
 * second portrait structure for female: backhair~body~jaw~extraUnder~eyes~eyebrows~nose~mouth~extraTop~fronthair~background
 * where each part is index (see Constants_Avatarparts)
 */
@Serializable
data class PBCreateAvatarRequest(
    val gender: PBGender,
    val characterClass: PBCharacterClass,
    val portrait: String,
) {
    companion object {
        fun dummy(): PBCreateAvatarRequest {
            return PBCreateAvatarRequest(
                gender = PBGenderConstants.MALE,
                characterClass = PBCharacterClassConstants.ROGUE,
                portrait = RandomData.randomMalePortrait()
            )
        }
    }
}
