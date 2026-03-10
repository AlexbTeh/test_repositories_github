package com.done.weather.ui.compose_ui.camera_screen_vision

import com.done.weather.domain.model.camera_detect.DetectionBox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private var boxes: List<DetectionBox> = emptyList()

    fun setBoxes(newBoxes: List<DetectionBox>) {
        boxes = newBoxes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        for (b in boxes) {
            val left = b.left * w
            val top = b.top * h
            val right = b.right * w
            val bottom = b.bottom * h
            canvas.drawRect(left, top, right, bottom, paint)
        }
    }
}
