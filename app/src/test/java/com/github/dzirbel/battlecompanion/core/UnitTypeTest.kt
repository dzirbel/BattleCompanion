package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitTypeTest {

    @Test
    fun testCombatPower() {
        UnitType.values().forEach { unitType ->
            assertEquals(
                unitType.attack,
                unitType.combatPower(isAttacking = true, weaponDevelopments = emptySet())
            )
            assertEquals(
                unitType.defense,
                unitType.combatPower(isAttacking = false, weaponDevelopments = emptySet())
            )
        }
    }

    @Test
    fun testNumberOfRolls() {
        UnitType.values().filter { it != UnitType.ANTIAIRCRAFT_GUN }.forEach { unitType ->
            Armies.all.forEach { army ->
                listOf(true, false).forEach { isAttacking ->
                    assertEquals(
                        1,
                        unitType.numberOfRolls(
                            enemies = army,
                            isAttacking = isAttacking,
                            weaponDevelopments = emptySet()
                        )
                    )
                }
            }
        }

        assertEquals(
            0,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                enemies = Armies.armyWithAirUnits(fighters = 0, bombers = 0),
                isAttacking = false,
                weaponDevelopments = emptySet()
            )
        )

        assertEquals(
            2,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                enemies = Armies.armyWithAirUnits(fighters = 2),
                isAttacking = false,
                weaponDevelopments = emptySet()
            )
        )

        assertEquals(
            2,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                enemies = Armies.armyWithAirUnits(bombers = 2),
                isAttacking = false,
                weaponDevelopments = emptySet()
            )
        )

        assertEquals(
            5,
            UnitType.ANTIAIRCRAFT_GUN.numberOfRolls(
                enemies = Armies.armyWithAirUnits(fighters = 3, bombers = 2),
                isAttacking = false,
                weaponDevelopments = emptySet()
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

    @Test
    fun testHasRequiredWeaponDevelopments() {
        UnitType.values().filter { it != UnitType.BOMBARDING_DESTROYER }.forEach { unitType ->
            assertTrue(unitType.hasRequiredWeaponDevelopments(emptySet()))
        }

        assertFalse(UnitType.BOMBARDING_DESTROYER.hasRequiredWeaponDevelopments(emptySet()))
        assertTrue(
            UnitType.BOMBARDING_DESTROYER.hasRequiredWeaponDevelopments(
                setOf(WeaponDevelopment.COMBINED_BOMBARDMENT)
            )
        )
    }

    @Test
    fun testCanAttackIn() {
        listOf(Domain.LAND, Domain.SEA).forEach { domain ->
            UnitType.values().forEach { unitType ->
                when (unitType) {
                    UnitType.ANTIAIRCRAFT_GUN -> assertFalse(unitType.canAttackIn(domain))
                    else -> assertEquals(
                        unitType.domain == Domain.AIR || unitType.domain == domain,
                        unitType.canAttackIn(domain)
                    )
                }
            }
        }
    }

    @Test
    fun testCanDefendIn() {
        listOf(Domain.LAND, Domain.SEA).forEach { domain ->
            UnitType.values().forEach { unitType ->
                when (unitType) {
                    UnitType.BOMBARDING_BATTLESHIP -> assertFalse(unitType.canDefendIn(domain))
                    UnitType.BOMBARDING_DESTROYER -> assertFalse(unitType.canDefendIn(domain))
                    UnitType.BOMBER ->
                        assertEquals(domain == Domain.LAND, unitType.canDefendIn(domain))
                    else -> assertEquals(
                        unitType.domain == Domain.AIR || unitType.domain == domain,
                        unitType.canDefendIn(domain)
                    )
                }
            }
        }
    }
}
