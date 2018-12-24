package com.github.dzirbel.battlecompanion.core

data class Army(
    val units: Map<UnitType, Int>,
    val unitPriority: Comparator<UnitType>
) {

    val totalUnits: Int = units.values.sum()

    fun filter(filter: (UnitType) -> Boolean): Army {
        return copy(units = units.filterKeys(filter))
    }

    fun takeHits(hits: Int, filter: (UnitType) -> Boolean = { true }): Army {
        var remainingHits = hits
        return copy(
            units = units.keys
                .filter { it != UnitType.ANTIAIRCRAFT_GUN }
                .filter(filter)
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

