package dev.gangster.game.fight

import dev.gangster.context.GlobalContext
import dev.gangster.game.PlayerService
import dev.gangster.game.data.ItemIds
import dev.gangster.game.data.ItemSubtypeId
import dev.gangster.game.model.protobuf.common.Difficulty
import dev.gangster.game.model.protobuf.common.PBCharacterClass
import dev.gangster.game.model.protobuf.common.PBItemQualityConstants
import dev.gangster.game.model.protobuf.common.PBItemTypeConstants
import dev.gangster.game.model.protobuf.common.PBPlayerProfile
import dev.gangster.game.model.protobuf.common.PBSide
import dev.gangster.game.model.protobuf.common.PBSideConstants
import dev.gangster.game.model.protobuf.common.toCombatStats
import dev.gangster.game.model.protobuf.fight.PBFight
import dev.gangster.game.model.protobuf.fight.PBFightAction
import dev.gangster.game.model.protobuf.fight.PBFightItem
import dev.gangster.game.model.protobuf.fight.PBFightTurn
import dev.gangster.game.model.protobuf.fight.PBFighter
import dev.gangster.game.model.protobuf.fight.PBFighterTypeConstants
import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionHit
import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionHitResultConstants
import dev.gangster.game.model.protobuf.fight.fightaction.PBFightActionTypeConstants
import kotlin.random.Random

class FightService : PlayerService {
    fun createNPCFighter(id: Int = 0, weaponDmgRange: IntRange, npcProfile: PBPlayerProfile): PBFighter {
        val attr = npcProfile.attributes
        val combatStats = attr.toCombatStats(
            weapon = weaponDmgRange, // TO-DO replace with weapon damage
            level = npcProfile.level,
            characterClass = npcProfile.characterClass
        )

        return PBFighter(
            id = id,
            type = PBFighterTypeConstants.NPC,
            gender = npcProfile.gender,
            characterClass = npcProfile.characterClass,
            level = npcProfile.level,
            totalHp = combatStats.health!!,
            currentHp = combatStats.health,  // mission always full HP
            attributes = attr,
            name = npcProfile.name,
            picString = npcProfile.picString
        )
    }

    fun createPlayerFighter(id: Int, weaponDmgRange: IntRange, playerProfile: PBPlayerProfile): PBFighter {
        val attr = playerProfile.attributes
        val combatStats = attr.toCombatStats(
            weapon = weaponDmgRange, // TO-DO replace with weapon damage
            level = playerProfile.level,
            characterClass = playerProfile.characterClass
        )

        return PBFighter(
            id = id,
            type = PBFighterTypeConstants.PLAYER,
            gender = playerProfile.gender,
            characterClass = playerProfile.characterClass,
            level = playerProfile.level,
            totalHp = combatStats.health!!,
            currentHp = combatStats.health,  // mission always full HP
            attributes = attr,
            name = playerProfile.name,
            picString = playerProfile.picString
        )
    }

    /**
     * Create and simulate a fight between two players
     * fightMode:
     *  - duel
     *  - mis
     *  - surv
     *  - wanted
     */
    fun beginFight(
        leftFighter: PBFighter,
        rightFighter: PBFighter,
    ): PBFight {
        val (winnerSide, turns) = simulateFight(leftFighter, rightFighter)

        val fight = PBFight(
            leftFighter = leftFighter,
            rightFighter = rightFighter,
            winner = winnerSide,
            turns = turns,
            leftAmmunition = emptyList(), // TO-DO not sure what is this for
            rightAmmunition = emptyList()
        )

        return fight
    }

    /**
     * Simulate a fight between two fighter
     */
    private fun simulateFight(left: PBFighter, right: PBFighter): Pair<PBSide, List<PBFightTurn>> {
        val turns = mutableListOf<PBFightTurn>()

        var leftHp = left.currentHp
        var rightHp = right.currentHp

        val firstBlow = chooseFirstAttacker(0.5, 0.5) // same chance without any items
        var attacker = firstBlow

        while (leftHp > 0 && rightHp > 0) {
            val actions = mutableListOf<PBFightAction>()

            val (attackerFighter, defenderFighter, defenderHp) = when (attacker) {
                PBSideConstants.LEFT -> Triple(left, right, rightHp)
                else -> Triple(right, left, leftHp)
            }

            // TO-DO replace with real weapon
            val weaponDmgRange = (3 * attackerFighter.level)..(8 * attackerFighter.level)

            // action can performHit or (performItem -> performHeal, performStun, etc)
            val action = performHit(weaponDmgRange, attackerFighter, defenderFighter)
            actions += action

            // apply damage to defender
            val dmg = action.hit?.firstDmg ?: 0
            if (attacker == PBSideConstants.LEFT) {
                rightHp = (rightHp - dmg).coerceAtLeast(0)
            } else {
                leftHp = (leftHp - dmg).coerceAtLeast(0)
            }

            turns += PBFightTurn(
                attacker = attacker,
                actions = actions
            )

            // switch attacker
            attacker = if (attacker == PBSideConstants.LEFT) PBSideConstants.RIGHT else PBSideConstants.LEFT
        }

        val winner = if (leftHp > 0) PBSideConstants.LEFT else PBSideConstants.RIGHT
        return winner to turns
    }

    fun chooseFirstAttacker(leftChance: Double, rightChance: Double): PBSide {
        val total = leftChance + rightChance
        val roll = Random.nextDouble(0.0, total)
        return if (roll < leftChance) PBSideConstants.LEFT else PBSideConstants.RIGHT
    }

    private fun performHit(
        weaponDmgRange: IntRange,
        attacker: PBFighter,
        defender: PBFighter,
    ): PBFightAction {
        val stats = attacker.attributes.toCombatStats(
            weapon = weaponDmgRange, // TO-DO: real weapon stats
            level = attacker.level,
            characterClass = attacker.characterClass
        )

        // only display
        val weaponFightItem =
            PBFightItem(
                PBItemTypeConstants.WEAPON,
                ItemIds.WEAPON_LIAR,
                PBItemQualityConstants.NORMAL,
                ItemSubtypeId.WEAPON_PISTOL
            )

        // roll for missed hit
        if (Random.nextFloat() > (stats.hitChance ?: 1f)) {
            return PBFightAction(
                type = PBFightActionTypeConstants.HIT,
                hit = PBFightActionHit(
                    weapon = weaponFightItem,
                    result = PBFightActionHitResultConstants.MISSED,
                    firstDmg = 0,
                    secondDmg = null,
                    critical = false
                ),
                consume = null,
                effect = null
            )
        }

        // hit successfully (no evasion roll for now)
        // crit roll
        val crit = Random.nextFloat() < (stats.critical ?: 0f)

        // damage roll between 0.9 - 1.1 of baseDamage on stats
        val baseDamage = stats.damage ?: 0
        val randomized = (baseDamage * Random.nextDouble(0.9, 1.1)).toInt()

        // if crit damage is 1.5x
        val damage = if (crit) (randomized * 1.5).toInt() else randomized

        // reduce damage by the resistance factor
        val defStats = defender.attributes.toCombatStats(weaponDmgRange, defender.level, defender.characterClass)
        val reduced = (damage * (1 - (defStats.resistance ?: 0f))).toInt()

        return PBFightAction(
            type = PBFightActionTypeConstants.HIT,
            hit = PBFightActionHit(
                weapon = weaponFightItem,
                result = PBFightActionHitResultConstants.HIT,
                firstDmg = reduced,
                secondDmg = null,
                critical = crit
            ),
            consume = null,
            effect = null
        )
    }

    override suspend fun init(playerId: Int): Result<Unit> {
        return runCatching {}
    }

    override suspend fun close(playerId: Int): Result<Unit> {
        return runCatching {}
    }
}
