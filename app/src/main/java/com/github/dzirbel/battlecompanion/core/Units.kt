package com.github.dzirbel.battlecompanion.core

enum class UnitTerrain { LAND, SEA, AIR }

// TODO artillery boosted infantry
// TODO battleship health
// TODO techs
enum class UnitType(
    val terrain: UnitTerrain,
    val attack: Int,
    val defense: Int,
    val cost: Int,
    val specificTerrain: UnitTerrain? = null
) {

    INFANTRY(terrain = UnitTerrain.LAND, attack = 1, defense = 2, cost = 3),
    ARTILLERY(terrain = UnitTerrain.LAND, attack = 2, defense = 2, cost = 4),
    TANK(terrain = UnitTerrain.LAND, attack = 3, defense = 3, cost = 5),
    ANTIAIRCRAFT_GUN(
        terrain = UnitTerrain.LAND,
        attack = 0,
        defense = 1,
        cost = 5,
        specificTerrain = UnitTerrain.AIR
    ),

    FIGHTER(terrain = UnitTerrain.AIR, attack = 3, defense = 4, cost = 10),
    BOMBER(terrain = UnitTerrain.AIR, attack = 4, defense = 1, cost = 15),

    TRANSPORT(terrain = UnitTerrain.SEA, attack = 0, defense = 1, cost = 8),
    SUBMARINE(
        terrain = UnitTerrain.SEA,
        attack = 2,
        defense = 2,
        cost = 8,
        specificTerrain = UnitTerrain.SEA
    ),
    DESTROYER(terrain = UnitTerrain.SEA, attack = 3, defense = 3, cost = 12),
    AIRCRAFT_CARRIER(terrain = UnitTerrain.SEA, attack = 1, defense = 3, cost = 16),
    BATTLESHIP(terrain = UnitTerrain.SEA, attack = 4, defense = 4, cost = 24)
}
