package com.github.dzirbel.battlecompanion.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * A [LinearLayoutManager] which organizes the child views into equal-width columns which fill the
 *  [RecyclerView] horizontally.
 */
class ColumnLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {

    private fun columnWidth(): Int {
        return Math.round((width - paddingRight - paddingLeft).toFloat() / itemCount)
    }

    override fun canScrollVertically() = false
    override fun canScrollHorizontally() = false

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return super.generateDefaultLayoutParams().apply { width = columnWidth() }
    }

    override fun generateLayoutParams(
        context: Context,
        attrs: AttributeSet
    ): RecyclerView.LayoutParams {
        return super.generateLayoutParams(context, attrs).apply { width = columnWidth() }
    }

    override fun generateLayoutParams(
        layoutParams: ViewGroup.LayoutParams
    ): RecyclerView.LayoutParams {
        return super.generateLayoutParams(layoutParams).apply { width = columnWidth() }
    }
}
