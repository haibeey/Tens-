package com.sender.ui.send

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.sender.R

class ProgressSendingReceiving @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var progressColor = R.color.faintPrimary
    private var bgProgress = 0f
    private val rectF = RectF()
    private val paint = Paint()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressSendingReceiving,
            0, 0).apply {
            try {
                progressColor = getInt(R.styleable.ProgressSendingReceiving_bgColor, R.color.faintPrimary)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val pos = width * (bgProgress/100f)

        rectF.top=0f
        rectF.bottom=height*1f
        rectF.left=0f
        rectF.right=pos
        paint.color = ContextCompat.getColor(context, R.color.faintPrimary)

        canvas?.drawRect(rectF,paint)

    }

    fun updateProgress(progress : Float){
        if (progress<0)bgProgress=0f
        if (progress>100)bgProgress=progress
        bgProgress=progress
        invalidate()
        requestLayout()
    }

}