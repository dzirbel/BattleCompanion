package com.github.dzirbel.battlecompanion.core

import java.util.EnumMap

/**
 * A generic way for an [Army] to pick which units to lose as casualties when taking hits.
 * Standard implementations are provided for losing units based on cost, combat effectiveness, etc.
 *  and the interface allows for asynchronous user-entered choices.
 */
interface CasualtyPicker {

    /**
     * Chooses which units of [army] should be lost as casualties to the given [hits], as a map from
     *  each [UnitType] to the number of units of that type to be casualties.
     * This function is only called when the [army] is not completely wiped out by the [hits], i.e.
     *  there is a real choice.
     */
    fun pick(army: Army, hits: HitProfile, isAttacking: Boolean): Map<UnitType, Int>

    sealed class InvalidCasualtiesError : Throwable() {

        data class TooFewInDomain(
            val domain: Domain?,
            val casualties: Int,
            val hits: Int
        ) : InvalidCasualtiesError()

        data class TooManyOfType(
            val unitType: UnitType,
            val casualties: Int,
            val units: Int
        ) : InvalidCasualtiesError()
    }

    private object Comparators {
        val cost: Comparator<UnitType> = Comparator.comparingInt { it.cost }
        val tieBreaker: Comparator<UnitType> = Comparator.comparingInt { it.ordinal }

        fun combatPower(
            isAttacking: Boolean,
            weaponDevelopments: Set<WeaponDevelopment>
        ): Comparator<UnitType> {
            return Comparator.comparingInt {
                it.combatPower(isAttacking = isAttacking, weaponDevelopments = weaponDevelopments)
            }
        }
    }

    /**
     * A [CasualtyPicker] which determines which [UnitType]s to lose first based on the [Comparator]
     *  returned by [comparatorGetter].
     * A single invading unit (i.e. non-air unit, see [UnitType.canInvade]) can optionally be kept,
     *  regardless of the [Comparator], by toggling [keepInvadingUnit].
     *
     * TODO option to always save transports?
     */
    abstract class ByComparator(
        private val comparatorGetter: (Boolean, Set<WeaponDevelopment>) -> Comparator<UnitType>,
        private val keepInvadingUnit: Boolean = false
    ) : CasualtyPicker {

        override fun pick(army: Army, hits: HitProfile, isAttacking: Boolean): Map<UnitType, Int> {
            val comparator = comparatorGetter(isAttacking, army.weaponDevelopments)
            val sortedUnits = army.units.toSortedMap(comparator)

            val bestInvadingUnit =
                if (keepInvadingUnit) {
                    sortedUnits.keys.last { it.canInvade() }
                } else {
                    null
                }

            val casualties = EnumMap<UnitType, Int>(UnitType::class.java)

            fun pickForDomain(hits: Int, domain: Domain?) {
                var remainingHits = hits
                for ((unitType, hps) in sortedUnits) {
                    if (unitType.firstRoundOnly) {
                        continue
                    }

                    if (domain == null || unitType.domain == domain) {
                        val casualtiesSoFar = casualties[unitType] ?: 0
                        val unitCount = hps.size - casualtiesSoFar
                        if (unitCount > 0) {
                            val minLeft = if (unitType == bestInvadingUnit) 1 else 0
                            val hitsTaken = Math.min(remainingHits, unitCount - minLeft)
                            remainingHits -= hitsTaken
                            casualties[unitType] = casualtiesSoFar + hitsTaken
                            if (remainingHits == 0) {
                                return
                            }
                        }
                    }
                }
            }

            hits.domainHits.forEach { domain, domainHitCount ->
                pickForDomain(hits = domainHitCount, domain = domain)
            }

            pickForDomain(hits = hits.generalHits, domain = null)

            return casualties
        }
    }

    /**
     * A [CasualtyPicker] which chooses the cheapest units to lose first, breaking ties based on
     *  combat power.
     * A single invading unit (i.e. non-air unit, see [UnitType.canInvade]) can optionally be kept,
     *  regardless of cost, by toggling [keepInvadingUnit].
     */
    class ByCost(keepInvadingUnit: Boolean = false) : ByComparator(
        comparatorGetter = { isAttacking, weaponDevelopments ->
            Comparators.cost
                .thenComparing(
                    Comparators.combatPower(
                        isAttacking = isAttacking,
                        weaponDevelopments = weaponDevelopments
                    )
                )
                .thenComparing(Comparators.tieBreaker)
        },
        keepInvadingUnit = keepInvadingUnit
    )

    /**
     * A [CasualtyPicker] which chooses units with the lowest combat power to lose first, breaking
     *  ties based on unit cost.
     * A single invading unit (i.e. non-air unit, see [UnitType.canInvade]) can optionally be kept,
     *  regardless of cost, by toggling [keepInvadingUnit].
     */
    class ByCombatPower(keepInvadingUnit: Boolean = false) : ByComparator(
        comparatorGetter = { isAttacking, weaponDevelopments ->
            Comparators.combatPower(
                isAttacking = isAttacking,
                weaponDevelopments = weaponDevelopments
            )
                .thenComparing(Comparators.cost)
                .thenComparing(Comparators.tieBreaker)
        },
        keepInvadingUnit = keepInvadingUnit
    )
}
