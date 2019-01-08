package com.github.dzirbel.battlecompanion.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.github.dzirbel.battlecompanion.R
import com.github.dzirbel.battlecompanion.core.WeaponDevelopment

class ArmyDialogFragment : DialogFragment() {

    var weaponDevelopments: Set<WeaponDevelopment> = setOf()
    var isAttacking: Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val boardActivity = activity as? BoardActivity ?: throw NullPointerException()
        val builder = AlertDialog.Builder(boardActivity)
        builder
            .setTitle(R.string.army_dialog_title)
            .setMultiChoiceItems(weaponDevelopmentNames, null) { _, position, isChecked ->
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
//            .setNegativeButton(R.string.cancel) { _, _ -> /* no-op */ }

        return builder.create()
    }

    companion object {

        private val weaponDevelopmentNames =
            WeaponDevelopment.values().map { it.prettyName }.toTypedArray()

        fun getWeaponDevelopmentByPosition(position: Int): WeaponDevelopment {
            return WeaponDevelopment.values()[position]
        }
    }
}
