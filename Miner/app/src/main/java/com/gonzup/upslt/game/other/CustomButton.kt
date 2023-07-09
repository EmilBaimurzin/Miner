package com.gonzup.upslt.game.other

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class CustomButton(context: Context, attr: AttributeSet) : AppCompatTextView(context, attr) {
    init {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .start()
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                    v.invalidate()
                }
            }
            false
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}