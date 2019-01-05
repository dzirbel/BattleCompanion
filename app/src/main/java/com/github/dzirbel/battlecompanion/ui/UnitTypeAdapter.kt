package com.github.dzirbel.battlecompanion.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.Domain
import com.github.dzirbel.battlecompanion.core.UnitType

class UnitTypeAdapter(domains: Set<Domain>) : RecyclerView.Adapter<UnitTypeViewHolder>() {

    private val unitTypes = UnitType.values().filter { domains.contains(it.domain) }

    override fun getItemCount() = unitTypes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitTypeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.board_unit, parent, false)
        return UnitTypeViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: UnitTypeViewHolder, position: Int) {
        viewHolder.bind(unitTypes[position])
    }
}
