package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational
import java.math.BigInteger

/**
 * Computes the factorial of [n], i.e. `1 * 2 * 3 * ... * n` as a [BigInteger] to avoid overflow.
 */
fun factorial(n: Int): BigInteger {
    var result = BigInteger.ONE
    for (k in 2..n) {
        result *= k.toBigInteger()
    }
    return result
}

/**
 * Computes `n! / k!`, i.e. the factorial of [n] divided by the factorial of [k] (as a [BigInteger]
 *  to avoid overflow).
 * To avoid unnecessary multiplication, we compute the quotient by `(k+1) * (k+2) * ... * n`.
 * Returns [BigInteger.ONE] if [n] equals [k] and throws [IllegalArgumentException] if [n] is less
 *  than [k].
 */
fun factorialFrom(n: Int, k: Int): BigInteger {
    if (n < k) throw IllegalArgumentException()
    if (n == k) return BigInteger.ONE

    var result = (k + 1).toBigInteger()
    for (j in (k + 2)..n) {
        result *= j.toBigInteger()
    }
    return result
}

/**
 * Computes the binomial distribution of [n] and [p] at [k]; that is, the probability that among [n]
 *  independent trials each with success probability [p], there are exactly [k] successes.
 * Returns [Rational.ZERO] if the given value of [n] or [k] is out of range (i.e. [k] or [n] are
 *  negative or [k] is greater than [n]).
 */
fun binomial(p: Rational, n: Int, k: Int): Rational {
    return when {
        k < 0 || n < k -> Rational.ZERO
        k == 0 -> p.oneMinus().exp(n)
        k == n -> p.exp(n)
        else -> {
            val coefficient =
                factorialFrom(n = n, k = Math.max(k, n - k)) / factorial(Math.min(k, n - k))

            (p.exp(k) * p.oneMinus().exp(n - k)) * coefficient
        }
    }
}
