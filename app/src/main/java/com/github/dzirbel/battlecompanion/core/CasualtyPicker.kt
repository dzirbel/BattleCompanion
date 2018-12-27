package com.github.dzirbel.battlecompanion.core

/**
 * A generic way for an [Army] to pick which units to lose as casualties when taking hits.
 * Standard implementations are provided for losing units based on cost, combat effectiveness, etc.
 *  and the interface allows for asynchronous user-entered choices.
 */
interface CasualtyPicker {

    /**
     * Chooses which units of [army] should be lost as casualties to the given [hits], as a map from
     *  each [UnitType] to the number of units of that type to be casualties.
     */
    fun pick(army: Army, hits: HitProfile): Map<UnitType, Int>

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
        val attack: Comparator<UnitType> = Comparator.comparingInt { it.attack }
        val defense: Comparator<UnitType> = Comparator.comparingInt { it.defense }
        val tieBreaker: Comparator<UnitType> = Comparator.comparingInt { it.ordinal }
    }

    // TODO implement keepInvadingUnit
    abstract class ByComparator(
        private val comparator: Comparator<UnitType>,
        private val keepInvadingUnit: Boolean = false
    ) : CasualtyPicker {

        override fun pick(army: Army, hits: HitProfile): Map<UnitType, Int> {
            val sortedUnits = army.units.toSortedMap(comparator)

            val casualties = mutableMapOf<UnitType, Int>()

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
                            val hitsTaken = Math.min(remainingHits, unitCount)
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

    class ByCost(isAttacking: Boolean, keepInvadingUnit: Boolean = false) : ByComparator(
        comparator = Comparators.cost
            .thenComparing(if (isAttacking) Comparators.attack else Comparators.defense)
            .thenComparing(Comparators.tieBreaker),
        keepInvadingUnit = keepInvadingUnit
    )

    class ByCombatPower(isAttacking: Boolean, keepInvadingUnit: Boolean = false) : ByComparator(
        comparator = (if (isAttacking) Comparators.attack else Comparators.defense)
            .thenComparing(Comparators.cost)
            .thenComparing(Comparators.tieBreaker),
        keepInvadingUnit = keepInvadingUnit
    )
}