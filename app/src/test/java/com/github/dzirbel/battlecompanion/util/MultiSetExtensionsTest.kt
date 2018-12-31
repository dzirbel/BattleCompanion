package com.github.dzirbel.battlecompanion.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MultiSetExtensionsTest {

    @Test
    fun testIterableToMultiSet() {
        assertEquals(MultiSet<String>(), emptyList<String>().toMultiSet())
        assertEquals(MultiSet(mapOf("a" to 1)), listOf("a").toMultiSet())
        assertEquals(
            MultiSet(mapOf("a" to 1, "b" to 1, "c" to 1)),
            listOf("c", "b", "a").toMultiSet()
        )
        assertEquals(
            MultiSet(mapOf("a" to 1, "b" to 2, "c" to 3)),
            listOf("c", "b", "a", "c", "b", "c").toMultiSet()
        )
    }

    @Test
    fun testArrayToMultiSet() {
        assertEquals(MultiSet<String>(), emptyArray<String>().toMultiSet())
        assertEquals(MultiSet(mapOf("a" to 1)), arrayOf("a").toMultiSet())
        assertEquals(
            MultiSet(mapOf("a" to 1, "b" to 1, "c" to 1)),
            arrayOf("c", "b", "a").toMultiSet()
        )
        assertEquals(
            MultiSet(mapOf("a" to 1, "b" to 2, "c" to 3)),
            arrayOf("c", "b", "a", "c", "b", "c").toMultiSet()
        )
    }

    @Test
    fun testEmptyMultiSet() {
        assertEquals(MultiSet<String>(), emptyMultiSet<String>())
        assertTrue(emptyMultiSet<String>().isEmpty())
    }

    @Test
    fun testMultiSetOfNothing() {
        assertEquals(MultiSet<String>(), multiSetOf<String>())
        assertTrue(multiSetOf<String>().isEmpty())
    }

    @Test
    fun testMultiSetOfCounts() {
        assertEquals(MultiSet(mapOf("a" to 1)), multiSetOf("a" to 1))
        assertEquals(MultiSet(mapOf("a" to 1, "b" to 2)), multiSetOf("a" to 1, "b" to 2))
        assertEquals(
            MultiSet(mapOf("a" to 1, "b" to 2, "c" to 0)),
            multiSetOf("a" to 1, "b" to 2, "c" to 0)
        )
    }

    @Test
    fun testMultiSetOfElements() {
        assertEquals(MultiSet(mapOf("a" to 1)), multiSetOf("a"))
        assertEquals(MultiSet(mapOf("a" to 1, "b" to 1)), multiSetOf("a", "b"))
        assertEquals(MultiSet(mapOf("a" to 2, "b" to 1)), multiSetOf("a", "a", "b"))
        assertEquals(MultiSet(mapOf("a" to 2, "b" to 2)), multiSetOf("a", "b", "a", "b"))
    }
}
