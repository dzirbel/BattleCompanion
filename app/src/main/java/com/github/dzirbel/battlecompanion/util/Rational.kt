package com.github.dzirbel.battlecompanion.util

import java.math.BigInteger
import java.math.MathContext

/**
 * Arbitrary precision representation of rational numbers as numerator [p] and denominator [q].
 *
 * TODO support NaN (0/0) and +/- infinity (+/-1/0)
 */
class Rational private constructor(
    p: BigInteger,
    q: BigInteger,
    reduce: Boolean
) : Number() {

    constructor(p: Int, q: Int) : this(p = p.toBigInteger(), q = q.toBigInteger(), reduce = true)

    private val p: BigInteger
    private val q: BigInteger

    init {
        when {
            q.signum() <= 0 -> throw IllegalArgumentException()
            p.signum() == 0 -> {
                this.p = BigInteger.ZERO
                this.q = BigInteger.ONE
            }
            reduce -> {
                val gcd = p.gcd(q)
                this.p = p / gcd
                this.q = q / gcd
            }
            else -> {
                this.p = p
                this.q = q
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Rational && other.p == p && other.q == q
    }

    override fun hashCode(): Int {
        return p.toInt() + 17 * q.toInt()
    }

    override fun toString(): String {
        return "$p / $q"
    }

    override fun toByte() = (p / q).toByte()
    override fun toChar() = (p / q).toChar()
    override fun toShort() = (p / q).toShort()
    override fun toInt() = (p / q).toInt()
    override fun toLong() = (p / q).toLong()

    override fun toDouble(): Double {
        return p.toBigDecimal().divide(q.toBigDecimal(), MathContext.DECIMAL64).toDouble()
    }

    override fun toFloat(): Float {
        return p.toBigDecimal().divide(q.toBigDecimal(), MathContext.DECIMAL32).toFloat()
    }

    operator fun plus(other: Rational): Rational {
        return Rational(
            p = (p * other.q) + (other.p * q),
            q = q * other.q,
            reduce = true
        )
    }

    operator fun minus(other: Rational): Rational {
        return Rational(
            p = (p * other.q) - (other.p * q),
            q = q * other.q,
            reduce = true
        )
    }

    operator fun times(other: Rational): Rational {
        val gcd1 = p.gcd(other.q)
        val gcd2 = q.gcd(other.p)
        return Rational(
            p = (p / gcd1) * (other.p / gcd2),
            q = (q / gcd2) * (other.q / gcd1),
            reduce = false
        )
    }

    operator fun div(other: Rational): Rational {
        val gcd1 = p.gcd(other.p)
        val gcd2 = q.gcd(other.q)
        return Rational(
            p = (p / gcd1) * (other.q / gcd2),
            q = (q / gcd2) * (other.p / gcd1),
            reduce = false
        )
    }

    operator fun times(other: BigInteger): Rational {
        val gcd = q.gcd(other)
        return Rational(p = p * (other / gcd), q = q / gcd, reduce = false)
    }

    /**
     * Returns the [n]th exponent of this [Rational], i.e. `(p/q)^n`.
     */
    fun exp(n: Int): Rational {
        if (n == 0) return ONE

        return Rational(
            p = p.pow(n),
            q = q.pow(n),
            reduce = false
        )
    }

    /**
     * Returns [ONE] minus this [Rational].
     */
    fun oneMinus(): Rational {
        return Rational(p = q - p, q = q, reduce = false)
    }

    companion object {

        val ZERO = Rational(BigInteger.ZERO, BigInteger.ONE, reduce = false)
        val ONE = Rational(BigInteger.ONE, BigInteger.ONE, reduce = false)
    }
}
