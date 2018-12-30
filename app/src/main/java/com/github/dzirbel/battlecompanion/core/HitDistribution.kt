package com.github.dzirbel.battlecompanion.core

import com.github.dzirbel.battlecompanion.util.Rational

typealias HitDistribution = Map<HitProfile, Rational>

val emptyHitDistribution = mapOf(
    HitProfile(generalHits = 0, domainHits = emptyMap()) to Rational.ONE
)

fun HitDistribution.plusBinomial(domain: Domain?, p: Rational, n: Int): HitDistribution {
    return flatMapAndReduce(Rational::plus) { hitProfile, originalChance ->
        List(n + 1) { hits ->
            Pair(
                hitProfile.plus(hits = hits, domain = domain),
                binomial(k = hits, n = n, p = p) * originalChance
            )
        }.toMap()
    }
}
