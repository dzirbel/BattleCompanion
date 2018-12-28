package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.MultiSet
import org.junit.Assert.assertEquals
import org.junit.Test

class ArmyTest {

    @Test
    fun testFromMap() {
        val casualtyPicker = CasualtyPickers.default

        assertEquals(
            Army(
                units = mapOf(
                    UnitType.INFANTRY to MultiSet(mapOf(1 to 3)),
                    UnitType.TANK to MultiSet(mapOf(1 to 2)),
                    UnitType.SUBMARINE to MultiSet(mapOf(1 to 4)),
                    UnitType.ANTIAIRCRAFT_GUN to MultiSet(mapOf(1 to 1)),
                    UnitType.BATTLESHIP to MultiSet(mapOf(2 to 3))
                ),
                casualtyPicker = casualtyPicker
            ),
            Army.fromMap(
                units = mapOf(
                    UnitType.INFANTRY to 3,
                    UnitType.TANK to 2,
                    UnitType.SUBMARINE to 4,
                    UnitType.ANTIAIRCRAFT_GUN to 1,
                    UnitType.BATTLESHIP to 3
                ),
                casualtyPicker = casualtyPicker
            )
        )
    }

    @Test
    fun testCount() {
        val units = mapOf(
            UnitType.INFANTRY to 3,
            UnitType.ARTILLERY to 2,
            UnitType.TANK to 5,
            UnitType.ANTIAIRCRAFT_GUN to 1,
            UnitType.FIGHTER to 3,
            UnitType.BOMBER to 1,
            UnitType.TRANSPORT to 2,
            UnitType.SUBMARINE to 3,
            UnitType.DESTROYER to 1,
            UnitType.AIRCRAFT_CARRIER to 2,
            UnitType.BATTLESHIP to 1
        )

        val army = Armies.fromUnits(units)

        assertEquals(24, army.count { true })
        assertEquals(0, army.count { false })

        units.forEach { (unitType, count) ->
            assertEquals(count, army.count { it == unitType })
        }

        assertEquals(11, army.count { it.domain == Domain.LAND })
        assertEquals(4, army.count { it.domain == Domain.AIR })
        assertEquals(9, army.count { it.domain == Domain.SEA })
    }

    @Test
    fun testWithoutFirstRoundOnly() {
        val army = Armies.fromUnits(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.ANTIAIRCRAFT_GUN to 1,
                UnitType.BOMBARDING_BATTLESHIP to 1
            )
        )

        assertEquals(
            Armies.fromUnits(mapOf(UnitType.INFANTRY to 3)),
            army.withoutFirstRoundOnlyUnits()
        )
    }
}
