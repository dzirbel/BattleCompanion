package com.github.dzirbel.battlecompanion.text

import java.text.DecimalFormat
import java.text.NumberFormat

private val PERCENT_FORMAT = DecimalFormat("#00.00%")
private val INTEGER_FORMAT = NumberFormat.getIntegerInstance()

fun Double.formatPercent(): String = PERCENT_FORMAT.format(this)

fun Int.format(): String = INTEGER_FORMAT.format(this)
