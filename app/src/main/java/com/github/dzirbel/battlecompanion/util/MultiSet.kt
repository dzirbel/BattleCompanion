package com.github.dzirbel.battlecompanion.util

/**
 * Represents a set of [T]s, each of which may occur more than once.
 * Internally the [MultiSet] is stored as a [Map] from each element [T] to the number of times it
 *  occurs in the [MultiSet]; this count is guaranteed to be strictly positive.
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
        return object : Iterator<T> {

            val countIterator = counts.iterator()
            var currentKey: T?
            var currentKeyRemaining: Int

            init {
                if (countIterator.hasNext()) {
                    val firstEntry = countIterator.next()
                    currentKey = firstEntry.key
                    currentKeyRemaining = firstEntry.value
                } else {
                    currentKey = null
                    currentKeyRemaining = 0
                }
            }

            override fun hasNext() = currentKeyRemaining > 0

            override fun next(): T {
                return currentKey?.also {
                    currentKeyRemaining--
                    if (currentKeyRemaining == 0) {
                        if (countIterator.hasNext()) {
                            val nextEntry = countIterator.next()
                            currentKey = nextEntry.key
                            currentKeyRemaining = nextEntry.value
                        }
                    }
                } ?: throw NoSuchElementException()
            }
        }
    }

    override fun hashCode() = counts.hashCode()

    override fun equals(other: Any?) = other is MultiSet<*> && other.counts == counts

    override fun toString(): String {
        return counts.entries.joinToString(transform = { "${it.value} of ${it.key}" })
    }

    fun toString(elementToString: (T) -> String): String {
        return counts.entries.joinToString(
            transform = { "${it.value} of ${elementToString(it.key)}" }
        )
    }

    /**
     * Returns a [List] containing all the elements in this [MultiSet] (including repetitions), in
     *  an arbitrary order.
     */
    fun toList(): List<T> {
        return counts.flatMap { (element, count) -> List(count) { element } }
    }

    /**
     * Returns the number of occurrences of the given [element] in this [MultiSet], or 0 if it is
     *  not contained by this [MultiSet].
     */
    fun countOf(element: T) = counts[element] ?: 0

    /**
     * Determines whether this [MultiSet] contains only the given [element] (with any number of
     *  copies).
     */
    fun hasOnly(element: T) = counts.size == 1 && counts.containsKey(element)

    /**
     * Returns a copy of this [MultiSet] with an element added, optionally multiple times (i.e.
     *  multiple copies).
     *
     * @param n the number of times to add [element]
     * @throws [IllegalArgumentException] if [n] is not strictly positive
     */
    fun plus(element: T, n: Int = 1): MultiSet<T> {
        if (n <= 0) {
            throw IllegalArgumentException("Attempted to add non-positive count $n: $element")
        }

        return MultiSet(counts.plus(element to countOf(element) + n))
    }

    /**
     * Returns a [MultiSet] containing both the elements of this [MultiSet] and the given one,
     *  counting all repetitions.
     */
    operator fun plus(set: MultiSet<T>): MultiSet<T> {
        if (isEmpty()) return set
        if (set.isEmpty()) return this

        return MultiSet(
            (counts.keys + set.counts.keys)
                .map { element -> Pair(element, countOf(element) + set.countOf(element)) }
                .toMap()
        )
    }

    /**
     * Returns a copy of this [MultiSet] with an element removed, optionally multiple times (i.e.
     *  multiple copies).
     *
     * @param n the number of times to remove [element]
     * @param safe whether to allow removing [element] more times than it occurs in this [MultiSet]
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
            else -> throw IllegalArgumentException(
                "Attempted to remove $n elements but only have $count: $element"
            )
        }
    }

    /**
     * Returns a [MultiSet] with the elements of the given [MultiSet] safely removed (i.e. if there
     *  are more duplicates in [set] than this [MultiSet], or elements in [set] but not this
     *  [MultiSet], the extras are ignored).
     */
    operator fun minus(set: MultiSet<T>): MultiSet<T> {
        if (isEmpty() || set.isEmpty()) return this

        return MultiSet(
            counts
                .mapValues { (element, count) -> count - set.countOf(element) }
                .filterValues { it > 0 }
        )
    }

    /**
     * Returns a [MultiSet] with each elements of this set repeated [n] times, including duplicates.
     * Returns an empty [MultiSet] is [n] is zero.
     */
    fun repeat(n: Int): MultiSet<T> {
        return when {
            n < 0 -> throw IllegalArgumentException()
            n == 0 -> MultiSet(emptyMap())
            n == 1 -> this
            else -> MultiSet(counts.mapValues { (_, count) -> count * n })
        }
    }

    /**
     * Returns a [MultiSet] containing the elements of this [MultiSet] (including their duplicates)
     *  having been mapped by the given [mapper].
     * [mapper] is called once for each copy of an element in this [MultiSet] (rather than once for
     *  all the copies).
     */
    fun <R> map(mapper: (T) -> R): MultiSet<R> {
        val mapped = mutableMapOf<R, Int>()
        counts.forEach { element, count ->
            repeat(count) {
                mapped.compute(mapper(element)) { _, prevCount -> (prevCount ?: 0) + 1 }
            }
        }
        return MultiSet(mapped)
    }
}
