package com.github.dzirbel.battlecompanion.util

import org.junit.Assert
import org.junit.Test

class FactoredRationalTest {

    @Test
    fun testEquality() {
        Assert.assertEquals(FactoredRational(2, 1), FactoredRational(4, 2))
        Assert.assertEquals(FactoredRational(1, 2), FactoredRational(2, 4))
        Assert.assertEquals(FactoredRational(2, 3), FactoredRational(4, 6))
        Assert.assertEquals(FactoredRational(3, 8), FactoredRational(1155, 3080))
        Assert.assertEquals(FactoredRational(2, 21), FactoredRational(10, 105))
        Assert.assertEquals(FactoredRational.ZERO, FactoredRational(0, 5))
        Assert.assertEquals(FactoredRational.ONE, FactoredRational(3, 3))

        Assert.assertNotEquals(FactoredRational(1, 2), FactoredRational(2, 3))
        Assert.assertNotEquals(FactoredRational(2, 1), FactoredRational(3, 1))
    }

    @Test
    fun testPlus() {
        Assert.assertEquals(FactoredRational(3, 4), FactoredRational(1, 2) + FactoredRational(1, 4))
        Assert.assertEquals(FactoredRational(3, 4), FactoredRational(1, 4) + FactoredRational(1, 2))
        Assert.assertEquals(
            FactoredRational(2, 3),
            FactoredRational(1, 27) + FactoredRational(17, 27)
        )

        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ZERO + FactoredRational.ZERO)
        Assert.assertEquals(FactoredRational.ONE, FactoredRational.ONE + FactoredRational.ZERO)
        Assert.assertEquals(FactoredRational.ONE, FactoredRational.ZERO + FactoredRational.ONE)
    }

    @Test
    fun testMinus() {
        Assert.assertEquals(FactoredRational(1, 4), FactoredRational(1, 2) - FactoredRational(1, 4))
        Assert.assertEquals(FactoredRational(1, 4), FactoredRational(3, 8) - FactoredRational(1, 8))
        Assert.assertEquals(
            FactoredRational(1, 27),
            FactoredRational(2, 3) - FactoredRational(17, 27)
        )

        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ZERO - FactoredRational.ZERO)
        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ONE - FactoredRational.ONE)
        Assert.assertEquals(FactoredRational.ONE, FactoredRational.ONE - FactoredRational.ZERO)
    }

    @Test
    fun testTimes() {
        Assert.assertEquals(FactoredRational(1, 4), FactoredRational(1, 2) * FactoredRational(1, 2))
        Assert.assertEquals(
            FactoredRational(2, 21),
            FactoredRational(1, 3) * FactoredRational(2, 7)
        )
        Assert.assertEquals(
            FactoredRational(2, 21),
            FactoredRational(2, 15) * FactoredRational(5, 7)
        )

        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ZERO * FactoredRational.ZERO)
        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ONE * FactoredRational.ZERO)
        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ZERO * FactoredRational.ONE)
    }

    @Test
    fun testTimesBigInteger() {
        Assert.assertEquals(FactoredRational(1, 2), FactoredRational(1, 4) * 2.toBigInteger())
        Assert.assertEquals(FactoredRational.ONE, FactoredRational(1, 2) * 2.toBigInteger())
        Assert.assertEquals(FactoredRational(3, 8), FactoredRational(1, 8) * 3.toBigInteger())
        Assert.assertEquals(FactoredRational(3, 2), FactoredRational(1, 4) * 6.toBigInteger())
    }

    @Test
    fun testDiv() {
        Assert.assertEquals(FactoredRational.ONE, FactoredRational(1, 2) / FactoredRational(1, 2))
        Assert.assertEquals(FactoredRational(1, 4), FactoredRational(1, 2) / FactoredRational(2, 1))
        Assert.assertEquals(FactoredRational(7, 6), FactoredRational(1, 3) / FactoredRational(2, 7))

        Assert.assertEquals(FactoredRational.ONE, FactoredRational.ONE / FactoredRational.ONE)
    }

    @Test
    fun testExp() {
        Assert.assertEquals(FactoredRational.ONE, FactoredRational(1, 2).exp(0))
        Assert.assertEquals(FactoredRational(1, 2), FactoredRational(1, 2).exp(1))
        Assert.assertEquals(FactoredRational(1, 4), FactoredRational(1, 2).exp(2))
        Assert.assertEquals(FactoredRational(1, 8), FactoredRational(1, 2).exp(3))
        Assert.assertEquals(FactoredRational(1, 16), FactoredRational(1, 2).exp(4))
        Assert.assertEquals(FactoredRational(1, 32), FactoredRational(1, 2).exp(5))

        Assert.assertEquals(FactoredRational.ONE, FactoredRational(3, 11).exp(0))
        Assert.assertEquals(FactoredRational(3, 11), FactoredRational(3, 11).exp(1))
        Assert.assertEquals(FactoredRational(9, 121), FactoredRational(3, 11).exp(2))
        Assert.assertEquals(FactoredRational(27, 1331), FactoredRational(3, 11).exp(3))
    }

    @Test
    fun testOneMinus() {
        Assert.assertEquals(FactoredRational.ZERO, FactoredRational.ONE.oneMinus())
        Assert.assertEquals(FactoredRational.ONE, FactoredRational.ZERO.oneMinus())
        Assert.assertEquals(FactoredRational(1, 2), FactoredRational(1, 2).oneMinus())
        Assert.assertEquals(FactoredRational(1, 3), FactoredRational(2, 3).oneMinus())
        Assert.assertEquals(FactoredRational(2, 3), FactoredRational(1, 3).oneMinus())
        Assert.assertEquals(FactoredRational(3, 11), FactoredRational(8, 11).oneMinus())
        Assert.assertEquals(FactoredRational(8, 11), FactoredRational(3, 11).oneMinus())
    }
}
