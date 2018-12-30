package com.github.dzirbel.battlecompanion.util

import java.math.BigInteger

class Rational(
    numerator: BigInteger,
    denominator: BigInteger
) : Number() {

    constructor(numerator: Int, denominator: Int) :
            this(numerator = numerator.toBigInteger(), denominator = denominator.toBigInteger())

    val numerator: BigInteger
    val denominator: BigInteger

    init {
        when {
            denominator.signum() <= 0 -> throw IllegalArgumentException()
            numerator.signum() == 0 -> {
                this.numerator = BigInteger.ZERO
                this.denominator = BigInteger.ONE
            }
            else -> {
                val gcd = numerator.gcd(denominator)
                this.numerator = numerator / gcd
                this.denominator = denominator / gcd
            }
        }
    }

    override fun toByte() = (numerator.toDouble() / denominator.toDouble()).toByte()
    override fun toChar() = (numerator.toDouble() / denominator.toDouble()).toChar()
    override fun toDouble() = numerator.toDouble() / denominator.toDouble()
    override fun toFloat() = numerator.toFloat() / denominator.toFloat()
    override fun toInt() = (numerator.toDouble() / denominator.toDouble()).toInt()
    override fun toLong() = (numerator.toDouble() / denominator.toDouble()).toLong()
    override fun toShort() = (numerator.toDouble() / denominator.toDouble()).toShort()

    override fun equals(other: Any?): Boolean {
        return other is Rational && other.numerator == numerator && other.denominator == denominator
    }

    override fun hashCode(): Int {
        return numerator.toInt() + 17 * denominator.toInt()
    }

    override fun toString(): String {
        return "$numerator / $denominator"
    }

    operator fun plus(other: Rational): Rational {
        return Rational(
            numerator = (numerator * other.denominator) + (denominator * other.numerator),
            denominator = denominator * other.denominator
        )
    }

    operator fun times(other: Rational): Rational {
        return Rational(
            numerator = numerator * other.numerator,
            denominator = denominator * other.denominator
        )
    }

    operator fun div(other: Rational): Rational {
        return times(Rational(other.denominator, other.numerator))
    }

    fun exp(n: Int): Rational {
        if (n == 0) return Rational.ONE

        return Rational(
            numerator = numerator.pow(n),
            denominator = denominator.pow(n)
        )
    }

    fun oneMinus(): Rational {
        return Rational(denominator - numerator, denominator)
    }

    companion object {

        val ZERO = Rational(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Rational(BigInteger.ONE, BigInteger.ONE)
    }
}
