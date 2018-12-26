package com.github.dzirbel.battlecompanion.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

internal class MultiSetTest {

    @Test
    fun testEmpty() {
        val empty = MultiSet<String>()
        assertEquals(0, empty.size)
        assertTrue(empty.isEmpty())

        empty.assertDoesNotContain("")
        empty.assertDoesNotContain("abc")
        empty.assertDoesNotContain(" ")

        assertTrue(empty.containsAll(setOf()))
        assertFalse(empty.containsAll(setOf("a", "b", "c")))

        assertFalse(empty.iterator().hasNext())

        assertSameElements(emptyList(), empty.toList())
    }

    @Test
    fun testSingleton() {
        val item = "abc"
        val singleton = MultiSet(mapOf(item to 1))
        assertEquals(1, singleton.size)
        assertFalse(singleton.isEmpty())

        singleton.assertContains(item, 1)
        singleton.assertDoesNotContain("")
        singleton.assertDoesNotContain(" ")

        assertTrue(singleton.iterator().hasNext())
        var iterations = 0
        singleton.forEach {
            assertEquals(item, it)
            iterations++
        }
        assertEquals(1, iterations)

        assertSameElements(listOf(item), singleton.toList())
    }

    @Test
    fun testMultiples() {
        val item = "abc"
        val count = 3
        val set = MultiSet(mapOf(item to count))

        assertEquals(count, set.size)
        assertFalse(set.isEmpty())

        set.assertContains(item, count)
        set.assertDoesNotContain("")
        set.assertDoesNotContain(" ")

        assertTrue(set.iterator().hasNext())
        var iterations = 0
        set.forEach {
            assertEquals(item, it)
            iterations++
        }
        assertEquals(count, iterations)

        assertSameElements(List(count) { item }, set.toList())
    }

    @Test
    fun testDistinct() {
        val items = setOf("a", "b", "c", "d")
        val set = MultiSet(items.map { it to 1 }.toMap())

        assertEquals(items.size, set.size)
        assertFalse(set.isEmpty())

        items.forEach { set.assertContains(it, 1) }
        set.assertDoesNotContain("")
        set.assertDoesNotContain("e")

        assertTrue(set.iterator().hasNext())
        val found = mutableSetOf<String>()
        var iterations = 0
        set.forEach {
            found.add(it)
            iterations++
        }
        assertEquals(items.size, iterations)
        assertEquals(items, found)

        assertSameElements(items.toList(), set.toList())
    }

    @Test
    fun testMixed() {
        val items = mapOf("a" to 3, "b" to 2, "c" to 1, "d" to 0)
        val total = 6
        val set = MultiSet(items)

        assertEquals(total, set.size)
        assertFalse(set.isEmpty())

        items.forEach { item, count ->
            if (count > 0) {
                set.assertContains(item, count)
            } else {
                set.assertDoesNotContain(item)
            }
        }
        set.assertDoesNotContain("")
        set.assertDoesNotContain("e")

        assertTrue(set.iterator().hasNext())
        val found = mutableMapOf<String, Int>()
        var iterations = 0
        set.forEach {
            found[it] = (found[it] ?: 0) + 1
            iterations++
        }
        assertEquals(total, iterations)
        assertEquals(items.filterValues { it > 0 }, found)

        assertSameElements(listOf("a", "a", "a", "b", "b", "c"), set.toList())
    }

    @Test
    fun testEqualsEmpty() {
        val set1 = MultiSet<String>()
        val set2 = MultiSet<String>()

        assertEquals(set1, set2)
        assertEquals(set1.hashCode(), set2.hashCode())
        assertFalse(set1 === set2)

        assertNotEquals(set1, MultiSet(mapOf("a" to 1)))
    }

    @Test
    fun testEquals() {
        val set1 = MultiSet(mapOf("a" to 3, "b" to 2, "c" to 1))
        val set2 = MultiSet(mapOf("c" to 1, "b" to 2, "a" to 3, "d" to 0))

        assertEquals(set1, set2)
        assertEquals(set1.hashCode(), set2.hashCode())
        assertFalse(set1 === set2)

        assertNotEquals(set1, MultiSet(mapOf("a" to 1, "b" to 2, "c" to 3)))
    }

    @Test
    fun testPlus() {
        var set = MultiSet<String>()

        set = set.plus("a")
        assertEquals(MultiSet(mapOf("a" to 1)), set)

        set = set.plus("a", 2)
        assertEquals(MultiSet(mapOf("a" to 3)), set)

        set = set.plus("b", 1)
        assertEquals(MultiSet(mapOf("a" to 3, "b" to 1)), set)

        set = set.plus("b")
        assertEquals(MultiSet(mapOf("a" to 3, "b" to 2)), set)

        try {
            set = set.plus("c", -1)
            fail()
        } catch (ex: IllegalArgumentException) { }
        assertEquals(MultiSet(mapOf("a" to 3, "b" to 2)), set)

        try {
            set = set.plus("c", 0)
            fail()
        } catch (ex: IllegalArgumentException) { }
        assertEquals(MultiSet(mapOf("a" to 3, "b" to 2)), set)
    }

    @Test
    fun testMinus() {
        var set = MultiSet(mapOf("a" to 5, "b" to 5, "c" to 5))

        set = set.minus("a")
        assertEquals(MultiSet(mapOf("a" to 4, "b" to 5, "c" to 5)), set)

        set = set.minus("b", 2)
        assertEquals(MultiSet(mapOf("a" to 4, "b" to 3, "c" to 5)), set)

        set = set.minus("c", 8, safe = true)
        assertEquals(MultiSet(mapOf("a" to 4, "b" to 3)), set)

        try {
            set = set.minus("b", 8)
            fail()
        } catch (ex: IllegalArgumentException) { }
        assertEquals(MultiSet(mapOf("a" to 4, "b" to 3)), set)

        try {
            set = set.minus("a", -1)
            fail()
        } catch (ex: IllegalArgumentException) { }
        assertEquals(MultiSet(mapOf("a" to 4, "b" to 3)), set)

        try {
            set = set.minus("a", 0)
            fail()
        } catch (ex: IllegalArgumentException) { }
        assertEquals(MultiSet(mapOf("a" to 4, "b" to 3)), set)
    }

    @Test
    fun testMapToDistinct() {
        val set = MultiSet(mapOf("a" to 2, "b" to 3, "c" to 1, "m" to 6, "n" to 7))
        val mapped = set.map {
            when (it) {
                "a" -> "z"
                "b" -> "y"
                "c" -> "x"
                "d" -> "x"
                "m" -> "n"
                "n" -> "m"
                else -> throw AssertionError()
            }
        }
        val result = MultiSet(mapOf("z" to 2, "y" to 3, "x" to 1, "n" to 6, "m" to 7))

        assertEquals(result, mapped)
    }

    @Test
    fun testMapToSame() {
        val set = MultiSet(mapOf("a" to 1, "b" to 2, "c" to 3))
        val mapped = set.map { "a" }
        val result = MultiSet(mapOf("a" to 6))

        assertEquals(result, mapped)
    }

    private fun <T> MultiSet<T>.assertContains(element: T, count: Int) {
        assertTrue(contains(element))
        assertTrue(containsAll(setOf(element)))
        assertTrue(any { it == element })
        assertEquals(count, countOf(element))

        var found = false
        forEach { if (it == element) found = true }
        assertTrue(found)
    }

    @Test
    fun testMapCopies() {
        val set = MultiSet(mapOf("a" to 10))
        var counter = 0
        val mapped = set.map {
            counter++
            if (counter % 2 == 0) "b" else "c"
        }
        val result = MultiSet(mapOf("b" to 5, "c" to 5))

        assertEquals(result, mapped)
    }

    private fun <T> assertSameElements(a: List<T>, b: List<T>) {
        assertTrue(a.toMutableList().apply { b.forEach { remove(it) } }.isEmpty())
        assertTrue(b.toMutableList().apply { a.forEach { remove(it) } }.isEmpty())
    }

    private fun <T> MultiSet<T>.assertDoesNotContain(element: T) {
        assertFalse(contains(element))
        assertFalse(containsAll(setOf(element)))
        assertFalse(any { it == element })
        assertEquals(0, countOf(element))

        forEach { assertNotEquals(element, it) }
    }
}
