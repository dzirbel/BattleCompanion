package com.github.dzirbel.battlecompanion.util

fun <T> Iterable<T>.toMultiSet(): MultiSet<T> {
    val counts = mutableMapOf<T, Int>()
    forEach { element -> counts.compute(element) { _, value -> (value ?: 0) + 1 } }
    return MultiSet(counts)
}

fun <T> Array<T>.toMultiSet(): MultiSet<T> {
    val counts = mutableMapOf<T, Int>()
    forEach { element -> counts.compute(element) { _, value -> (value ?: 0) + 1 } }
    return MultiSet(counts)
}

fun <T> emptyMultiSet(): MultiSet<T> = MultiSet()

fun <T> multiSetOf(): MultiSet<T> = emptyMultiSet()

fun <T> multiSetOf(vararg counts: Pair<T, Int>): MultiSet<T> = MultiSet(mapOf(*counts))

fun <T> multiSetOf(vararg elements: T): MultiSet<out T> = elements.toMultiSet()
