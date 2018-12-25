package com.github.dzirbel.battlecompanion.core

// TODO techs
// TODO attacking aa guns can never be destroyed
// TODO multiple defending aa guns
// TODO don't allow defending bombarding battleships (although they technically work?)
enum class UnitType(
    val domain: Domain,
    val attack: Int,
    val defense: Int,
    val cost: Int,

    /**
     * The maximum number of hits this [UnitType] can sustain, typically 1 (default).
     */
    val maxHp: Int = 1,

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
    BATTLESHIP(domain = Domain.SEA, attack = 4, defense = 4, cost = 24, maxHp = 2);

    val prettyName = name.split("_").joinToString(separator = " ", transform = { it.toLowerCase().capitalize() })

    /**
     * Determines the number of dice this unit should throw each round against the given [Army].
     */
    fun numberOfRolls(enemies: Army): Int {
        return when (this) {
            ANTIAIRCRAFT_GUN -> enemies.countBy { it.domain == Domain.AIR }
            else -> 1
        }
    }

    /**
     * Determines whether this [UnitType] should fire during the opening fire round against the given [Army].
     */
    fun hasOpeningFire(enemies: Army): Boolean {
        return when (this) {
            ANTIAIRCRAFT_GUN -> true
            BOMBARDING_BATTLESHIP -> true
            SUBMARINE -> enemies.countBy { it == DESTROYER } == 0
            else -> false
        }
    }
}
