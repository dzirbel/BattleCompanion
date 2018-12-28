package com.github.dzirbel.battlecompanion.core

internal object CasualtyPickers {

    val default = CasualtyPicker.ByCost(isAttacking = true)
}

internal object Armies {

    val all = listOf(
        fromUnits(
            mapOf(
                UnitType.INFANTRY to 2,
                UnitType.TANK to 1,
                UnitType.ANTIAIRCRAFT_GUN to 1
            )
        ),
        fromUnits(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.ARTILLERY to 2,
                UnitType.TANK to 5,
                UnitType.FIGHTER to 2,
                UnitType.BOMBER to 1
            )
        ),
        fromUnits(
            mapOf(
                UnitType.BATTLESHIP to 1,
                UnitType.DESTROYER to 1
            )
        ),
        fromUnits(
            mapOf(
                UnitType.SUBMARINE to 3,
                UnitType.AIRCRAFT_CARRIER to 1
            )
        ),
        fromUnits(
            mapOf(
                UnitType.FIGHTER to 4,
                UnitType.BOMBER to 1
            )
        )
    )

    fun armyWithAirUnits(fighters: Int = 0, bombers: Int = 0): Army {
        return Army.fromMap(
            casualtyPicker = CasualtyPickers.default,
            units = mapOf(
                UnitType.INFANTRY to 3,
                UnitType.FIGHTER to fighters,
                UnitType.BOMBER to bombers
            )
        )
    }

    fun armyWithDestroyers(destroyers: Int = 0): Army {
        return Army.fromMap(
            casualtyPicker = CasualtyPickers.default,
            units = mapOf(
                UnitType.BATTLESHIP to 1,
                UnitType.DESTROYER to destroyers
            )
        )
    }

    private fun fromUnits(
        units: Map<UnitType, Int>,
        casualtyPicker: CasualtyPicker = CasualtyPickers.default
    ): Army {
        return Army.fromMap(units = units, casualtyPicker = casualtyPicker)
    }
}
