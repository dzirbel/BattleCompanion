package com.github.dzirbel.battlecompanion.core

import kotlin.random.Random

fun Random.rollDice(dice: Int): List<Int> {
    return List(dice) { rollDie() }
}

fun Random.rollDie(): Int {
    return nextInt(6) + 1
}