package com.github.dzirbel.battlecompanion.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.Domain
import kotlinx.android.synthetic.main.board_activity.*
import kotlinx.android.synthetic.main.board_tools.*

class BoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        attackerUnits.layoutManager = ColumnLayoutManager(this)
        defenderUnits.layoutManager = ColumnLayoutManager(this)

        domainGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.land -> setDomains(setOf(Domain.LAND, Domain.AIR))
                R.id.sea -> setDomains(setOf(Domain.SEA, Domain.AIR))
                else -> throw IllegalArgumentException()
            }
        }

        domainGroup.check(R.id.land)
    }

    private fun setDomains(domains: Set<Domain>) {
        attackerUnits.adapter = UnitTypeAdapter(top = true, domains = domains)
        defenderUnits.adapter = UnitTypeAdapter(top = false, domains = domains)
    }
}
