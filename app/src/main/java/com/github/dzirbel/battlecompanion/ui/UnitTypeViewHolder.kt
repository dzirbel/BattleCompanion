package com.github.dzirbel.battlecompanion.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.dzirbel.battlecompanion.core.UnitType
import kotlinx.android.synthetic.main.board_unit_bottom.view.*

class UnitTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val boardActivity = itemView.context as BoardActivity

    fun bind(unitType: UnitType, isAttacking: Boolean) {
        itemView.apply {
            unitName.text = unitType.shortName
            unitCount.text = boardActivity.getArmy(isAttacking).count(unitType).toString()

            plusOne.setOnClickListener {
                boardActivity.setUnitCount(
                    unitType = unitType,
                    count = boardActivity.getArmy(isAttacking).count(unitType) + 1,
                    isAttacking = isAttacking
                )
            }

            minusOne.setOnClickListener {
                boardActivity.setUnitCount(
                    unitType = unitType,
                    count = boardActivity.getArmy(isAttacking).count(unitType) - 1,
                    isAttacking = isAttacking
                )
            }
        }
    }

    fun bindCount(count: Int) {
        itemView.unitCount.text = count.toString()
    }
}
