package com.mahdikh.vision.viewpagerindicator.indicator

import android.graphics.Canvas
import com.mahdikh.vision.viewpagerindicator.LoopDraw
import com.mahdikh.vision.viewpagerindicator.indicator.abstractions.TransformIndicator
import com.mahdikh.vision.viewpagerindicator.info.IndicatorInfo
import com.mahdikh.vision.viewpagerindicator.util.Paint2

class SnowFlakeIndicator : TransformIndicator(), LoopDraw {
    override var loopCount: Int = 5
    var factor: Float = 0.5F

    private var cx: Float = 0.0F
    private var cy: Float = 0.0F
    private var topPoint: Float = 0.0F
    private var diameter: Float = 0.0F
    private var radius: Float = 0.0F

    override fun onReady() {
        super.onReady()
        setCoordinates()
    }

    override fun onStructureChanged() {
        super.onStructureChanged()
        setCoordinates()
    }

    private fun setCoordinates() {
        radius = computeCircleRadius()
        cx = cx(baseInfo)
        cy = cy(baseInfo)
        topPoint = baseInfo.y + radius
        diameter = baseInfo.y + radius * 2
    }

    override fun onDrawing(canvas: Canvas, info: IndicatorInfo, paint: Paint2) {
        loopDraw(canvas, cx, cy) {
            canvas.drawLine(cx, cy, cx, diameter, paint)
            canvas.drawCircle(cx, topPoint, radius, paint)
        }
    }

    private fun computeCircleRadius(): Float = factor * (size() / 2F)
}