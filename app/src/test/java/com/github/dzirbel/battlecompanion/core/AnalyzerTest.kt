package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational
import com.github.dzirbel.battlecompanion.util.multiSetOf
import org.junit.Assert.assertEquals
import org.junit.Test

class AnalyzerTest {

    @Test
    fun testImmediateOutcomes() {
        assertEquals(
            mapOf(Outcome.Tie to Rational.ONE),
            Analyzer.analyze(Board(attackers = Armies.empty, defenders = Armies.empty))
        )

        Armies.attackers.forEach { attackers ->
            assertEquals(
                mapOf(Outcome.AttackerWon(remaining = attackers) to Rational.ONE),
                Analyzer.analyze(Board(attackers = attackers, defenders = Armies.empty))
            )
        }

        Armies.defenders.forEach { defenders ->
            assertEquals(
                mapOf(Outcome.DefenderWon(remaining = defenders) to Rational.ONE),
                Analyzer.analyze(Board(attackers = Armies.empty, defenders = defenders))
            )
        }
    }

    @Test
    fun testTankOneOnOne() {
        val attackers = Armies.fromUnits(UnitType.TANK to 1, isAttacking = true)
        val defenders = Armies.fromUnits(UnitType.TANK to 1, isAttacking = false)

        assertEquals(
            mapOf(
                Outcome.AttackerWon(remaining = attackers) to Rational(1, 3),
                Outcome.DefenderWon(remaining = defenders) to Rational(1, 3),
                Outcome.Tie to Rational(1, 3)
            ),
            Analyzer.analyze(Board(attackers = attackers, defenders = defenders))
        )
    }

    @Test
    fun testInfantryOneOnOne() {
        val attackers = Armies.fromUnits(UnitType.INFANTRY to 1, isAttacking = true)
        val defenders = Armies.fromUnits(UnitType.INFANTRY to 1, isAttacking = false)

        assertEquals(
            mapOf(
                Outcome.AttackerWon(remaining = attackers) to Rational(1, 4),
                Outcome.DefenderWon(remaining = defenders) to Rational(5, 8),
                Outcome.Tie to Rational(1, 8)
            ),
            Analyzer.analyze(Board(attackers = attackers, defenders = defenders))
        )
    }

    @Test
    fun testSubmarineOneOnOne() {
        val attackers = Armies.fromUnits(UnitType.SUBMARINE to 1, isAttacking = true)
        val defenders = Armies.fromUnits(UnitType.SUBMARINE to 1, isAttacking = false)

        assertEquals(
            mapOf(
                Outcome.AttackerWon(remaining = attackers) to Rational(2, 5),
                Outcome.DefenderWon(remaining = defenders) to Rational(2, 5),
                Outcome.Tie to Rational(1, 5)
            ),
            Analyzer.analyze(Board(attackers = attackers, defenders = defenders))
        )
    }

    @Test
    fun testInfantryTankVersusInfantryArtillery() {
        val attackers = Armies.fromUnits(
            UnitType.INFANTRY to 1,
            UnitType.TANK to 1,
            isAttacking = true
        )
        val defenders = Armies.fromUnits(
            UnitType.INFANTRY to 1,
            UnitType.ARTILLERY to 1,
            isAttacking = false
        )

        assertEquals(
            mapOf(
                Outcome.AttackerWon(
                    remaining = Armies.fromUnits(UnitType.TANK to 1, isAttacking = true)
                ) to Rational(1257, 4004),
                Outcome.DefenderWon(
                    remaining = Armies.fromUnits(UnitType.ARTILLERY to 1, isAttacking = false)
                ) to Rational(1977, 8008),
                Outcome.AttackerWon(remaining = attackers) to Rational(5, 26),
                Outcome.DefenderWon(remaining = defenders) to Rational(85, 616),
                Outcome.Tie to Rational(109, 1001)
            ),
            Analyzer.analyze(Board(attackers = attackers, defenders = defenders))
        )
    }

    @Test
    fun testBattleshipVersusDestroyers() {
        val attackers = Armies.fromUnits(UnitType.BATTLESHIP to 1, isAttacking = true)
        val defenders = Armies.fromUnits(UnitType.DESTROYER to 2, isAttacking = false)

        assertEquals(
            mapOf(
                Outcome.DefenderWon(
                    remaining = Armies.fromUnits(UnitType.DESTROYER to 1, isAttacking = false)
                ) to Rational(1112, 3025),
                Outcome.AttackerWon(
                    remaining = attackers.copy(
                        units = mapOf(UnitType.BATTLESHIP to multiSetOf(1 to 1))
                    )
                ) to Rational(744, 3025),
                Outcome.Tie to Rational(524, 3025),
                Outcome.DefenderWon(remaining = defenders) to Rational(17, 121),
                Outcome.AttackerWon(remaining = attackers) to Rational(4, 55)
            ),
            Analyzer.analyze(Board(attackers = attackers, defenders = defenders))
        )
    }

    @Test
    fun testBattleshipVersusSubmarines() {
        val attackers = Armies.fromUnits(UnitType.BATTLESHIP to 1, isAttacking = true)
        val defenders = Armies.fromUnits(UnitType.SUBMARINE to 3, isAttacking = false)

        assertEquals(
            mapOf(
                Outcome.DefenderWon(remaining = defenders) to Rational(2217, 5329),
                Outcome.DefenderWon(
                    remaining = Armies.fromUnits(UnitType.SUBMARINE to 2, isAttacking = false)
                ) to Rational(821352, 2819041),
                Outcome.AttackerWon(
                    remaining = attackers.copy(
                        units = mapOf(UnitType.BATTLESHIP to multiSetOf(1 to 1))
                    )
                ) to Rational(20947968, 138133009),
                Outcome.DefenderWon(
                    remaining = Armies.fromUnits(UnitType.SUBMARINE to 1, isAttacking = false)
                ) to Rational(13454400, 138133009),
                Outcome.AttackerWon(remaining = attackers) to Rational(512, 11753)
            ),
            Analyzer.analyze(Board(attackers = attackers, defenders = defenders))
        )
    }
}
