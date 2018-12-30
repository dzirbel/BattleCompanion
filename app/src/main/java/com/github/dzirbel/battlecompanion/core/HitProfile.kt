package com.github.dzirbel.battlecompanion.core

/**
 * In order to have correct domain-specific hits (such as submarines only hitting sea units), the
 *  profile of a round of hits is more complicated than simply the total number of hits.
 * A [HitProfile] contains a [Map] from the [Domain]s which are being specifically hit to the number
 *  of hits on that [Domain], plus [generalHits] which counts hits that can be applied to all
 *  [Domain]s (the usual case).
 * Combat correctness dictates that domain-specific hits must be applied before domain-agnostic
 *  hits.
 * Since [domainHits] is a [Map] (and not a [MutableMap]), [HitProfile] is immutable.
 */
data class HitProfile(
    val generalHits: Int,
    val domainHits: Map<Domain, Int>
) {

    /**
     * Determines whether this [HitProfile] has any hits.
     */
    fun isEmpty() = generalHits == 0 && domainHits.isEmpty()

    /**
     * Returns a [HitProfile] plus the given [hits] in the given [domain] (or null for
     *  [generalHits]).
     */
    fun plus(hits: Int, domain: Domain?): HitProfile {
        if (hits == 0) return this

        return when (domain) {
            null -> copy(generalHits = generalHits + hits)
            else -> copy(domainHits = domainHits.plus(domain to (domainHits[domain] ?: 0) + hits))
        }
    }

    override fun toString(): String {
        if (isEmpty()) return "No hits"

        return domainHits
            .plus(null to generalHits)
            .filter { it.value > 0 }
            .map { (domain, hits) -> "$hits to ${domain?.name?.toLowerCase() ?: "anywhere"}" }
            .joinToString()
    }
}
