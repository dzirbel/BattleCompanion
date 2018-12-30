package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational
import java.math.BigInteger

fun factorial(n: Int): BigInteger {
    var result = BigInteger.ONE
    for (k in 2..n) {
        result *= k.toBigInteger()
    }
    return result
}

fun binomial(p: Rational, n: Int, k: Int): Rational {
    if (n < 0 || k < 0) {
        throw IllegalArgumentException()
    }

    if (n < k) {
        return Rational.ZERO
    }

    // TODO lots of ways to optimize this
    val coefficient = Rational(factorial(n), factorial(k) * factorial(n - k))
    return coefficient * p.exp(k) * p.oneMinus().exp(n - k)
}
