package com.pinelabs.pluralsdk.activity

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ActionMode
import androidx.appcompat.widget.AppCompatEditText


class OtpEditText : AppCompatEditText {
    private var mSpace = 24f //24 dp by default, space between the lines
    private var mNumChars = 4f
    private var mLineSpacing = 8f //8dp by default, height of the text from our lines
    private val mMaxLength = 4
    private var mLineStroke = 2f
    private var mLinesPaint: Paint? = null
    private var mClickListener: OnClickListener? = null

    constructor(context: Context?) : super(context!!)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val multi = context.resources.displayMetrics.density
        mLineStroke = multi * mLineStroke
        mLinesPaint = Paint(paint)
        mLinesPaint!!.strokeWidth = mLineStroke
        mLinesPaint!!.color = resources.getColor(R.color.black)
        setBackgroundResource(0)
        mSpace = multi * mSpace //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing //convert to pixels for our density
        mNumChars = mMaxLength.toFloat()

        super.setOnClickListener { v -> // When tapped, move cursor to end of text.
            setSelection(text!!.length)
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas) {
        val availableWidth = width - paddingRight - paddingLeft
        val mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars
        }

        // Calculate startX to center the content
        var startX = paddingLeft + (availableWidth - (mNumChars * mCharSize + (mSpace * (mNumChars - 1)))) / 2f
        val middleY = height / 2f  // Middle of the height

        // Set the line color to grey and reduce the width
        mLinesPaint?.color = Color.GRAY  // Set the line color to grey
        mLinesPaint?.strokeWidth = 4f  // Reduce the stroke width (adjust as needed)

        // Text Width
        val text = text
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)

        var i = 0
        while (i < mNumChars) {
            // Adjust the Y position to center the line in the middle of the text
            val lineCenterY = middleY  // You can adjust this value based on your needs

            // Reduce the length of the line (e.g., draw it at 80% of mCharSize)
            val lineEndX = startX + mCharSize * 0.8f  // 80% of the original line length

            // Draw the line centered vertically in the middle of the text with reduced length
            canvas.drawLine(
                startX, lineCenterY, lineEndX, lineCenterY,
                mLinesPaint!!  // Use the updated paint for drawing the line
            )

            // Draw the text in the correct position
            if (getText()!!.length > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(
                    text, i, i + 1, middle - textWidths[0] / 2, middleY - mLineSpacing,
                    paint
                )
            }

            // Update the startX based on whether there is space between the characters
            startX = if (mSpace < 0) {
                (startX + mCharSize * 2)
            } else {
                (startX + (mCharSize + mSpace))
            }
            i++
        }
    }
}
