package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.UnitType
import java.text.DecimalFormat

private val PERCENT_FORMAT = DecimalFormat("000.00%")

fun Double.formatPercent(): String = PERCENT_FORMAT.format(this)

fun Int.format(chars: Int) = String.format("%0${chars}d", this)

fun Board.print() {
    println("Attackers:")
    attackers.print()
    println("Defenders:")
    defenders.print()
}

fun Army.print() {
    units.entries.filter { it.value > 0 }.forEach { (unitType, count) ->
        println(" ${unitType.name.toLowerCase().capitalize()} : $count")
    }
}
