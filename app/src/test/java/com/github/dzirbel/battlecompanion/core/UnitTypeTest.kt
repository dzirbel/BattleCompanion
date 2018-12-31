package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitTypeTest {

    @Test
    fun testCombatPower() {
        UnitType.values().forEach { unitType ->
            assertEquals(unitType.attack, unitType.combatPower(isAttacking = true))
            assertEquals(unitType.defense, unitType.combatPower(isAttacking = false))
        }
    }

    @Test
    fun testNumberOfRolls() {
        UnitType.values().filter { it != UnitType.ANTIAIRCRAFT_GUN }.forEach { unitType ->
            Armies.all.forEach { army ->
                assertEquals(1, unitType.numberOfRolls(army))
            }
        }

        assertEquals(
            0,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                Armies.armyWithAirUnits(fighters = 0, bombers = 0)
            )
        )

        assertEquals(
            2,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                Armies.armyWithAirUnits(fighters = 2)
            )
        )

        assertEquals(
            2,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                Armies.armyWithAirUnits(bombers = 2)
            )
        )

        assertEquals(
            5,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                Armies.armyWithAirUnits(fighters = 3, bombers = 2)
            )
        )
    }

    @Test
    fun hasOpeningFire() {
        UnitType.values().filter { it != UnitType.SUBMARINE }.forEach { unitType ->
            Armies.all.forEach { army ->
                when (unitType) {
                    UnitType.ANTIAIRCRAFT_GUN -> assertTrue(unitType.hasOpeningFire(army))
                    UnitType.BOMBARDING_BATTLESHIP -> assertTrue(unitType.hasOpeningFire(army))
                    else -> assertFalse(unitType.hasOpeningFire(army))
                }
            }
        }

        assertTrue(UnitType.SUBMARINE.hasOpeningFire(Armies.armyWithDestroyers(destroyers = 0)))
        assertFalse(UnitType.SUBMARINE.hasOpeningFire(Armies.armyWithDestroyers(destroyers = 1)))
        assertFalse(UnitType.SUBMARINE.hasOpeningFire(Armies.armyWithDestroyers(destroyers = 3)))
    }

    @Test
    fun testCanInvade() {
        assertTrue(UnitType.INFANTRY.canInvade())
        assertTrue(UnitType.ARTILLERY.canInvade())
        assertTrue(UnitType.TANK.canInvade())
        assertTrue(UnitType.TRANSPORT.canInvade())
        assertTrue(UnitType.SUBMARINE.canInvade())
        assertTrue(UnitType.DESTROYER.canInvade())
        assertTrue(UnitType.AIRCRAFT_CARRIER.canInvade())
        assertTrue(UnitType.BATTLESHIP.canInvade())

        assertFalse(UnitType.ANTIAIRCRAFT_GUN.canInvade())
        assertFalse(UnitType.BOMBARDING_BATTLESHIP.canInvade())
        assertFalse(UnitType.FIGHTER.canInvade())
        assertFalse(UnitType.BOMBER.canInvade())
    }
}
