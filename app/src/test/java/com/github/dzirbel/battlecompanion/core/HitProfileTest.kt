package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HitProfileTest {

    @Test
    fun testIsEmpty() {
        assertTrue(HitProfile(generalHits = 0, domainHits = emptyMap()).isEmpty())

        assertFalse(HitProfile(generalHits = 1, domainHits = emptyMap()).isEmpty())
        assertFalse(HitProfile(generalHits = 0, domainHits = mapOf(Domain.LAND to 1)).isEmpty())
        assertFalse(HitProfile(generalHits = 0, domainHits = mapOf(Domain.AIR to 1)).isEmpty())
        assertFalse(HitProfile(generalHits = 0, domainHits = mapOf(Domain.SEA to 1)).isEmpty())
        assertFalse(
            HitProfile(
                generalHits = 4,
                domainHits = mapOf(Domain.SEA to 1, Domain.LAND to 2, Domain.AIR to 3)
            ).isEmpty()
        )
    }

    @Test
    fun testPlus() {
        var hitProfile = HitProfile(generalHits = 0, domainHits = emptyMap())
        hitProfile.assertEquals()

        hitProfile = hitProfile.plus(2, null)
        hitProfile.assertEquals(generalHits = 2)

        hitProfile = hitProfile.plus(1, Domain.LAND)
        hitProfile.assertEquals(generalHits = 2, domainHits = mapOf(Domain.LAND to 1))

        hitProfile = hitProfile.plus(3, null)
        hitProfile.assertEquals(generalHits = 5, domainHits = mapOf(Domain.LAND to 1))

        hitProfile = hitProfile.plus(1, Domain.LAND)
        hitProfile.assertEquals(generalHits = 5, domainHits = mapOf(Domain.LAND to 2))

        hitProfile = hitProfile.plus(5, Domain.AIR)
        hitProfile.assertEquals(
            generalHits = 5,
            domainHits = mapOf(Domain.LAND to 2, Domain.AIR to 5)
        )

        hitProfile = hitProfile.plus(1, null)
        hitProfile.assertEquals(
            generalHits = 6,
            domainHits = mapOf(Domain.LAND to 2, Domain.AIR to 5)
        )

        hitProfile = hitProfile.plus(3, Domain.SEA)
        hitProfile.assertEquals(
            generalHits = 6,
            domainHits = mapOf(Domain.LAND to 2, Domain.AIR to 5, Domain.SEA to 3)
        )
    }

    private fun HitProfile.assertEquals(
        generalHits: Int = 0,
        domainHits: Map<Domain, Int> = emptyMap()
    ) {
        assertEquals(this, HitProfile(generalHits = generalHits, domainHits = domainHits))
        assertEquals(this, this.plus(0, null))
        assertEquals(this, this.plus(0, Domain.LAND))
        assertEquals(this, this.plus(0, Domain.AIR))
        assertEquals(this, this.plus(0, Domain.SEA))
    }
}
