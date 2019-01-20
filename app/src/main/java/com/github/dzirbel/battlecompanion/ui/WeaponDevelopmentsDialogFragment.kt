package com.github.dzirbel.battlecompanion.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.WeaponDevelopment

class WeaponDevelopmentsDialogFragment : DialogFragment() {

    var weaponDevelopments: Set<WeaponDevelopment> = setOf()
    var isAttacking: Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val boardActivity = activity as? BoardActivity ?: throw NullPointerException()
        val checked = WeaponDevelopment.values()
            .map { weaponDevelopments.contains(it) }
            .toBooleanArray()
        val title = getString(
            R.string.weapon_developments_title,
            getString(if (isAttacking) R.string.attackers_label else R.string.defenders_label)
        )

        return AlertDialog.Builder(boardActivity)
            .setTitle(title)
            .setMultiChoiceItems(weaponDevelopmentNames, checked) { _, position, isChecked ->
                if (isChecked) {
                    weaponDevelopments += getWeaponDevelopmentByPosition(position)
                } else {
                    weaponDevelopments -= getWeaponDevelopmentByPosition(position)
                }
            }
            .setPositiveButton(R.string.accept) { _, _ ->
                boardActivity.setWeaponDevelopments(
                    weaponDevelopments = weaponDevelopments,
                    isAttacking = isAttacking
                )
            }
            .create()
    }

    companion object {

        private val weaponDevelopmentNames =
            WeaponDevelopment.values().map { it.prettyName }.toTypedArray()

        fun getWeaponDevelopmentByPosition(position: Int): WeaponDevelopment {
            return WeaponDevelopment.values()[position]
        }
    }
}
