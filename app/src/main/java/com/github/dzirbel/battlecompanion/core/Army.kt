package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.MultiSet
import com.github.dzirbel.battlecompanion.util.Rational
import com.github.dzirbel.battlecompanion.util.multiSetOf
import java.util.EnumMap
import kotlin.random.Random

/**
 * Represents one side of a combat board: the [units] (a map from each [UnitType] to a [MultiSet] of
 *  the hp's of each unit of that type; zero hp is not allowed) and [casualtyPicker] which chooses
 *  which units to lose as casualties first.
 * [Army] has no knowledge of its state in the combat sequence (e.g. whether it is performing
 *  opening fire).
 * Note that [Army]s are immutable.
 *
 * TODO try to instantiate all instances of [units] as EnumMaps for performance?
 */
data class Army(
    val units: Map<UnitType, MultiSet<Int>>,
    val isAttacking: Boolean,
    val casualtyPicker: CasualtyPicker,
    val weaponDevelopments: Set<WeaponDevelopment>
) {

    companion object {

        /**
         * Returns an [Army] with the given [casualtyPicker] and  [units] as a map from the
         *  [UnitType] to the number of units, all initialized to their respective [UnitType.maxHp].
         */
        fun fromMap(
            units: Map<UnitType, Int>,
            isAttacking: Boolean,
            casualtyPicker: CasualtyPicker,
            weaponDevelopments: Set<WeaponDevelopment>
        ): Army {
            return Army(
                units = units
                    .filterValues { it > 0 }
                    .mapValues { (unitType, count) -> multiSetOf(unitType.maxHp to count) },
                isAttacking = isAttacking,
                casualtyPicker = casualtyPicker,
                weaponDevelopments = weaponDevelopments
            )
        }
    }

    /**
     * Returns the total number of units of the given [UnitType], or 0 if it has no units of the
     *  given [UnitType].
     */
    fun count(unitType: UnitType): Int {
        return units[unitType]?.size ?: 0
    }

    /**
     * Returns the total number of [UnitType]s (of any hp) in this [Army] satisfying the given
     *  [predicate].
     */
    fun count(predicate: (UnitType) -> Boolean): Int {
        return units.entries.sumBy { if (predicate(it.key)) it.value.size else 0 }
    }

    /**
     * Returns the sum of the hps of the units whose [UnitType] satisfies the given [predicate].
     */
    fun totalHp(predicate: (UnitType) -> Boolean): Int {
        return units.entries.sumBy { (unitType, hps) ->
            if (predicate(unitType)) hps.counts.entries.sumBy { it.key * it.value } else 0
        }
    }

    /**
     * Returns a copy of this [Army] without units that only fire during the opening round, as per
     *  [UnitType.firstRoundOnly].
     */
    fun withoutFirstRoundOnlyUnits(): Army {
        return if (units.keys.any { it.firstRoundOnly }) {
            copy(units = units.filterKeys { !it.firstRoundOnly })
        } else {
            this
        }
    }

    /**
     * Returns a copy of this [Army] with the given number of the given [UnitType], all at their
     *  [UnitType.maxHp].
     * If [count] is zero or negative, all units of [unitType] will be removed.
     */
    fun withUnitCount(unitType: UnitType, count: Int): Army {
        return if (count <= 0) {
            copy(units = units.minus(unitType))
        } else {
            copy(units = units.plus(unitType to multiSetOf(unitType.maxHp to count)))
        }
    }

    /**
     * Computes the [HitProfile] that this [Army] inflicts in a single round, rolling with the given
     *  [Random].
     */
    fun rollHits(rand: Random, enemies: Army, isOpeningFire: Boolean): HitProfile {
        var generalHits = 0
        val domainHits = EnumMap<Domain, Int>(Domain::class.java)

        val supportingArtillery = if (isAttacking) count(UnitType.ARTILLERY) else 0

        units.forEach { unitType, hps ->
            if (unitType.hasOpeningFire(enemies = enemies) == isOpeningFire) {
                fun roll(count: Int, rollLimit: Int) {
                    val rolls = count * unitType.numberOfRolls(
                        enemies = enemies,
                        isAttacking = isAttacking,
                        weaponDevelopments = weaponDevelopments
                    )
                    val hits = rand.rollDice(rolls).count { it <= rollLimit }
                    if (unitType.targetDomain == null) {
                        generalHits += hits
                    } else {
                        domainHits[unitType.targetDomain] =
                                (domainHits[unitType.targetDomain] ?: 0) + hits
                    }
                }

                var remainingCount = hps.size

                if (unitType == UnitType.INFANTRY && supportingArtillery > 0) {
                    val supportedInfantry = Math.min(remainingCount, supportingArtillery)
                    remainingCount -= supportedInfantry

                    roll(
                        count = supportedInfantry,
                        rollLimit = UnitType.ARTILLERY.combatPower(
                            isAttacking = isAttacking,
                            weaponDevelopments = weaponDevelopments
                        )
                    )
                }

                roll(
                    count = remainingCount,
                    rollLimit = unitType.combatPower(
                        isAttacking = isAttacking,
                        weaponDevelopments = weaponDevelopments
                    )
                )
            }
        }

        return HitProfile(generalHits = generalHits, domainHits = domainHits)
    }

    /**
     * Computes the distribution of hits that this [Army] inflicts in a single round.
     */
    fun getHitDistribution(enemies: Army, isOpeningFire: Boolean): HitDistribution {
        var hitDistribution = emptyHitDistribution

        val supportingArtillery = if (isAttacking) count(UnitType.ARTILLERY) else 0

        units.forEach { unitType, hps ->
            if (unitType.hasOpeningFire(enemies = enemies) == isOpeningFire) {
                fun addHits(count: Int, rollLimit: Int) {
                    val rolls = count * unitType.numberOfRolls(
                        enemies = enemies,
                        isAttacking = isAttacking,
                        weaponDevelopments = weaponDevelopments
                    )
                    hitDistribution = hitDistribution.plusBinomial(
                        domain = unitType.targetDomain,
                        p = Rational(rollLimit, 6),
                        n = rolls
                    )
                }

                var remainingCount = hps.size

                if (unitType == UnitType.INFANTRY && supportingArtillery > 0) {
                    val supportedInfantry = Math.min(remainingCount, supportingArtillery)
                    remainingCount -= supportedInfantry

                    addHits(
                        count = supportedInfantry,
                        rollLimit = UnitType.ARTILLERY.combatPower(
                            isAttacking = isAttacking,
                            weaponDevelopments = weaponDevelopments
                        )
                    )
                }

                addHits(
                    count = remainingCount,
                    rollLimit = unitType.combatPower(
                        isAttacking = isAttacking,
                        weaponDevelopments = weaponDevelopments
                    )
                )
            }
        }

        return hitDistribution
    }

    /**
     * Returns a copy of this [Army] with the given [HitProfile] inflicted.
     */
    fun takeHits(hits: HitProfile): Army {
        if (hits.isEmpty()) return this
        if (isWipedBy(hits)) return copy(units = emptyMap())

        // first take as many hits as damage on units as possible
        // (i.e. bring units down to 1hp before taking casualties)
        val (armyAfterDamage, hitsAfterDamage) = takeDamage(hits)

        // then have the CasualtyPicker pick which units take the casualties and apply them
        val casualties = casualtyPicker.pick(
            army = armyAfterDamage,
            hits = hitsAfterDamage,
            isAttacking = isAttacking
        )
        armyAfterDamage.checkCasualties(casualties = casualties, hits = hitsAfterDamage)

        return copy(
            units = armyAfterDamage.units.mapValuesIgnoringNull { (unitType, hps) ->
                val casualtiesOfType = casualties[unitType] ?: 0
                val possibleCasualties = hps.countOf(1)
                when {
                    casualtiesOfType == 0 -> hps
                    casualtiesOfType > possibleCasualties ->
                        throw CasualtyPicker.InvalidCasualtiesError.TooManyOfType(
                            unitType = unitType,
                            casualties = casualtiesOfType,
                            units = hps.size
                        )
                    casualtiesOfType == possibleCasualties -> null
                    else -> hps.minus(element = 1, n = casualtiesOfType)
                }
            }
        )
    }

    private fun takeDamage(hits: HitProfile): Pair<Army, HitProfile> {
        var remainingArmy = this
        var remainingHits = hits
        hits.domainHits.forEach { (domain, count) ->
            if (count > 0) {
                val (armyAfterDamage, countAfterDamage) = remainingArmy.takeDamage(count, domain)
                if (countAfterDamage != count) {
                    remainingArmy = armyAfterDamage
                    remainingHits = remainingHits.copy(
                        domainHits = hits.domainHits.plus(domain to countAfterDamage)
                    )
                }
            }
        }

        if (hits.generalHits > 0) {
            val (armyAfterDamage, countAfterDamage) =
                    remainingArmy.takeDamage(hits.generalHits, null)
            if (countAfterDamage != hits.generalHits) {
                remainingArmy = armyAfterDamage
                remainingHits = remainingHits.copy(generalHits = countAfterDamage)
            }
        }

        return Pair(remainingArmy, remainingHits)
    }

    private fun takeDamage(hits: Int, domain: Domain?): Pair<Army, Int> {
        if (units.all {
                it.key.firstRoundOnly || (domain != null && it.key.domain != domain) ||
                        it.value.hasOnly(1)
            }
        ) {
            return Pair(this, hits)
        }

        var remainingHits = hits
        val afterDamage = units
            .mapValues { (unitType, hps) ->
                when {
                    unitType.firstRoundOnly -> hps
                    domain != null && unitType.domain != domain -> hps
                    remainingHits == 0 -> hps
                    hps.hasOnly(1) -> hps
                    else -> {
                        hps.map { hp ->
                            val hitsTaken = Math.min(hp - 1, remainingHits)
                            remainingHits -= hitsTaken
                            hp - hitsTaken
                        }
                    }
                }
            }

        return Pair(copy(units = afterDamage), remainingHits)
    }

    private fun isWipedBy(hits: HitProfile): Boolean {
        val domainHpTaken = hits.domainHits.entries.sumBy { (domain, domainHits) ->
            val domainHp = totalHp { it.domain == domain && !it.firstRoundOnly }
            Math.min(domainHits, domainHp)
        }

        val totalHp = totalHp { !it.firstRoundOnly }
        return hits.generalHits >= totalHp - domainHpTaken
    }

    private fun checkCasualties(casualties: Map<UnitType, Int>, hits: HitProfile) {
        var possibleDomainHits = 0
        hits.domainHits.forEach { domain, count ->
            val unitsInDomain = count { it.domain == domain && !it.firstRoundOnly }
            val casualtiesInDomain = casualties.entries.sumBy {
                if (it.key.domain == domain) it.value else 0
            }
            val possibleHits = Math.min(count, unitsInDomain)
            possibleDomainHits += possibleHits

            // check that the number of casualties taken in this domain is at least the min between
            // the number of units in this domain and the number of domain-specific hits
            if (casualtiesInDomain < possibleHits) {
                throw CasualtyPicker.InvalidCasualtiesError.TooFewInDomain(
                    domain = domain,
                    casualties = casualtiesInDomain,
                    hits = count
                )
            }
        }

        val remainingCasualties = casualties.values.sum() - possibleDomainHits
        val totalUnits = count { !it.firstRoundOnly }
        val remainingHits = hits.generalHits

        // check that the total number of casualties (minus the domain-specific hits) is at least
        // the min between the total number of units and the domain-agnostic hits
        if (remainingCasualties < Math.min(remainingHits, totalUnits)) {
            throw CasualtyPicker.InvalidCasualtiesError.TooFewInDomain(
                domain = null,
                casualties = remainingCasualties,
                hits = remainingHits
            )
        }
    }
}
