package com.github.dzirbel.battlecompanion.util

/**
 * Represents a set of [T]s, each of which may occur more than once.
 * Internally the [MultiSet] is stored as a [Map] from each element [T] to the number of times it occurs in the
 *  [MultiSet]; this count is guaranteed to be strictly positive.
 * [MultiSet] is immutable.
 */
class MultiSet<T>(counts: Map<T, Int> = mapOf()) : Collection<T> {

    private val counts: Map<T, Int> = counts.filterValues { it > 0 }

    /**
     * The total number of elements stored in this [MultiSet], including multiples.
     */
    override val size = counts.values.sum()

    override fun contains(element: T) = counts.containsKey(element)

    override fun containsAll(elements: Collection<T>) = elements.all(counts::containsKey)

    override fun isEmpty() = counts.isEmpty()

    override fun iterator(): Iterator<T> {
        return counts.keys.toList()
            .flatMap { element -> List(countOf(element)) { element } }
            .iterator()
    }

    override fun hashCode() = counts.hashCode()

    override fun equals(other: Any?) = other is MultiSet<*> && other.counts == counts

    override fun toString(): String {
        return counts.entries.joinToString(transform = { "${it.value} of ${it.key}" })
    }

    fun toString(elementToString: (T) -> String): String {
        return counts.entries.joinToString(transform = { "${it.value} of ${elementToString(it.key)}" })
    }

    /**
     * Returns the number of occurrences of the given [element] in this [MultiSet], or 0 if it is not contained by this
     *  [MultiSet].
     */
    fun countOf(element: T) = counts[element] ?: 0

    /**
     * Returns a copy of this [MultiSet] with an element added, optionally multiple times (i.e. multiple copies).
     *
     * @param n the number of times to add [element]; default 1
     * @throws [IllegalArgumentException] if [n] is not strictly positive
     */
    fun plus(element: T, n: Int = 1): MultiSet<T> {
        if (n <= 0) {
            throw IllegalArgumentException("Attempted to add non-positive count $n: $element")
        }

        return MultiSet(counts.plus(element to countOf(element) + n))
    }

    /**
     * Returns a copy of this [MultiSet] with an element removed, optionally multiple times (i.e. multiple copies).
     *
     * @param n the number of times to remove [element]; default 1
     * @param safe whether to allow removing [element] more times than it occurs in this [MultiSet]; default `false`
     * @throws [IllegalArgumentException] if [n] is not strictly positive
     */
    fun minus(element: T, n: Int = 1, safe: Boolean = false): MultiSet<T> {
        if (n <= 0) {
            throw IllegalArgumentException("Attempted to subtract non-positive count $n: $element")
        }

        val count = countOf(element)
        val remaining = (count - n).let { if (safe) Math.max(0, it) else it }

        return when {
            remaining > 0 -> MultiSet(counts.plus(element to remaining))
            remaining == 0 -> MultiSet(counts.minus(element))
            else -> throw IllegalArgumentException("Attempted to remove $n elements but only have $count: $element")
        }
    }

    /**
     * Returns a [MultiSet] containing the elements of this [MultiSet] (including their duplicates) having been mapped
     *  by the given [mapper].
     * [mapper] is called once for each copy of an element in this [MultiSet] (rather than once for all the copies).
     */
    fun map(mapper: (T) -> T): MultiSet<T> {
        return counts.entries
            .map { (element, count) ->
                List(count) { mapper(element) }.groupBy { it }.mapValues { it.value.size }
            }
            .reduce { a, b -> a.mergeSum(b) }
            .let { MultiSet(it) }
    }

    private fun Map<T, Int>.mergeSum(other: Map<T, Int>): Map<T, Int> {
        val result = LinkedHashMap<T, Int>(this.size + other.size)
        result.putAll(this)
        for ((key, value) in other) {
            result[key] = (result[key] ?: 0) + value
        }
        return result
    }
}
