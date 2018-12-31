package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational

/**
 * Represents a distribution of [HitProfile]s as a map from each possible [HitProfile] to its
 *  probability.
 * Note that [HitDistribution] is immutable.
 */
typealias HitDistribution = Map<HitProfile, Rational>

/**
 * A default, empty [HitDistribution] which maps a [HitProfile] with no hits to probability 1.
 */
val emptyHitDistribution = mapOf(
    HitProfile(generalHits = 0, domainHits = emptyMap()) to Rational.ONE
)

/**
 * Returns a copy of this [HitDistribution] plus a binomial distribution of hits in the given
 *  [Domain], with the given parameters [p] and [n].
 *
 * @param p the probability of a single hit
 * @param n the number of shots, each with independent probability [p]
 */
fun HitDistribution.plusBinomial(domain: Domain?, p: Rational, n: Int): HitDistribution {
    return flatMapAndReduce(Rational::plus) { hitProfile, originalChance ->
        val map = mutableMapOf<HitProfile, Rational>()
        for (hits in 0..n) {
            map[hitProfile.plus(hits = hits, domain = domain)] =
                    binomial(p = p, n = n, k = hits) * originalChance
        }
        map
    }
}
