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
        assertEquals(5040.toBigInteger(), factorial(7))
    }

    @Test
    fun testFactorialFrom() {
        assertEquals(2.toBigInteger(), factorialFrom(n = 2, k = 0))
        assertEquals(2.toBigInteger(), factorialFrom(n = 2, k = 1))

        assertEquals(6.toBigInteger(), factorialFrom(n = 3, k = 0))
        assertEquals(6.toBigInteger(), factorialFrom(n = 3, k = 1))
        assertEquals(3.toBigInteger(), factorialFrom(n = 3, k = 2))

        assertEquals(24.toBigInteger(), factorialFrom(n = 4, k = 0))
        assertEquals(24.toBigInteger(), factorialFrom(n = 4, k = 1))
        assertEquals(12.toBigInteger(), factorialFrom(n = 4, k = 2))
        assertEquals(4.toBigInteger(), factorialFrom(n = 4, k = 3))

        assertEquals(120.toBigInteger(), factorialFrom(n = 5, k = 0))
        assertEquals(120.toBigInteger(), factorialFrom(n = 5, k = 1))
        assertEquals(60.toBigInteger(), factorialFrom(n = 5, k = 2))
        assertEquals(20.toBigInteger(), factorialFrom(n = 5, k = 3))
        assertEquals(5.toBigInteger(), factorialFrom(n = 5, k = 4))

        assertEquals(720.toBigInteger(), factorialFrom(n = 6, k = 0))
        assertEquals(720.toBigInteger(), factorialFrom(n = 6, k = 1))
        assertEquals(360.toBigInteger(), factorialFrom(n = 6, k = 2))
        assertEquals(120.toBigInteger(), factorialFrom(n = 6, k = 3))
        assertEquals(30.toBigInteger(), factorialFrom(n = 6, k = 4))
        assertEquals(6.toBigInteger(), factorialFrom(n = 6, k = 5))

        assertEquals(9900.toBigInteger(), factorialFrom(n = 100, k = 98))

        assertEquals((998 * 999 * 1000).toBigInteger(), factorialFrom(n = 1000, k = 997))
    }

    @Test
    fun testBinomial() {
        assertEquals(Rational.ZERO, binomial(p = Rational(1, 2), n = 0, k = 1))
        assertEquals(Rational.ZERO, binomial(p = Rational(1, 2), n = 3, k = 4))

        assertEquals(Rational.ZERO, binomial(p = Rational(1, 2), n = 3, k = -1))
        assertEquals(Rational.ZERO, binomial(p = Rational(1, 2), n = 0, k = -2))

        assertEquals(Rational.ONE, binomial(p = Rational(1, 2), n = 0, k = 0))

        assertEquals(Rational.ONE,  binomial(p = Rational.ZERO, n = 3, k = 0))
        assertEquals(Rational.ZERO, binomial(p = Rational.ZERO, n = 3, k = 1))
        assertEquals(Rational.ZERO, binomial(p = Rational.ZERO, n = 3, k = 2))
        assertEquals(Rational.ZERO, binomial(p = Rational.ZERO, n = 3, k = 3))

        assertEquals(Rational.ZERO, binomial(p = Rational.ONE, n = 3, k = 0))
        assertEquals(Rational.ZERO, binomial(p = Rational.ONE, n = 3, k = 1))
        assertEquals(Rational.ZERO, binomial(p = Rational.ONE, n = 3, k = 2))
        assertEquals(Rational.ONE,  binomial(p = Rational.ONE, n = 3, k = 3))

        assertEquals(Rational(1,  32), binomial(p = Rational(1, 2), n = 5, k = 0))
        assertEquals(Rational(5,  32), binomial(p = Rational(1, 2), n = 5, k = 1))
        assertEquals(Rational(10, 32), binomial(p = Rational(1, 2), n = 5, k = 2))
        assertEquals(Rational(10, 32), binomial(p = Rational(1, 2), n = 5, k = 3))
        assertEquals(Rational(5,  32), binomial(p = Rational(1, 2), n = 5, k = 4))
        assertEquals(Rational(1,  32), binomial(p = Rational(1, 2), n = 5, k = 5))

        assertEquals(Rational(1  * 64, 729), binomial(p = Rational(1, 3), n = 6, k = 0))
        assertEquals(Rational(6  * 32, 729), binomial(p = Rational(1, 3), n = 6, k = 1))
        assertEquals(Rational(15 * 16, 729), binomial(p = Rational(1, 3), n = 6, k = 2))
        assertEquals(Rational(20 * 8,  729), binomial(p = Rational(1, 3), n = 6, k = 3))
        assertEquals(Rational(15 * 4,  729), binomial(p = Rational(1, 3), n = 6, k = 4))
        assertEquals(Rational(6  * 2,  729), binomial(p = Rational(1, 3), n = 6, k = 5))
        assertEquals(Rational(1  * 1,  729), binomial(p = Rational(1, 3), n = 6, k = 6))
    }
}
