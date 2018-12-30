package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational
import org.junit.Assert.assertEquals
import org.junit.Test

class HitDistributionTest {

    @Test
    fun testEmpty() {
        assertEquals(
            mapOf(HitProfile(generalHits = 0, domainHits = emptyMap()) to Rational.ONE),
            emptyHitDistribution
        )
    }

    @Test
    fun testPlusBinomial() {
        var hitDistribution = emptyHitDistribution

        hitDistribution = hitDistribution.plusBinomial(domain = null, p = Rational(1, 2), n = 1)
        assertEquals(
            mapOf(
                HitProfile(generalHits = 0, domainHits = emptyMap()) to Rational(1, 2),
                HitProfile(generalHits = 1, domainHits = emptyMap()) to Rational(1, 2)
            ),
            hitDistribution
        )

        hitDistribution = hitDistribution.plusBinomial(domain = null, p = Rational(1, 2), n = 1)
        assertEquals(
            mapOf(
                HitProfile(generalHits = 0, domainHits = emptyMap()) to Rational(1, 4),
                HitProfile(generalHits = 1, domainHits = emptyMap()) to Rational(1, 2),
                HitProfile(generalHits = 2, domainHits = emptyMap()) to Rational(1, 4)
            ),
            hitDistribution
        )

        assertEquals(
            emptyHitDistribution.plusBinomial(domain = null, p = Rational(1, 2), n = 2),
            hitDistribution
        )

        hitDistribution = hitDistribution.plusBinomial(
            domain = Domain.LAND,
            p = Rational(1, 3),
            n = 3
        )
        assertEquals(
            mapOf(
                HitProfile(
                    generalHits = 0,
                    domainHits = emptyMap()
                ) to Rational(1, 4) * Rational(8, 27),
                HitProfile(
                    generalHits = 0,
                    domainHits = mapOf(Domain.LAND to 1)
                ) to Rational(1, 4) * Rational(4, 9),
                HitProfile(
                    generalHits = 0,
                    domainHits = mapOf(Domain.LAND to 2)
                ) to Rational(1, 4) * Rational(2, 9),
                HitProfile(
                    generalHits = 0,
                    domainHits = mapOf(Domain.LAND to 3)
                ) to Rational(1, 4) * Rational(1, 27),

                HitProfile(
                    generalHits = 1,
                    domainHits = emptyMap()
                ) to Rational(1, 2) * Rational(8, 27),
                HitProfile(
                    generalHits = 1,
                    domainHits = mapOf(Domain.LAND to 1)
                ) to Rational(1, 2) * Rational(4, 9),
                HitProfile(
                    generalHits = 1,
                    domainHits = mapOf(Domain.LAND to 2)
                ) to Rational(1, 2) * Rational(2, 9),
                HitProfile(
                    generalHits = 1,
                    domainHits = mapOf(Domain.LAND to 3)
                ) to Rational(1, 2) * Rational(1, 27),

                HitProfile(
                    generalHits = 2,
                    domainHits = emptyMap()
                ) to Rational(1, 4) * Rational(8, 27),
                HitProfile(
                    generalHits = 2,
                    domainHits = mapOf(Domain.LAND to 1)
                ) to Rational(1, 4) * Rational(4, 9),
                HitProfile(
                    generalHits = 2,
                    domainHits = mapOf(Domain.LAND to 2)
                ) to Rational(1, 4) * Rational(2, 9),
                HitProfile(
                    generalHits = 2,
                    domainHits = mapOf(Domain.LAND to 3)
                ) to Rational(1, 4) * Rational(1, 27)
            ),
            hitDistribution
        )

        val p1 = Rational(1, 3)
        val n1 = 4

        val p2 = Rational(3, 4)
        val n2 = 3

        fun totalProb(k: Int): Rational {
            return List(k + 1) { j ->
                binomial(p = p1, n = n1, k = j) * binomial(p = p2, n = n2, k = k - j)
            }.reduce(Rational::plus)
        }

        assertEquals(
            mapOf(
                HitProfile(generalHits = 0, domainHits = emptyMap()) to totalProb(0),
                HitProfile(generalHits = 1, domainHits = emptyMap()) to totalProb(1),
                HitProfile(generalHits = 2, domainHits = emptyMap()) to totalProb(2),
                HitProfile(generalHits = 3, domainHits = emptyMap()) to totalProb(3),
                HitProfile(generalHits = 4, domainHits = emptyMap()) to totalProb(4),
                HitProfile(generalHits = 5, domainHits = emptyMap()) to totalProb(5),
                HitProfile(generalHits = 6, domainHits = emptyMap()) to totalProb(6),
                HitProfile(generalHits = 7, domainHits = emptyMap()) to totalProb(7)
            ),
            emptyHitDistribution
                .plusBinomial(domain = null, p = p1, n = n1)
                .plusBinomial(domain = null, p = p2, n = n2)
        )

        assertEquals(
            emptyHitDistribution
                .plusBinomial(domain = null, p = Rational(1, 3), n = 4)
                .plusBinomial(domain = null, p = Rational(3, 4), n = 3),
            emptyHitDistribution
                .plusBinomial(domain = null, p = Rational(3, 4), n = 1)
                .plusBinomial(domain = null, p = Rational(1, 3), n = 2)
                .plusBinomial(domain = null, p = Rational(3, 4), n = 1)
                .plusBinomial(domain = null, p = Rational(1, 3), n = 2)
                .plusBinomial(domain = null, p = Rational(3, 4), n = 1)
        )
    }
}
