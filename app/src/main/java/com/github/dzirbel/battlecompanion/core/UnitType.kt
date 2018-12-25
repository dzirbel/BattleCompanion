package com.github.dzirbel.battlecompanion.core

// TODO battleship health
// TODO techs
// TODO attacking aa guns can never be destroyed
// TODO multiple defending aa guns
enum class UnitType(
    val domain: Domain,
    val attack: Int,
    val defense: Int,
    val cost: Int,

    /**
     * Whether this [UnitType] participates only in the first round of combat, after which it should be removed.
     */
    val firstRoundOnly: Boolean = false,

    /**
     * Specifies a [Domain] which this [UnitType] can only hit, e.g. [SUBMARINE]s can only hit [Domain.SEA].
     * Null (default) indicates that this [UnitType] can hit any [Domain].
     */
    val targetDomain: Domain? = null
) {

    INFANTRY(domain = Domain.LAND, attack = 1, defense = 2, cost = 3),
    ARTILLERY(domain = Domain.LAND, attack = 2, defense = 2, cost = 4),
    TANK(domain = Domain.LAND, attack = 3, defense = 3, cost = 5),
    ANTIAIRCRAFT_GUN(
        domain = Domain.LAND,
        attack = 0,
        defense = 1,
        cost = 5,
        firstRoundOnly = true,
        targetDomain = Domain.AIR
    ),
    BOMBARDING_BATTLESHIP(
        domain = Domain.LAND,
        attack = 4,
        defense = 0,
        cost = 24,
        firstRoundOnly = true,
        targetDomain = Domain.LAND
    ),

    FIGHTER(domain = Domain.AIR, attack = 3, defense = 4, cost = 10),
    BOMBER(domain = Domain.AIR, attack = 4, defense = 1, cost = 15),

    TRANSPORT(domain = Domain.SEA, attack = 0, defense = 1, cost = 8),
    SUBMARINE(
        domain = Domain.SEA,
        attack = 2,
        defense = 2,
        cost = 8,
        targetDomain = Domain.SEA
    ),
    DESTROYER(domain = Domain.SEA, attack = 3, defense = 3, cost = 12),
    AIRCRAFT_CARRIER(domain = Domain.SEA, attack = 1, defense = 3, cost = 16),
    BATTLESHIP(domain = Domain.SEA, attack = 4, defense = 4, cost = 24);

    val prettyName = name.split("_").joinToString(separator = " ", transform = { it.toLowerCase().capitalize() })

    /**
     * Determines whether this [UnitType] should fire during the opening fire round for the given [Board].
     */
    fun hasOpeningFire(board: Board, isAttacking: Boolean): Boolean {
        return when (this) {
            ANTIAIRCRAFT_GUN -> true
            BOMBARDING_BATTLESHIP -> true
            SUBMARINE -> {
                if (isAttacking) {
                    board.defenders.units.none { it.key == DESTROYER }
                } else {
                    board.attackers.units.none { it.key == DESTROYER }
                }
            }
            else -> false
        }
    }
}