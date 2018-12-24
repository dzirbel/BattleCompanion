package com.github.dzirbel.battlecompanion.core

// TODO UnitType might not be the best name
// TODO battleship health
// TODO techs
enum class UnitType(
    val domain: Domain,
    val attack: Int,
    val defense: Int,
    val cost: Int,

    /**
     * Specifies a [Domain] which this [UnitType] can only hit, e.g. [SUBMARINE]s can only hit [Domain.SEA].
     * Null indicates that this [UnitType] can hit any [Domain].
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
        targetDomain = Domain.AIR
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

    val prettyName = name.split("\\w+").joinToString(separator = " ", transform = { it.toLowerCase().capitalize() })
}
