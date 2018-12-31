package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational

/**
 * Computes the factorial of [n], i.e. `1 * 2 * 3 * ... * n` as a [Long] to avoid overflow.
 */
fun factorial(n: Int): Long {
    var result = 1L
    for (k in 2..n) {
        result *= k
    }
    return result
}

/**
 * Computes `n! / k!`, i.e. the factorial of [n] divided by the factorial of [k] (as a [Long] to
 *  avoid overflow).
 * To avoid unnecessary multiplication, we compute the quotient by `(k+1) * (k+2) * ... * n`.
 * Returns 1 if [n] equals [k] and throws [IllegalArgumentException] if [n] is less than [k].
 */
fun factorialFrom(n: Int, k: Int): Long {
    if (n < k) throw IllegalArgumentException()
    if (n == k) return 1L

    var result = (k + 1).toLong()
    for (j in (k + 2)..n) {
        result *= j
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

            (p.exp(k) * p.oneMinus().exp(n - k)) * coefficient.toBigInteger()
        }
    }
}
