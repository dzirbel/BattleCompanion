package com.github.dzirbel.battlecompanion.util

import com.github.dzirbel.battlecompanion.core.factor
import java.math.BigInteger
import java.math.MathContext

class FactoredRational private constructor(
    numerator: MultiSet<Long>,
    denominator: MultiSet<Long>,
    reduce: Boolean
) : Number() {

    constructor(
        numerator: Int,
        denominator: Int
    ) : this(
        numerator = factor(numerator.toBigInteger()),
        denominator = factor(denominator.toBigInteger()),
        reduce = true
    )

    // TODO check for negative factors
    val numerator: MultiSet<Long>
    val denominator: MultiSet<Long>
    val isZero: Boolean

    private fun MultiSet<Long>.toBigInteger(): BigInteger {
        return when {
            isEmpty() -> BigInteger.ONE
            else -> map { it.toBigInteger() }.reduce(BigInteger::times)
        }
    }

    init {
        when {
            numerator.countOf(0L) > 0 -> {
                this.numerator = MultiSet(mapOf(0L to 1))
                this.denominator = MultiSet()
                isZero = true
            }
            reduce -> {
                // TODO does this work? do we need to intersect before subtracting?
                this.numerator = numerator.minus(denominator)
                this.denominator = denominator.minus(numerator)
                isZero = false
            }
            else -> {
                this.numerator = numerator
                this.denominator = denominator
                isZero = false
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is FactoredRational &&
                other.numerator == numerator && other.denominator == denominator
    }

    override fun hashCode(): Int {
        return numerator.hashCode() + 17 * denominator.hashCode()
    }

    override fun toString(): String {
        return "($numerator) / ($denominator)"
    }

    override fun toByte() = (numerator.toBigInteger() / denominator.toBigInteger()).toByte()
    override fun toChar() = (numerator.toBigInteger() / denominator.toBigInteger()).toChar()
    override fun toShort() = (numerator.toBigInteger() / denominator.toBigInteger()).toShort()
    override fun toInt() = (numerator.toBigInteger() / denominator.toBigInteger()).toInt()
    override fun toLong() = (numerator.toBigInteger() / denominator.toBigInteger()).toLong()

    override fun toDouble(): Double {
        return numerator.toBigInteger().toBigDecimal()
            .divide(denominator.toBigInteger().toBigDecimal(), MathContext.DECIMAL64)
            .toDouble()
    }

    override fun toFloat(): Float {
        return numerator.toBigInteger().toBigDecimal()
            .divide(denominator.toBigInteger().toBigDecimal(), MathContext.DECIMAL32)
            .toFloat()
    }

    operator fun plus(other: FactoredRational): FactoredRational {
        if (isZero) return other
        if (other.isZero) return this

        return FactoredRational(
            numerator = factor(
                numerator.plus(other.denominator).toBigInteger() +
                        other.numerator.plus(denominator).toBigInteger()
            ),
            denominator = denominator.plus(other.denominator),
            reduce = true
        )
    }

    operator fun minus(other: FactoredRational): FactoredRational {
        if (other.isZero) return this

        return FactoredRational(
            numerator = factor(
                numerator.plus(other.denominator).toBigInteger() -
                        other.numerator.plus(denominator).toBigInteger()
            ),
            denominator = denominator.plus(other.denominator),
            reduce = true
        )
    }

    operator fun times(other: FactoredRational): FactoredRational {
        if (isZero || other.isZero) return ZERO

        return FactoredRational(
            numerator = numerator.plus(other.numerator),
            denominator = denominator.plus(other.denominator),
            reduce = true
        )
    }

    operator fun times(other: BigInteger): FactoredRational {
        if (other == BigInteger.ZERO) return ZERO

        return FactoredRational(
            numerator = numerator.plus(factor(other)),
            denominator = denominator,
            reduce = true
        )
    }

    operator fun div(other: FactoredRational): FactoredRational {
        return times(
            FactoredRational(
                numerator = other.denominator,
                denominator = other.numerator,
                reduce = false
            )
        )
    }

    fun exp(n: Int): FactoredRational {
        if (n == 0) return ONE

        return FactoredRational(
            numerator = numerator.repeat(n),
            denominator = denominator.repeat(n),
            reduce = false
        )
    }

    fun oneMinus(): FactoredRational {
        return FactoredRational(
            numerator = factor(denominator.toBigInteger() - numerator.toBigInteger()),
            denominator = denominator,
            reduce = false
        )
    }

    companion object {

        private val NO_FACTORS = MultiSet<Long>(mapOf())   // represents 1

        val ONE = FactoredRational(numerator = NO_FACTORS, denominator = NO_FACTORS, reduce = false)
        val ZERO = FactoredRational(
            numerator = MultiSet(mapOf(0L to 1)),
            denominator = NO_FACTORS,
            reduce = false
        )
    }
}
