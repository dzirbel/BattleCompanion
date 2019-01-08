package com.github.dzirbel.battlecompanion.core

enum class WeaponDevelopment {

    JET_FIGHTERS,
    ROCKETS,
    SUPER_SUBMARINES,
    LONG_RANGE_AIRCRAFT,
    COMBINED_BOMBARDMENT,
    HEAVY_BOMBERS;

    val prettyName = name.split("_").joinToString(
        separator = " ",
        transform = { it.toLowerCase().capitalize() }
    )
}
