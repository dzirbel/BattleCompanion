package com.github.dzirbel.battlecompanion.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.Board
import com.github.dzirbel.battlecompanion.core.CasualtyPicker
import com.github.dzirbel.battlecompanion.core.Domain
import com.github.dzirbel.battlecompanion.core.UnitType
import com.github.dzirbel.battlecompanion.core.WeaponDevelopment
import kotlinx.android.synthetic.main.board_activity.*
import kotlinx.android.synthetic.main.board_tools.*
import kotlin.random.Random

class BoardActivity : AppCompatActivity() {

    private val defaultCasualtyPicker = CasualtyPicker.ByCost()

    // TODO save/restore board
    private var board = Board(
        attackers = Army(
            units = emptyMap(),
            isAttacking = true,
            casualtyPicker = defaultCasualtyPicker,
            weaponDevelopments = emptySet()
        ),
        defenders = Army(
            units = emptyMap(),
            isAttacking = false,
            casualtyPicker = defaultCasualtyPicker,
            weaponDevelopments = emptySet()
        )
    )
        set(value) {
            field = value
            attackerAdapter.updateUnitCounts(board.attackers)
            defenderAdapter.updateUnitCounts(board.defenders)

            roll.isEnabled = board.outcome == null
        }

    private var attackerAdapter = UnitTypeAdapter(isAttacking = true, unitTypes = emptyList())
    private var defenderAdapter = UnitTypeAdapter(isAttacking = false, unitTypes = emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        attackerUnits.layoutManager = ColumnLayoutManager(this)
        defenderUnits.layoutManager = ColumnLayoutManager(this)

        attackerUnits.adapter = attackerAdapter
        defenderUnits.adapter = defenderAdapter

        roll.setOnClickListener { board = board.roll(Random) }

        attackerHeader.setOnClickListener { showArmyDialog(isAttacking = true) }
        defenderHeader.setOnClickListener { showArmyDialog(isAttacking = false) }

        domainGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.land -> setUnitTypes(Domain.LAND)
                R.id.sea -> setUnitTypes(Domain.SEA)
                else -> throw IllegalArgumentException()
            }
        }

        domainGroup.check(R.id.land)
    }

    fun getArmy(isAttacking: Boolean) = if (isAttacking) board.attackers else board.defenders

    fun setUnitCount(unitType: UnitType, count: Int, isAttacking: Boolean) {
        board = if (isAttacking) {
            board.copy(
                attackers = board.attackers.withUnitCount(unitType = unitType, count = count)
            )
        } else {
            board.copy(
                defenders = board.defenders.withUnitCount(unitType = unitType, count = count)
            )
        }
    }

    fun setWeaponDevelopments(weaponDevelopments: Set<WeaponDevelopment>, isAttacking: Boolean) {
        board = if (isAttacking) {
            board.copy(
                attackers = board.attackers.copy(weaponDevelopments = weaponDevelopments)
            )
        } else {
            board.copy(
                defenders = board.defenders.copy(weaponDevelopments = weaponDevelopments)
            )
        }
    }

    private fun showArmyDialog(isAttacking: Boolean) {
        val fragment = ArmyDialogFragment()
        fragment.isAttacking = isAttacking
        fragment.weaponDevelopments = if (isAttacking) {
            board.attackers.weaponDevelopments
        } else {
            board.defenders.weaponDevelopments
        }
        fragment.show(supportFragmentManager, "attacker_army_dialog")
    }

    private fun setUnitTypes(domain: Domain) {
        attackerAdapter.unitTypes = UnitType.values().filter {
            it.canAttackIn(domain) &&
                    it.hasRequiredWeaponDevelopments(board.attackers.weaponDevelopments)
        }

        defenderAdapter.unitTypes = UnitType.values().filter {
            it.canDefendIn(domain) &&
                    it.hasRequiredWeaponDevelopments(board.defenders.weaponDevelopments)
        }

        // TODO just notifyDataSetChanged() causes the ColumnLayoutManager to get messed up
        attackerUnits.adapter = attackerAdapter
        defenderUnits.adapter = defenderAdapter
    }
}
