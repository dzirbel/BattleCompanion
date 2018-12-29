package com.github.dzirbel.battlecompanion.core

import android.util.Rational
import kotlin.random.Random

fun Random.rollDice(dice: Int): List<Int> {
    return List(dice) { rollDie() }
}

fun Random.rollDie(): Int {
    return nextInt(6) + 1
}

operator fun Rational.times(other: Rational): Rational {
    return Rational(numerator * other.numerator, denominator * other.denominator)
}

fun <K, V> cross(dim1: Map<K, V>, dim2: Map<K, V>): Map<Pair<K, K>, Pair<V, V>> {
    return dim1.flatMap { (key1, value1) ->
        dim2.map { (key2, value2) -> Pair(Pair(key1, key2), Pair(value1, value2)) }
    }.toMap()
}
