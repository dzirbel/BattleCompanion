package com.github.dzirbel.battlecompanion.ui

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.dzirbel.battlecompanion.core.UnitType
import kotlinx.android.synthetic.main.board_unit.view.*

class UnitTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(unitType: UnitType) {
        itemView.unitName.text = unitType.prettyName
    }
}
