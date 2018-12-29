package com.github.dzirbel.battlecompanion.text

import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import java.text.DecimalFormat
import java.text.NumberFormat

private val PERCENT_FORMAT = DecimalFormat("#00.00%")
private val INTEGER_FORMAT = NumberFormat.getIntegerInstance()

fun Double.formatPercent(): String = PERCENT_FORMAT.format(this)

fun Int.format(): String = INTEGER_FORMAT.format(this)

fun Board.print() {
    if (attackers.units.isEmpty() && defenders.units.isEmpty()) {
        println("Empty board")
    }

    if (attackers.units.isNotEmpty()) {
        println("Attackers:")
        attackers.print()
    }

    if (defenders.units.isNotEmpty()) {
        println("Defenders:")
        defenders.print()
    }
}

fun Army.print() {
    units.forEach { (unitType, hps) ->
        println(" ${unitType.prettyName} : ${hps.count()} | ${hps.toString { "${it}hp" }}")
    }
}
