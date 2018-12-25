package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import java.text.DecimalFormat

private val PERCENT_FORMAT = DecimalFormat("000.00%")

fun Double.formatPercent(): String = PERCENT_FORMAT.format(this)

fun Int.format(chars: Int) = String.format("%0${chars}d", this)

fun Board.print() {
    if (attackers.isEmpty() && defenders.isEmpty()) {
        println("Empty board")
    }

    if (!attackers.isEmpty()) {
        println("Attackers:")
        attackers.print()
    }

    if (!defenders.isEmpty()) {
        println("Defenders:")
        defenders.print()
    }
}

fun Army.print() {
    units.filter { it.value > 0 }.forEach { (unitType, count) ->
        println(" ${unitType.prettyName} : $count")
    }
}
