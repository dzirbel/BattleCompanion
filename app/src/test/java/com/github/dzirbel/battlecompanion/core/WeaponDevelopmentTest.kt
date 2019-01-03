package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Test

class WeaponDevelopmentTest {

    @Test
    fun testJetFighters() {
        assertEquals(
            4,
            UnitType.FIGHTER.combatPower(isAttacking = false, weaponDevelopments = emptySet())
        )

        assertEquals(
            5,
            UnitType.FIGHTER.combatPower(
                isAttacking = false,
                weaponDevelopments = setOf(WeaponDevelopment.JET_FIGHTERS)
            )
        )

        assertEquals(
            3,
            UnitType.FIGHTER.combatPower(isAttacking = true, weaponDevelopments = emptySet())
        )

        assertEquals(
            3,
            UnitType.FIGHTER.combatPower(
                isAttacking = true,
                weaponDevelopments = setOf(WeaponDevelopment.JET_FIGHTERS)
            )
        )
    }

    @Test
    fun testSuperSubmarines() {
        assertEquals(
            2,
            UnitType.SUBMARINE.combatPower(isAttacking = true, weaponDevelopments = emptySet())
        )

        assertEquals(
            3,
            UnitType.SUBMARINE.combatPower(
                isAttacking = true,
                weaponDevelopments = setOf(WeaponDevelopment.SUPER_SUBMARINES)
            )
        )

        assertEquals(
            2,
            UnitType.SUBMARINE.combatPower(isAttacking = false, weaponDevelopments = emptySet())
        )

        assertEquals(
            2,
            UnitType.SUBMARINE.combatPower(
                isAttacking = false,
                weaponDevelopments = setOf(WeaponDevelopment.SUPER_SUBMARINES)
            )
        )
    }

    @Test
    fun testHeavyBombers() {
        assertEquals(
            1,
            UnitType.BOMBER.numberOfRolls(
                enemies = Armies.empty,
                isAttacking = true,
                weaponDevelopments = emptySet()
            )
        )

        assertEquals(
            1,
            UnitType.BOMBER.numberOfRolls(
                enemies = Armies.empty,
                isAttacking = false,
                weaponDevelopments = emptySet()
            )
        )

        assertEquals(
            2,
            UnitType.BOMBER.numberOfRolls(
                enemies = Armies.empty,
                isAttacking = true,
                weaponDevelopments = setOf(WeaponDevelopment.HEAVY_BOMBERS)
            )
        )

        assertEquals(
            1,
            UnitType.BOMBER.numberOfRolls(
                enemies = Armies.empty,
                isAttacking = false,
                weaponDevelopments = setOf(WeaponDevelopment.HEAVY_BOMBERS)
            )
        )
    }
}
