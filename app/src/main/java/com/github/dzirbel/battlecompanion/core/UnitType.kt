package com.github.dzirbel.battlecompanion.core

enum class UnitType(
    val domain: Domain,
    val attack: Int,
    val defense: Int,
    val cost: Int,

    /**
     * The maximum number of hits this [UnitType] can sustain, typically 1 (default).
     * Must be strictly positive.
     */
    val maxHp: Int = 1,

    /**
     * Whether this [UnitType] participates only in the first round of combat, after which it should
     *  be removed.
     * Units for which this is true are not allowed to take hits in combat.
     */
    val firstRoundOnly: Boolean = false,

    /**
     * Specifies a [Domain] which this [UnitType] can only hit, e.g. [SUBMARINE]s can only hit
     *  [Domain.SEA].
     * Null (default) indicates that this [UnitType] can hit any [Domain].
     */
    val targetDomain: Domain? = null
) {

    INFANTRY(domain = Domain.LAND, attack = 1, defense = 2, cost = 3),
    ARTILLERY(domain = Domain.LAND, attack = 2, defense = 2, cost = 4),
    TANK(domain = Domain.LAND, attack = 3, defense = 3, cost = 5),

    TRANSPORT(domain = Domain.SEA, attack = 0, defense = 1, cost = 8),
    SUBMARINE(
        domain = Domain.SEA,
        attack = 2,
        defense = 2,
        cost = 8,
        targetDomain = Domain.SEA
    ),
    DESTROYER(domain = Domain.SEA, attack = 3, defense = 3, cost = 12),
    AIRCRAFT_CARRIER(domain = Domain.SEA, attack = 1, defense = 3, cost = 16),
    BATTLESHIP(domain = Domain.SEA, attack = 4, defense = 4, cost = 24, maxHp = 2),

    FIGHTER(domain = Domain.AIR, attack = 3, defense = 4, cost = 10),
    BOMBER(domain = Domain.AIR, attack = 4, defense = 1, cost = 15),

    ANTIAIRCRAFT_GUN(
        domain = Domain.LAND,
        attack = 0,
        defense = 1,
        cost = 5,
        firstRoundOnly = true,
        targetDomain = Domain.AIR
    ),
    BOMBARDING_BATTLESHIP(
        domain = Domain.LAND,
        attack = 4,
        defense = 0,
        cost = 24,
        firstRoundOnly = true,
        targetDomain = Domain.LAND
    ),
    BOMBARDING_DESTROYER(
        domain = Domain.LAND,
        attack = 3,
        defense = 0,
        cost = 12,
        firstRoundOnly = true,
        targetDomain = Domain.LAND
    );

    val fullName = name.split("_").joinToString(
        separator = " ",
        transform = { it.toLowerCase().capitalize() }
    )

    val shortName by lazy {
        when (this) {
            ANTIAIRCRAFT_GUN -> "AA Gun"
            BOMBARDING_BATTLESHIP -> "Battleship"
            BOMBARDING_DESTROYER -> "Destroyer"
            else -> fullName
        }
    }

    /**
     * Determines the attack or defense value of this [UnitType] for the given attacking role, i.e.
     *  [attack] if [isAttacking] is true and [defense] otherwise.
     */
    fun combatPower(isAttacking: Boolean, weaponDevelopments: Set<WeaponDevelopment>): Int {
        return when {
            this == FIGHTER && !isAttacking &&
                    weaponDevelopments.contains(WeaponDevelopment.JET_FIGHTERS) -> 5
            this == SUBMARINE && isAttacking &&
                    weaponDevelopments.contains(WeaponDevelopment.SUPER_SUBMARINES) -> 3
            else -> if (isAttacking) attack else defense
        }
    }

    /**
     * Determines the number of dice this unit should throw each round against the given [Army].
     */
    fun numberOfRolls(
        enemies: Army,
        isAttacking: Boolean,
        weaponDevelopments: Set<WeaponDevelopment>
    ): Int {
        return when {
            this == ANTIAIRCRAFT_GUN -> enemies.count { it.domain == Domain.AIR }
            this == BOMBER && isAttacking &&
                    weaponDevelopments.contains(WeaponDevelopment.HEAVY_BOMBERS) -> 2
            else -> 1
        }
    }

    /**
     * Determines whether this [UnitType] should fire during the opening fire round against the
     *  given [Army].
     */
    fun hasOpeningFire(enemies: Army): Boolean {
        return when (this) {
            ANTIAIRCRAFT_GUN -> true
            BOMBARDING_BATTLESHIP -> true
            SUBMARINE -> !enemies.units.containsKey(DESTROYER)
            else -> false
        }
    }

    /**
     * Determines whether this [UnitType] can capture a territory or sea zone; i.e. is not an air
     *  unit and not [firstRoundOnly].
     */
    fun canInvade() = !firstRoundOnly && domain != Domain.AIR

    /**
     * Determines whether this [UnitType] can be used by an army with the given set of
     *  [WeaponDevelopment]s.
     */
    fun hasRequiredWeaponDevelopments(weaponDevelopments: Set<WeaponDevelopment>): Boolean {
        return when (this) {
            BOMBARDING_DESTROYER ->
                weaponDevelopments.contains(WeaponDevelopment.COMBINED_BOMBARDMENT)
            else -> true
        }
    }

    /**
     * Determines whether this [UnitType] should be listed as able to attack in the given [Domain].
     */
    fun canAttackIn(domain: Domain): Boolean {
        return when (this) {
            UnitType.ANTIAIRCRAFT_GUN -> false
            UnitType.FIGHTER -> true
            UnitType.BOMBER -> true
            else -> this.domain == domain
        }
    }

    /**
     * Determines whether this [UnitType] should be listed as able to defend in the given [Domain].
     */
    fun canDefendIn(domain: Domain): Boolean {
        return when (this) {
            UnitType.BOMBARDING_BATTLESHIP -> false
            UnitType.BOMBARDING_DESTROYER -> false
            UnitType.FIGHTER -> true
            UnitType.BOMBER -> domain == Domain.LAND
            else -> this.domain == domain
        }
    }
}
