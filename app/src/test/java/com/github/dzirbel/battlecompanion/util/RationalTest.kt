package com.github.dzirbel.battlecompanion.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class RationalTest {

    @Test
    fun testEquality() {
        assertEquals(Rational(2, 1), Rational(4, 2))
        assertEquals(Rational(1, 2), Rational(2, 4))
        assertEquals(Rational(2, 3), Rational(4, 6))
        assertEquals(Rational(3, 8), Rational(1155, 3080))
        assertEquals(Rational.ZERO, Rational(0, 5))
        assertEquals(Rational.ONE, Rational(3, 3))

        assertNotEquals(Rational(1, 2), Rational(2, 3))
        assertNotEquals(Rational(2, 1), Rational(3, 1))
    }

    @Test
    fun testPlus() {
        assertEquals(Rational(3, 4), Rational(1, 2) + Rational(1, 4))
        assertEquals(Rational(3, 4), Rational(1, 4) + Rational(1, 2))
        assertEquals(Rational(2, 3), Rational(1, 27) + Rational(17, 27))

        assertEquals(Rational.ZERO, Rational.ZERO + Rational.ZERO)
        assertEquals(Rational.ONE, Rational.ONE + Rational.ZERO)
        assertEquals(Rational.ONE, Rational.ZERO + Rational.ONE)
    }

    @Test
    fun testTimes() {
        assertEquals(Rational(1, 4), Rational(1, 2) * Rational(1, 2))
        assertEquals(Rational(2, 21), Rational(1, 3) * Rational(2, 7))

        assertEquals(Rational.ZERO, Rational.ZERO * Rational.ZERO)
        assertEquals(Rational.ZERO, Rational.ONE * Rational.ZERO)
        assertEquals(Rational.ZERO, Rational.ZERO * Rational.ONE)
    }

    @Test
    fun testDiv() {
        assertEquals(Rational.ONE, Rational(1, 2) / Rational(1, 2))
        assertEquals(Rational(1, 4), Rational(1, 2) / Rational(2, 1))
        assertEquals(Rational(7, 6), Rational(1, 3) / Rational(2, 7))

        assertEquals(Rational.ONE, Rational.ONE / Rational.ONE)
    }

    @Test
    fun testExp() {
        assertEquals(Rational.ONE, Rational(1, 2).exp(0))
        assertEquals(Rational(1, 2), Rational(1, 2).exp(1))
        assertEquals(Rational(1, 4), Rational(1, 2).exp(2))
        assertEquals(Rational(1, 8), Rational(1, 2).exp(3))
        assertEquals(Rational(1, 16), Rational(1, 2).exp(4))
        assertEquals(Rational(1, 32), Rational(1, 2).exp(5))

        assertEquals(Rational.ONE, Rational(3, 11).exp(0))
        assertEquals(Rational(3, 11), Rational(3, 11).exp(1))
        assertEquals(Rational(9, 121), Rational(3, 11).exp(2))
        assertEquals(Rational(27, 1331), Rational(3, 11).exp(3))
    }

    @Test
    fun testOneMinus() {
        assertEquals(Rational.ZERO, Rational.ONE.oneMinus())
        assertEquals(Rational(1, 2), Rational(1, 2).oneMinus())
        assertEquals(Rational(1, 3), Rational(2, 3).oneMinus())
        assertEquals(Rational(2, 3), Rational(1, 3).oneMinus())
        assertEquals(Rational(3, 11), Rational(8, 11).oneMinus())
        assertEquals(Rational(8, 11), Rational(3, 11).oneMinus())
    }
}
