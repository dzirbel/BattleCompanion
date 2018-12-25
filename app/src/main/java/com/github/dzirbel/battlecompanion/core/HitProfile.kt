package com.github.dzirbel.battlecompanion.core

/**
 * In order to have correct domain-specific hits (such as submarines only hitting sea units), the profile of a round of
 *  hits is more complicated than simply the total number of hits.
 * A [HitProfile] is a [Map] from the [Domain]s which are being specifically hit to the number of hits, with the null
 *  [Domain] key representing hits that can be applied to all [Domain]s (the usual case).
 * Combat correctness dictates that domain-specific hits must be applied before domain-agnostic hits.
 * Since [HitProfile] is a [Map] (and not a [MutableMap]), it is immutable.
 */
typealias HitProfile = Map<Domain?, Int>

/**
 * Returns a copy of this [HitProfile] with the given additional hits to the given [Domain].
 */
fun HitProfile.plusHits(key: Domain?, hits: Int): HitProfile {
    if (hits == 0) {
        return this
    }

    return plus(key to (this[key] ?: 0) + hits)
}
