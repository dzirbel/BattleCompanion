package com.github.dzirbel.battlecompanion.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView

class VerticalTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = currentTextColor
        paint.drawableState = drawableState

        canvas.save()

        canvas.translate(measuredWidth.toFloat(), 0F)
        canvas.rotate(90F)
        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())

        layout.draw(canvas)

        canvas.restore()
    }
}
