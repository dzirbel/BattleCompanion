package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

internal object Randoms {

    val all = List(10) { Random(it) }
}

internal object CasualtyPickers {

    val default = CasualtyPicker.ByCost()
}

internal object Armies {

    val empty = fromUnits()

    private val units = listOf(
        arrayOf(
            UnitType.INFANTRY to 2,
            UnitType.TANK to 1,
            UnitType.ANTIAIRCRAFT_GUN to 1
        ),
        arrayOf(
            UnitType.INFANTRY to 3,
            UnitType.ARTILLERY to 2,
            UnitType.TANK to 5,
            UnitType.FIGHTER to 2,
            UnitType.BOMBER to 1
        ),
        arrayOf(
            UnitType.BATTLESHIP to 1,
            UnitType.DESTROYER to 1
        ),
        arrayOf(
            UnitType.SUBMARINE to 3,
            UnitType.AIRCRAFT_CARRIER to 1
        ),
        arrayOf(
            UnitType.FIGHTER to 4,
            UnitType.BOMBER to 1
        )
    )

    val attackers = units.map { fromUnits(*it, isAttacking = true) }
    val defenders = units.map { fromUnits(*it, isAttacking = false) }

    val all = attackers + defenders

    fun armyWithAirUnits(fighters: Int = 0, bombers: Int = 0): Army {
        return fromUnits(
            UnitType.INFANTRY to 3,
            UnitType.FIGHTER to fighters,
            UnitType.BOMBER to bombers
        )
    }

    fun armyWithDestroyers(destroyers: Int = 0): Army {
        return fromUnits(
            UnitType.BATTLESHIP to 1,
            UnitType.DESTROYER to destroyers
        )
    }

    fun fromUnits(
        vararg units: Pair<UnitType, Int>,
        isAttacking: Boolean = true,
        casualtyPicker: CasualtyPicker = CasualtyPickers.default,
        weaponDevelopments: Set<WeaponDevelopment> = emptySet()
    ): Army {
        return Army.fromMap(
            units = units.toMap(),
            isAttacking = isAttacking,
            casualtyPicker = casualtyPicker,
            weaponDevelopments = weaponDevelopments
        )
    }
}
