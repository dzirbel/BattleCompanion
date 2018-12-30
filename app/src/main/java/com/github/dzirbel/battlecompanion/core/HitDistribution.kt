package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational

typealias HitDistribution = Map<HitProfile, Rational>

val emptyHitDistribution = mapOf(
    HitProfile(generalHits = 0, domainHits = emptyMap()) to Rational.ONE
)

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
