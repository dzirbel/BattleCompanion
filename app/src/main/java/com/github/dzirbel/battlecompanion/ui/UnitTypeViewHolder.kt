package com.github.dzirbel.battlecompanion.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.dzirbel.battlecompanion.core.UnitType
import kotlinx.android.synthetic.main.board_unit_bottom.view.*

class UnitTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(unitType: UnitType) {
        itemView.apply {
            unitName.text = unitType.prettyName
            unitCount.text = "0"
        }
    }
}
