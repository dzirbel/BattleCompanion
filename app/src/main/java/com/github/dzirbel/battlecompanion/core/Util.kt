package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

fun Random.rollDice(dice: Int): List<Int> {
    return List(dice) { rollDie() }
}

fun Random.rollDie(): Int {
    return nextInt(6) + 1
}

/**
 * Computes the cross product of [dim1] and [dim2], ordered first by the key order of [dim1] and
 *  second by the key order of [dim2].
 */
fun <K, V> cross(dim1: Map<K, V>, dim2: Map<K, V>): Map<Pair<K, K>, Pair<V, V>> {
    return dim1.flatMap { (k1, v1) ->
        dim2.map { (k2, v2) -> Pair(k1, k2) to Pair(v1, v2) }
    }.toMap()
}

/**
 * Combines a [flatMap] and [reduce] into a single simpler and more performant operation.
 * The first step maps all the key-value pairs in this [Map] to [Map]s of their own via
 *  [flatMapper]; the second step gathers all of these [Map]s by key and reduces overlapping
 *  values via [reduce].
 */
fun <K, V, R, S> Map<K, V>.flatMapAndReduce(
    reducer: (S, S) -> S,
    flatMapper: (K, V) -> Map<R, S>
): Map<R, S> {
    val flatMapped = mutableMapOf<R, MutableList<S>>()
    entries.forEach { (key, value) ->
        flatMapper(key, value).forEach { (mappedKey, mappedValue) ->
            flatMapped.compute(mappedKey) { _, list ->
                list?.apply { add(mappedValue) } ?: mutableListOf(mappedValue)
            }
        }
    }
    return flatMapped.mapValues { it.value.reduce(reducer) }
}
