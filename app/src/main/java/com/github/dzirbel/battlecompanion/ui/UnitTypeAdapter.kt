package com.github.dzirbel.battlecompanion.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.Army
import com.github.dzirbel.battlecompanion.core.UnitType

class UnitTypeAdapter(
    private val isAttacking: Boolean,
    var unitTypes: List<UnitType>
) : RecyclerView.Adapter<UnitTypeViewHolder>() {

    private val layout = if (isAttacking) R.layout.board_unit_top else R.layout.board_unit_bottom

    override fun getItemCount() = unitTypes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitTypeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return UnitTypeViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: UnitTypeViewHolder, position: Int) {
        viewHolder.bind(unitType = unitTypes[position], isAttacking = isAttacking)
    }

    override fun onBindViewHolder(
        viewHolder: UnitTypeViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, position)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    is Int -> viewHolder.bindCount(payload)
                }
            }
        }
    }

    fun updateUnitCounts(army: Army) {
        unitTypes.forEach { unitType ->
            notifyItemChanged(unitTypes.indexOf(unitType), army.count(unitType))
        }
    }
}
