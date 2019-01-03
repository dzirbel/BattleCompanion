package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational
import com.github.dzirbel.battlecompanion.util.multiSetOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArmyTest {

    @Test
    fun testFromMap() {
        val casualtyPicker = CasualtyPickers.default
        val weaponDevelopments = emptySet<WeaponDevelopment>()

        listOf(true, false).forEach { isAttacking ->
            assertEquals(
                Army(
                    units = mapOf(
                        UnitType.INFANTRY to multiSetOf(1 to 3),
                        UnitType.TANK to multiSetOf(1 to 2),
                        UnitType.SUBMARINE to multiSetOf(1 to 4),
                        UnitType.ANTIAIRCRAFT_GUN to multiSetOf(1 to 1),
                        UnitType.BATTLESHIP to multiSetOf(2 to 3)
                    ),
                    isAttacking = isAttacking,
                    casualtyPicker = casualtyPicker,
                    weaponDevelopments = weaponDevelopments
                ),
                Army.fromMap(
                    units = mapOf(
                        UnitType.INFANTRY to 3,
                        UnitType.TANK to 2,
                        UnitType.SUBMARINE to 4,
                        UnitType.ANTIAIRCRAFT_GUN to 1,
                        UnitType.BATTLESHIP to 3
                    ),
                    isAttacking = isAttacking,
                    casualtyPicker = casualtyPicker,
                    weaponDevelopments = weaponDevelopments
                )
            )
        }
    }

    @Test
    fun testCountAndTotalHp() {
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

        val army = Armies.fromUnits(*units.entries.map { Pair(it.key, it.value) }.toTypedArray())

        assertEquals(24, army.count { true })
        assertEquals(0, army.count { false })

        assertEquals(25, army.totalHp { true })
        assertEquals(0, army.totalHp { false })

        UnitType.values().forEach { unitType ->
            val count = units[unitType] ?: 0
            assertEquals(count, army.count { it == unitType })
            assertEquals(count, army.count(unitType = unitType))
            assertEquals(count * unitType.maxHp, army.totalHp { it == unitType })
        }

        assertEquals(11, army.count { it.domain == Domain.LAND })
        assertEquals(4, army.count { it.domain == Domain.AIR })
        assertEquals(9, army.count { it.domain == Domain.SEA })

        assertEquals(11, army.totalHp { it.domain == Domain.LAND })
        assertEquals(4, army.totalHp { it.domain == Domain.AIR })
        assertEquals(10, army.totalHp { it.domain == Domain.SEA })
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
            Armies.attackers.forEach { attackers ->
                Armies.defenders.forEach { defenders ->
                    listOf(true, false).forEach { isOpeningFire ->
                        val attackerHits = attackers.rollHits(
                            rand = rand,
                            enemies = defenders,
                            isOpeningFire = isOpeningFire
                        )

                        val defenderHits = defenders.rollHits(
                            rand = rand,
                            enemies = attackers,
                            isOpeningFire = isOpeningFire
                        )

                        fun maxHits(army: Army, enemies: Army, domain: Domain?): Int {
                            return army.units.entries
                                .filter {
                                    it.key.targetDomain == domain &&
                                            it.key.hasOpeningFire(enemies) == isOpeningFire
                                }
                                .sumBy {
                                    it.value.size * it.key.numberOfRolls(
                                        enemies = enemies,
                                        isAttacking = army.isAttacking,
                                        weaponDevelopments = emptySet()
                                    )
                                }
                        }

                        assertTrue(attackerHits.generalHits <= maxHits(attackers, defenders, null))
                        assertTrue(defenderHits.generalHits <= maxHits(defenders, attackers, null))

                        attackerHits.domainHits.forEach { domain, domainHits ->
                            assertTrue(domainHits <= maxHits(attackers, defenders, domain))
                        }

                        defenderHits.domainHits.forEach { domain, domainHits ->
                            assertTrue(domainHits <= maxHits(defenders, attackers, domain))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testGetHitDistributionEmpty() {
        listOf(true, false).forEach { isOpeningFire ->
            assertEquals(
                emptyHitDistribution,
                Armies.empty.getHitDistribution(
                    enemies = Armies.empty,
                    isOpeningFire = isOpeningFire
                )
            )
        }
    }

    @Test
    fun testGetHitDistributionSingleton() {
        val attacking = Armies.fromUnits(UnitType.INFANTRY to 1, isAttacking = true)
        val defending = Armies.fromUnits(UnitType.INFANTRY to 1, isAttacking = false)

        assertEmptyHitDistribution(army = attacking, isOpeningFire = true)
        assertEmptyHitDistribution(army = defending, isOpeningFire = true)

        assertEquals(
            emptyHitDistribution.plusBinomial(domain = null, p = Rational(1, 6), n = 1),
            attacking.getHitDistribution(enemies = defending, isOpeningFire = false)
        )

        assertEquals(
            emptyHitDistribution.plusBinomial(domain = null, p = Rational(1, 3), n = 1),
            defending.getHitDistribution(enemies = attacking, isOpeningFire = false)
        )
    }

    @Test
    fun testGetHitDistributionArtilleryAndInfantry() {
        val attacking = Armies.fromUnits(
            UnitType.INFANTRY to 2, UnitType.ARTILLERY to 1,
            isAttacking = true
        )
        val defending = Armies.fromUnits(
            UnitType.INFANTRY to 2, UnitType.ARTILLERY to 1,
            isAttacking = false
        )

        assertEmptyHitDistribution(army = attacking, isOpeningFire = true)
        assertEmptyHitDistribution(army = defending, isOpeningFire = true)

        assertEquals(
            emptyHitDistribution
                .plusBinomial(domain = null, p = Rational(1, 3), n = 2)
                .plusBinomial(domain = null, p = Rational(1, 6), n = 1),
            attacking.getHitDistribution(enemies = defending, isOpeningFire = false)
        )

        assertEquals(
            emptyHitDistribution.plusBinomial(domain = null, p = Rational(1, 3), n = 3),
            defending.getHitDistribution(enemies = attacking, isOpeningFire = false)
        )
    }

    @Test
    fun testGetHitDistributionSubmarine() {
        val attacking = Armies.fromUnits(UnitType.SUBMARINE to 1, isAttacking = true)
        val defending = Armies.fromUnits(UnitType.SUBMARINE to 1, isAttacking = false)

        assertEmptyHitDistribution(army = attacking, isOpeningFire = false)
        assertEmptyHitDistribution(army = defending, isOpeningFire = false)

        assertEquals(
            emptyHitDistribution.plusBinomial(domain = Domain.SEA, p = Rational(1, 3), n = 1),
            attacking.getHitDistribution(enemies = defending, isOpeningFire = true)
        )

        assertEquals(
            emptyHitDistribution.plusBinomial(domain = Domain.SEA, p = Rational(1, 3), n = 1),
            defending.getHitDistribution(enemies = attacking, isOpeningFire = true)
        )
    }

    @Test
    fun testTakeHitsEmpty() {
        Armies.all.forEach { army ->
            assertEquals(
                army,
                army.takeHits(HitProfile(generalHits = 0, domainHits = mapOf()))
            )
        }
    }

    @Test
    fun testTakeHitsOverwhelming() {
        Armies.all.forEach { army ->
            assertEquals(
                army.copy(units = emptyMap()),
                army.takeHits(HitProfile(generalHits = army.totalHp { true }, domainHits = mapOf()))
            )

            assertEquals(
                army.copy(units = emptyMap()),
                army.takeHits(
                    HitProfile(
                        generalHits = 0,
                        domainHits = Domain.values().map { domain ->
                            domain to army.totalHp { it.domain == domain }
                        }.toMap()
                    )
                )
            )
        }
    }

    @Test
    fun testTakeHitsJustDamage() {
        val army = Armies.fromUnits(
            UnitType.BATTLESHIP to 5,
            UnitType.DESTROYER to 5
        )

        assertEquals(
            army.copy(
                units = mapOf(
                    UnitType.BATTLESHIP to multiSetOf(2 to 1, 1 to 4),
                    UnitType.DESTROYER to multiSetOf(1 to 5)
                )
            ),
            army.takeHits(HitProfile(generalHits = 2, domainHits = mapOf(Domain.SEA to 2)))
        )
    }

    @Test
    fun testTakeHitsMixed() {
        val army = Armies.fromUnits(
            UnitType.INFANTRY to 4,             // takes 2 land hits and 2 general hits
            UnitType.ARTILLERY to 1,            // takes 1 general hit
            UnitType.TANK to 3,

            UnitType.FIGHTER to 2,              // takes 1 air hit
            UnitType.BOMBER to 1,

            UnitType.BATTLESHIP to 2,           // takes 2 sea hits (as damage)
            UnitType.AIRCRAFT_CARRIER to 1,     // takes 1 sea hit
            UnitType.SUBMARINE to 1,

            UnitType.ANTIAIRCRAFT_GUN to 1,
            UnitType.BOMBARDING_BATTLESHIP to 2,

            isAttacking = true,
            casualtyPicker = CasualtyPicker.ByCombatPower()
        )

        assertEquals(
            army.copy(
                units = mapOf(
                    UnitType.TANK to multiSetOf(1 to 3),

                    UnitType.FIGHTER to multiSetOf(1 to 1),
                    UnitType.BOMBER to multiSetOf(1 to 1),

                    UnitType.BATTLESHIP to multiSetOf(1 to 2),
                    UnitType.SUBMARINE to multiSetOf(1 to 1),

                    UnitType.ANTIAIRCRAFT_GUN to multiSetOf(1 to 1),
                    UnitType.BOMBARDING_BATTLESHIP to multiSetOf(1 to 2)
                )
            ),
            army.takeHits(
                hits = HitProfile(
                    generalHits = 3,
                    domainHits = mapOf(
                        Domain.SEA to 3,
                        Domain.LAND to 2,
                        Domain.AIR to 1
                    )
                )
            )
        )
    }

    private fun assertEmptyHitDistribution(army: Army, isOpeningFire: Boolean) {
        assertEquals(
            emptyHitDistribution,
            army.getHitDistribution(
                enemies = army,
                isOpeningFire = isOpeningFire
            )
        )
    }
}
