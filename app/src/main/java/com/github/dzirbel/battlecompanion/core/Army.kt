package com.github.dzirbel.battlecompanion.core

import java.util.EnumMap
import kotlin.random.Random

/**
 * Represents one side of a combat board: the [units] (as a map from each [UnitType] to the number of those units in the
 *  army) and [unitPriority] which determines which units to lose as casualties first.
 * Note that [Army]s are immutable.
 * TODO allow [unitPriority] to be more generic to support user input (i.e. just return the list of casualties)
 */
data class Army(
    val units: Map<UnitType, Int>,
    val unitPriority: Comparator<UnitType>
) {

    /**
     * In order to have terrain-specific hits (such as submarines only hitting sea units), the profile of a round of
     *  hits is more complicated than simply the total number of hits.
     * [allTerrainHits] is the number of hits which can be applied to all [UnitTerrain]s; [terrainHits] is a map from
     *  each [UnitTerrain] to the number of hits which must be applied to that [UnitTerrain] (missing keys are allowed
     *  and represent zero terrain-specific hits).
     * Battle correctness dictates that terrain-specific hits must be applied before terrain-agnostic hits.
     */
    data class HitProfile(val allTerrainHits: Int, val terrainHits: Map<UnitTerrain, Int>)

    /**
     * The total number of units in this [Army] (including antiaircraft guns, etc).
     */
    val totalUnits: Int = units.values.sum()

    /**
     * Returns a copy of this [Army] without units of the given [UnitType].
     */
    fun withoutType(unitType: UnitType): Army {
        return copy(units = units.filterKeys { it != unitType })
    }

    /**
     * Returns a copy of this [Army] with only units of the given [UnitType].
     */
    fun ofType(unitType: UnitType): Army {
        return copy(units = mapOf(unitType to (units[unitType] ?: 0)))
    }

    /**
     * Computes the [HitProfile] that this [Army] inflicts in a single round, rolling with the given [Random].
     */
    fun computeHits(rand: Random, isAttacking: Boolean): HitProfile {
        var allHits = 0
        val terrainHits = EnumMap<UnitTerrain, Int>(UnitTerrain::class.java)

        units.forEach { (unitType, count) ->
            val hits = rand.rollDice(count).count { it <= if (isAttacking) unitType.attack else unitType.defense }
            if (hits > 0) {
                if (unitType.specificTerrain == null) {
                    allHits += hits
                } else {
                    terrainHits[unitType.specificTerrain] = (terrainHits[unitType.specificTerrain] ?: 0) + hits
                }
            }
        }

        return HitProfile(allTerrainHits = allHits, terrainHits = terrainHits)
    }

    /**
     * Returns a copy of this [Army] with the given [HitProfile] inflicted.
     */
    fun takeHits(hitProfile: HitProfile): Army {
        var remainingArmy = this
        hitProfile.terrainHits.forEach { (terrain, hits) ->
            remainingArmy = remainingArmy.takeHits(hits = hits, terrain = terrain)
        }
        return remainingArmy.takeHits(hitProfile.allTerrainHits)
    }

    private fun takeHits(hits: Int, terrain: UnitTerrain? = null): Army {
        var remainingHits = hits
        return copy(
            units = units.keys
                .filter { it != UnitType.ANTIAIRCRAFT_GUN }
                .filter { terrain == null || it.terrain == terrain }
                .sortedWith(unitPriority)
                .map { unitType: UnitType ->
                    val count = units[unitType] ?: throw IllegalStateException()
                    val casualties = Math.min(count, remainingHits)
                    remainingHits -= casualties
                    Pair(unitType, count - casualties)
                }
                .toMap()
                .filter { it.value > 0 }
        )
    }
}
