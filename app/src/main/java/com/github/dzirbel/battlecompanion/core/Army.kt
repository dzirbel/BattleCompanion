package com.github.dzirbel.battlecompanion.core

import java.util.EnumMap
import kotlin.random.Random

data class Army(
    val units: Map<UnitType, Int>,
    val unitPriority: Comparator<UnitType>
) {

    data class HitProfile(val allTerrainHits: Int, val terrainHits: Map<UnitTerrain, Int>)

    val totalUnits: Int = units.values.sum()

    fun withoutType(unitType: UnitType): Army {
        return copy(units = units.filterKeys { it != unitType })
    }

    fun computeHits(rand: Random): HitProfile {
        var allHits = 0
        val terrainHits = EnumMap<UnitTerrain, Int>(UnitTerrain::class.java)

        units.forEach { (unitType, count) ->
            val hits = rand.rollDice(count).count { it <= unitType.attack }
            if (unitType.specificTerrain == null) {
                allHits += hits
            } else {
                terrainHits[unitType.specificTerrain] = (terrainHits[unitType.specificTerrain] ?: 0) + hits
            }
        }

        return HitProfile(allTerrainHits = allHits, terrainHits = terrainHits)
    }

    fun takeHits(hitProfile: HitProfile): Army {
        var remaining = this
        hitProfile.terrainHits.forEach { (terrain, hits) ->
            remaining = remaining.takeHits(hits = hits, terrain = terrain)
        }
        return remaining.takeHits(hitProfile.allTerrainHits)
    }

    fun takeHits(hits: Int, terrain: UnitTerrain? = null): Army {
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
