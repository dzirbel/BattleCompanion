package com.github.dzirbel.battlecompanion.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.Domain
import com.github.dzirbel.battlecompanion.core.UnitType
import com.github.dzirbel.battlecompanion.core.WeaponDevelopment
import kotlinx.android.synthetic.main.board_activity.*
import kotlinx.android.synthetic.main.board_tools.*

class BoardActivity : AppCompatActivity() {

    private val attackerWeaponDevelopments = emptySet<WeaponDevelopment>()
    private val defenderWeaponDevelopments = emptySet<WeaponDevelopment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        attackerUnits.layoutManager = ColumnLayoutManager(this)
        defenderUnits.layoutManager = ColumnLayoutManager(this)

        domainGroup.setOnCheckedChangeListener { _, checkedId ->
            val domain = when (checkedId) {
                R.id.land -> Domain.LAND
                R.id.sea -> Domain.SEA
                else -> throw IllegalArgumentException()
            }

            val attackingUnits = UnitType.values().filter {
                it.canAttackIn(domain) &&
                        it.hasRequiredWeaponDevelopments(attackerWeaponDevelopments)
            }
            val defendingUnits = UnitType.values().filter {
                it.canDefendIn(domain) &&
                        it.hasRequiredWeaponDevelopments(defenderWeaponDevelopments)
            }

            attackerUnits.adapter = UnitTypeAdapter(top = true, units = attackingUnits)
            defenderUnits.adapter = UnitTypeAdapter(top = false, units = defendingUnits)
        }

        domainGroup.check(R.id.land)
    }
}
