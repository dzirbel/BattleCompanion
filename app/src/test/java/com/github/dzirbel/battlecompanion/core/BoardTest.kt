package com.github.dzirbel.battlecompanion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardTest {

    private val emptyBoard = Board(attackers = Armies.empty, defenders = Armies.empty)

    @Test
    fun testOutcome() {
        assertEquals(Outcome.Tie, emptyBoard.outcome)

        Armies.all.forEach { army ->
            assertEquals(
                Outcome.AttackerWon(remaining = army),
                Board(attackers = army, defenders = Armies.empty).outcome
            )

            assertEquals(
                Outcome.DefenderWon(remaining = army),
                Board(attackers = Armies.empty, defenders = army).outcome
            )

            assertNull(Board(attackers = army, defenders = army).outcome)
        }
    }

    @Test
    fun testRoll() {
        Randoms.all.forEach { rand ->
            assertEquals(emptyBoard, emptyBoard.roll(rand))

            Armies.all.forEach { attackers ->
                Armies.all.forEach { defenders ->
                    var board = Board(attackers = attackers, defenders = defenders)
                    var prevAttackers: Army
                    var prevDefenders: Army

                    while (board.outcome == null) {
                        prevAttackers = board.attackers
                        prevDefenders = board.defenders

                        board = board.roll(rand)

                        assertTrue(board.attackers.units.none { it.key.firstRoundOnly })
                        assertTrue(board.defenders.units.none { it.key.firstRoundOnly })

                        board.attackers.assertSubsetOf(prevAttackers)
                        board.defenders.assertSubsetOf(prevDefenders)
                    }

                    assertTrue(board.attackers.units.isEmpty() || board.defenders.units.isEmpty())
                }
            }
        }
    }

    /**
     * Asserts that this [Army] is a (non-strict) subset of the given one, i.e. for each [UnitType]
     *  this [Army] has at most as many units of that type as [larger].
     */
    private fun Army.assertSubsetOf(larger: Army) {
        UnitType.values().forEach { unitType ->
            assertTrue("", count { it == unitType } <= larger.count { it == unitType })
        }
    }
}
