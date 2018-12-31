package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.multiSetOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArmyTest {

    @Test
    fun testFromMap() {
        val casualtyPicker = CasualtyPickers.default

        assertEquals(
            Army(
                units = mapOf(
                    UnitType.INFANTRY to multiSetOf(1 to 3),
                    UnitType.TANK to multiSetOf(1 to 2),
                    UnitType.SUBMARINE to multiSetOf(1 to 4),
                    UnitType.ANTIAIRCRAFT_GUN to multiSetOf(1 to 1),
                    UnitType.BATTLESHIP to multiSetOf(2 to 3)
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
        Armies.all.forEach { army ->
            val withoutFirstRoundOnly = army.withoutFirstRoundOnlyUnits()
            UnitType.values().forEach { unitType ->
                val count = withoutFirstRoundOnly.count { it == unitType }
                if (unitType.firstRoundOnly) {
                    assertEquals(0, count)
                } else {
                    assertEquals(army.count { it == unitType }, count)
                }
            }
        }
    }

    @Test
    fun testRollHits() {
        Randoms.all.forEach { rand ->
            Armies.all.forEach { army ->
                Armies.all.forEach { enemies ->
                    listOf(true, false).forEach { isAttacking ->
                        listOf(true, false).forEach { isOpeningFire ->
                            val hits = army.rollHits(
                                rand = rand,
                                enemies = enemies,
                                isAttacking = isAttacking,
                                isOpeningFire = isOpeningFire
                            )

                            assertTrue(
                                hits.generalHits <= maxHits(
                                    army = army,
                                    enemies = enemies,
                                    domain = null,
                                    isOpeningFire = isOpeningFire
                                )
                            )

                            hits.domainHits.forEach { domain, domainHits ->
                                assertTrue(
                                    domainHits <= maxHits(
                                        army = army,
                                        enemies = enemies,
                                        domain = domain,
                                        isOpeningFire = isOpeningFire
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun maxHits(army: Army, enemies: Army, domain: Domain?, isOpeningFire: Boolean): Int {
        return army.units.entries
            .filter {
                it.key.targetDomain == domain && it.key.hasOpeningFire(enemies) == isOpeningFire
            }
            .sumBy { it.value.size * it.key.numberOfRolls(enemies) }
    }
}
