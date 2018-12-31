package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {

    @Test
    fun testCross() {
        assertEquals(
            emptyMap<Pair<String, String>, Pair<Int, Int>>(),
            cross(emptyMap<String, Int>(), emptyMap())
        )

        assertEquals(
            emptyMap<Pair<String, String>, Pair<Int, Int>>(),
            cross(mapOf("a" to 1, "b" to 2), emptyMap())
        )

        assertEquals(
            emptyMap<Pair<String, String>, Pair<Int, Int>>(),
            cross(emptyMap(), mapOf("a" to 1, "b" to 2))
        )

        assertEquals(
            mapOf(Pair("a", "b") to Pair(1, 2)),
            cross(mapOf("a" to 1), mapOf("b" to 2))
        )

        assertEquals(
            mapOf(
                Pair("a", "z") to Pair(1, 1),
                Pair("a", "y") to Pair(1, 2),
                Pair("a", "x") to Pair(1, 3),

                Pair("b", "z") to Pair(2, 1),
                Pair("b", "y") to Pair(2, 2),
                Pair("b", "x") to Pair(2, 3),

                Pair("d", "z") to Pair(3, 1),
                Pair("d", "y") to Pair(3, 2),
                Pair("d", "x") to Pair(3, 3),

                Pair("c", "z") to Pair(3, 1),
                Pair("c", "y") to Pair(3, 2),
                Pair("c", "x") to Pair(3, 3),

                Pair("z", "z") to Pair(0, 1),
                Pair("z", "y") to Pair(0, 2),
                Pair("z", "x") to Pair(0, 3)
            ),
            cross(
                mapOf("a" to 1, "b" to 2, "d" to 3, "c" to 3, "z" to 0),
                mapOf("z" to 1, "y" to 2, "x" to 3)
            )
        )
    }

    @Test
    fun testFlatMapAndReduce() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)

        assertEquals(
            emptyMap<String, Int>(),
            map.flatMapAndReduce(Int::plus) { _, _ -> emptyMap<String, Int>() }
        )

        assertEquals(
            map,
            map.flatMapAndReduce(Int::plus) { key, value -> mapOf(key to value) }
        )

        assertEquals(
            mapOf("a" to 6),
            map.flatMapAndReduce(Int::plus) { _, value -> mapOf("a" to value) }
        )

        assertEquals(
            mapOf("a" to 6, "b" to 12),
            map.flatMapAndReduce(Int::plus) { _, value -> mapOf("a" to value, "b" to 2 * value) }
        )

        assertEquals(
            mapOf("a" to 2, "b" to 3, "bb" to 3, "c" to 4, "cc" to 4, "ccc" to 4),
            map.flatMapAndReduce(Int::plus) { key, value ->
                List(value) { index ->
                    key.repeat(index + 1) to value + 1
                }.toMap()
            }
        )
    }
}
