package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational
import org.junit.Assert.assertEquals
import org.junit.Test

class MathTest {

    @Test
    fun testFactorial() {
        assertEquals(1.toBigInteger(), factorial(1))
        assertEquals(2.toBigInteger(), factorial(2))
        assertEquals(6.toBigInteger(), factorial(3))
        assertEquals(24.toBigInteger(), factorial(4))
        assertEquals(120.toBigInteger(), factorial(5))
        assertEquals(720.toBigInteger(), factorial(6))
    }

    @Test
    fun testBinomial() {
        assertEquals(Rational.ZERO, binomial(p = Rational(1, 2), n = 0, k = 1))
        assertEquals(Rational.ZERO, binomial(p = Rational(1, 2), n = 3, k = 4))

        assertEquals(Rational.ONE, binomial(p = Rational(1, 2), n = 0, k = 0))

        assertEquals(Rational.ONE,  binomial(p = Rational.ZERO, n = 3, k = 0))
        assertEquals(Rational.ZERO, binomial(p = Rational.ZERO, n = 3, k = 1))
        assertEquals(Rational.ZERO, binomial(p = Rational.ZERO, n = 3, k = 2))
        assertEquals(Rational.ZERO, binomial(p = Rational.ZERO, n = 3, k = 3))

        assertEquals(Rational.ZERO, binomial(p = Rational.ONE, n = 3, k = 0))
        assertEquals(Rational.ZERO, binomial(p = Rational.ONE, n = 3, k = 1))
        assertEquals(Rational.ZERO, binomial(p = Rational.ONE, n = 3, k = 2))
        assertEquals(Rational.ONE,  binomial(p = Rational.ONE, n = 3, k = 3))

        assertEquals(Rational(1, 32),  binomial(p = Rational(1, 2), n = 5, k = 0))
        assertEquals(Rational(5, 32),  binomial(p = Rational(1, 2), n = 5, k = 1))
        assertEquals(Rational(10, 32), binomial(p = Rational(1, 2), n = 5, k = 2))
        assertEquals(Rational(10, 32), binomial(p = Rational(1, 2), n = 5, k = 3))
        assertEquals(Rational(5, 32),  binomial(p = Rational(1, 2), n = 5, k = 4))
        assertEquals(Rational(1, 32),  binomial(p = Rational(1, 2), n = 5, k = 5))
    }
}
