package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Test

class CasualtyPickerTest {

    private val army = Armies.fromUnits(
        UnitType.INFANTRY to 3,
        UnitType.TANK to 3,
        UnitType.ANTIAIRCRAFT_GUN to 1,
        UnitType.FIGHTER to 2,
        UnitType.BOMBER to 2,
        UnitType.TRANSPORT to 2,
        UnitType.AIRCRAFT_CARRIER to 2,
        UnitType.BATTLESHIP to 1
    )

    private val lightHits = HitProfile(
        generalHits = 2,
        domainHits = mapOf(
            Domain.LAND to 1,
            Domain.SEA to 1,
            Domain.AIR to 1
        )
    )

    private val mediumHits = HitProfile(
        generalHits = 3,
        domainHits = mapOf(
            Domain.LAND to 2,
            Domain.SEA to 2,
            Domain.AIR to 2
        )
    )

    private val heavyHits = HitProfile(
        generalHits = 3,
        domainHits = mapOf(
            Domain.LAND to 3,
            Domain.SEA to 3,
            Domain.AIR to 3
        )
    )

    @Test
    fun testByCost() {
        val casualtyPicker = CasualtyPicker.ByCost()

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.FIGHTER to 1,
                UnitType.TRANSPORT to 1
            ),
            casualtyPicker.pick(army = army, hits = lightHits, isAttacking = true)
        )

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.TANK to 2,
                UnitType.FIGHTER to 2,
                UnitType.TRANSPORT to 2
            ),
            casualtyPicker.pick(army = army, hits = mediumHits, isAttacking = true)
        )

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.TANK to 3,
                UnitType.FIGHTER to 2,
                UnitType.BOMBER to 1,
                UnitType.TRANSPORT to 2,
                UnitType.AIRCRAFT_CARRIER to 1
            ),
            casualtyPicker.pick(army = army, hits = heavyHits, isAttacking = true)
        )
    }

    @Test
    fun testByCombatPower() {
        val casualtyPicker = CasualtyPicker.ByCombatPower()

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 2,
                UnitType.FIGHTER to 1,
                UnitType.TRANSPORT to 2
            ),
            casualtyPicker.pick(army = army, hits = lightHits, isAttacking = true)
        )

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.FIGHTER to 2,
                UnitType.TRANSPORT to 2,
                UnitType.AIRCRAFT_CARRIER to 2
            ),
            casualtyPicker.pick(army = army, hits = mediumHits, isAttacking = true)
        )

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 3,
                UnitType.TANK to 2,
                UnitType.FIGHTER to 2,
                UnitType.BOMBER to 1,
                UnitType.TRANSPORT to 2,
                UnitType.AIRCRAFT_CARRIER to 2
            ),
            casualtyPicker.pick(army = army, hits = heavyHits, isAttacking = true)
        )
    }

    @Test
    fun testByCombatPowerDefending() {
        val casualtyPicker = CasualtyPicker.ByCombatPower()
        val army = Armies.fromUnits(
            UnitType.INFANTRY to 3,
            UnitType.BOMBER to 3
        )

        val hits = HitProfile(
            generalHits = 3,
            domainHits = mapOf(
                Domain.LAND to 1,
                Domain.SEA to 1,
                Domain.AIR to 1
            )
        )

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 2,
                UnitType.BOMBER to 3
            ),
            casualtyPicker.pick(army = army, hits = hits, isAttacking = false)
        )
    }

    @Test
    fun testByCostKeepInvading() {
        val casualtyPicker = CasualtyPicker.ByCost(keepInvadingUnit = true)
        val army = Armies.fromUnits(
            UnitType.INFANTRY to 2,
            UnitType.TANK to 2,
            UnitType.FIGHTER to 5,
            UnitType.BOMBER to 1
        )

        val hits = HitProfile(
            generalHits = 4,
            domainHits = mapOf(
                Domain.LAND to 2
            )
        )

        assertEquals(
            mapOf(
                UnitType.INFANTRY to 2,
                UnitType.TANK to 1,
                UnitType.FIGHTER to 3
            ),
            casualtyPicker.pick(army = army, hits = hits, isAttacking = true)
        )
    }
}
