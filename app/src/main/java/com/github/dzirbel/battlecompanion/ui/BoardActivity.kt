package com.github.dzirbel.battlecompanion.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.Domain
import kotlinx.android.synthetic.main.board_activity.*

class BoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        attackerUnits.layoutManager = ColumnLayoutManager(this)
        defenderUnits.layoutManager = ColumnLayoutManager(this)

        attackerUnits.adapter = UnitTypeAdapter(domains = setOf(Domain.LAND, Domain.AIR))
        defenderUnits.adapter = UnitTypeAdapter(domains = setOf(Domain.LAND, Domain.AIR))
    }
}
