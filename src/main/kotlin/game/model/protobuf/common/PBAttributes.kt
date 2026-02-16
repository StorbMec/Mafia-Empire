package dev.gangster.game.model.protobuf.common

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.random.Random

enum class Difficulty { VERY_EASY, EASY, MODERATE, HARD, VERY_HARD }

@Serializable
data class PBAttributes(
    val attack: Int?,
    val endurance: Int?,
    val luck: Int?,
    val toughness: Int?,
) {
    companion object {
        fun newGame(): PBAttributes {
            return PBAttributes(
                attack = 20,
                endurance = 20,
                luck = 20,
                toughness = 20
            )
        }

        fun hacker(): PBAttributes {
            return PBAttributes(
                attack = 8885555,
                endurance = 7777555,
                luck = 20,
                toughness = 20
            )
        }

        // we categorize npc difficulty to 5.
        // depending on the context
        // for mission it should be very easy, wanted may be 3-5
        fun randomRelativeToPlayer(
            player: PBAttributes,
            characterClass: PBCharacterClass,
            difficulty: Difficulty
        ): PBAttributes {
            val diffMultiplier = when (difficulty) {
                Difficulty.VERY_EASY -> 0.5..0.7
                Difficulty.EASY -> 0.8..0.95
                Difficulty.MODERATE -> 1.0..1.2
                Difficulty.HARD -> 1.3..1.5
                Difficulty.VERY_HARD -> 1.6..2.0
            }

            fun scaled(base: Int, factor: ClosedRange<Double>): Int {
                return (base * Random.nextDouble(factor.start, factor.endInclusive)).toInt().coerceAtLeast(1)
            }

            requireNotNull(player.attack)
            requireNotNull(player.endurance)
            requireNotNull(player.luck)
            requireNotNull(player.toughness)

            return when (characterClass) {
                PBCharacterClassConstants.BULLY -> PBAttributes(
                    attack = scaled(
                        player.attack,
                        (diffMultiplier.start * 0.6)..(diffMultiplier.endInclusive * 0.8)
                    ), // weaker attack
                    endurance = scaled(
                        player.endurance,
                        (diffMultiplier.start * 1.2)..(diffMultiplier.endInclusive * 1.5)
                    ), // stronger hp
                    luck = scaled(player.luck, diffMultiplier),
                    toughness = scaled(player.toughness, diffMultiplier)
                )

                PBCharacterClassConstants.ROGUE -> PBAttributes(
                    attack = scaled(
                        player.attack,
                        (diffMultiplier.start * 0.9)..(diffMultiplier.endInclusive * 1.1)
                    ), // moderate attack
                    endurance = scaled(
                        player.endurance,
                        (diffMultiplier.start * 0.9)..(diffMultiplier.endInclusive * 1.1)
                    ), // moderate hp
                    luck = scaled(
                        player.luck,
                        (diffMultiplier.start * 1.2)..(diffMultiplier.endInclusive * 1.4)
                    ), // slightly better luck
                    toughness = scaled(player.toughness, diffMultiplier)
                )

                // tactictian
                else -> PBAttributes(
                    attack = scaled(
                        player.attack,
                        (diffMultiplier.start * 1.3)..(diffMultiplier.endInclusive * 1.6)
                    ), // strong attack
                    endurance = scaled(
                        player.endurance,
                        (diffMultiplier.start * 0.6)..(diffMultiplier.endInclusive * 0.9)
                    ), // weaker hp
                    luck = scaled(player.luck, diffMultiplier),
                    toughness = scaled(player.toughness, diffMultiplier)
                )
            }
        }
    }
}

// TO-DO improve formula
// take account items and accessory
fun PBAttributes.toCombatStats(weapon: IntRange, level: Int, characterClass: PBCharacterClass): PBCombatStats {
    val levelMultiplier = 1.13.pow(level - 1).toFloat() // exponential growth

    // base scaling factors
    val baseDamage = attack!! * 2
    val baseHealth = endurance!! * 10 * levelMultiplier
    val baseCrit = (luck!! / 100.0f).coerceIn(0f, 0.5f)     // cap crit at 50%
    val baseResistance = (toughness!! / 200.0f).coerceIn(0f, 0.5f) // cap resistance at 50%
    val baseHitChance = 0.75f + (luck / 200.0f).coerceIn(0f, 0.2f) // 75–95%

    val weaponDamage = (weapon.first..weapon.last).random() * levelMultiplier

    return when (characterClass) {
        PBCharacterClassConstants.BULLY -> PBCombatStats(
            damage = (baseDamage * 0.8 + weaponDamage).toInt(),
            health = (baseHealth * 1.5).toInt(),
            critical = (baseCrit * 0.8f),
            resistance = (baseResistance * 1.3f).coerceAtMost(0.6f),
            hitChance = (baseHitChance * 0.9f).coerceIn(0.6f, 0.95f)
        )

        PBCharacterClassConstants.ROGUE -> PBCombatStats(
            damage = (baseDamage + weaponDamage).toInt(),
            health = (baseHealth * 1.1f).toInt(),
            critical = (baseCrit * 1.5f).coerceAtMost(0.6f),
            resistance = baseResistance,
            hitChance = (baseHitChance * 1.1f).coerceAtMost(0.95f)
        )

        // tactictian
        else -> PBCombatStats(
            damage = (baseDamage * 1.4 + weaponDamage).toInt(),
            health = (baseHealth * 0.8).toInt(),
            critical = baseCrit,
            resistance = (baseResistance * 0.8f),
            hitChance = (baseHitChance * 1.2f).coerceAtMost(0.99f)
        )
    }
}
