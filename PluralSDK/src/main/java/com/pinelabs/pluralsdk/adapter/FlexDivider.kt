package com.pinelabs.pluralsdk.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.pinelabs.pluralsdk.R

class FlexDivider(context: Context) : RecyclerView.ItemDecoration() {

    private val dividerSize: Int = context.resources.getDimensionPixelSize(R.dimen.divider_space)
    private val paint: Paint = Paint().apply {
        color = context.resources.getColor(R.color.grey_button_border)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        // Draw vertical dividers
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val top = child.top
            val bottom = child.bottom
            val right = left + dividerSize

            // Draw divider
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }

        // Draw horizontal dividers
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val left = child.left
            val right = child.right
            val bottom = top + dividerSize

            // Draw divider
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}