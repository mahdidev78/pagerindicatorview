package com.mahdikh.vision.pagerindicatorview.progress

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.viewpager.widget.ViewPager
import com.mahdikh.vision.pagerindicatorview.indicator.Indicator
import com.mahdikh.vision.pagerindicatorview.info.IndicatorInfo
import com.mahdikh.vision.pagerindicatorview.util.Paint2
import com.mahdikh.vision.pagerindicatorview.widget.PagerIndicatorView

abstract class IndicatorProgress {
    val paint: Paint2 = Paint2()
    protected open lateinit var pagerIndicatorView: PagerIndicatorView
    protected lateinit var indicator: Indicator
    protected lateinit var currentInfo: IndicatorInfo
    protected lateinit var destinationInfo: IndicatorInfo
    private var oldDestinationPosition = 0
    open var interpolator: TimeInterpolator? = null
    open var keepDraw = false
    var hideBottom = true
    var paintFromIndicator: Boolean = false
    var inProgress: Boolean = false
        protected set
    var offset: Float = 0.0F
        private set

    @CallSuper
    open fun onPageScrolled(position: Int, offset: Float, state: Int) {
        val currentPosition = pagerIndicatorView.getCurrentItem()
        var destinationPosition: Int

        destinationPosition = if (currentPosition == position) {
            if (offset <= 0.005f) {
                currentPosition
            } else {
                currentPosition + 1
            }
        } else {
            currentPosition - 1
        }

        if (destinationPosition <= 0) destinationPosition = 0

        if (destinationPosition != oldDestinationPosition && state != ViewPager.SCROLL_STATE_SETTLING) {
            val oldCurrent = currentInfo
            val oldDestination = destinationInfo

            currentInfo = pagerIndicatorView.getIndicatorInfo(currentPosition)
            destinationInfo = pagerIndicatorView.getIndicatorInfo(destinationPosition)

            onIndicatorInfoChanged(oldCurrent, oldDestination)
        }
        this.offset = offset
        oldDestinationPosition = destinationPosition
        pagerIndicatorView.invalidate()
    }

    fun onPageScrollStateChanged(state: Int) {
        inProgress = state != ViewPager.SCROLL_STATE_IDLE
        pagerIndicatorView.invalidate()
        onScrollStateChanged(state)
    }

    open fun onScrollStateChanged(state: Int) {
        currentInfo.draw = true
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            currentInfo = pagerIndicatorView.getIndicatorInfo(pagerIndicatorView.getCurrentItem())
            destinationInfo = currentInfo

            if (keepDraw && hideBottom) {
                currentInfo.draw = false
            }
        }
    }

    open fun setStrokeWidth(strokeWidth: Float) {
        paint.strokeWidth = strokeWidth
    }

    open fun getColor(): Int {
        if (paintFromIndicator)
            return indicator.paint.color
        return paint.color
    }

    open fun setColor(@ColorInt color: Int) {
        paint.color = color
    }

    open fun setStyle(style: Paint.Style) {
        paint.style = style
    }

    internal fun setIndicator(indicator: Indicator) {
        this.indicator = indicator
    }

    internal fun setPagerIndicatorView(pagerIndicatorView: PagerIndicatorView) {
        this.pagerIndicatorView = pagerIndicatorView
    }

    @CallSuper
    open fun onReady() {
        currentInfo = pagerIndicatorView.getIndicatorInfo(pagerIndicatorView.getCurrentItem())
        destinationInfo = currentInfo

        if (keepDraw && hideBottom) {
            pagerIndicatorView.getInfoList()[pagerIndicatorView.getCurrentItem()].draw = false
        }
    }

    open fun draw(canvas: Canvas) {
        if (inProgress || keepDraw) {
            if (paintFromIndicator) {
                val p = indicator.paint
                val color = p.color
                p.color = pagerIndicatorView.selectedColor
                onDraw(canvas, p)
                p.color = color
            } else {
                onDraw(canvas, paint)
            }
        }
    }

    open fun onIndicatorInfoChanged(
        oldCurrent: IndicatorInfo,
        oldDestination: IndicatorInfo
    ) {
        oldCurrent.draw = true
        oldDestination.draw = true
    }


    open fun onPreDraw(canvas: Canvas) {
        // Do Nothing
    }

    abstract fun onDraw(canvas: Canvas, paint: Paint2)

    protected fun cx(info: IndicatorInfo): Float = indicator.cx(info)

    protected fun cy(info: IndicatorInfo): Float = indicator.cy(info)

    fun computeDistance(): Float = cx(destinationInfo) - cx(currentInfo)

    open fun getInterpolation(input: Float): Float {
        return interpolator?.getInterpolation(input) ?: input
    }

    open fun fractionInterpolation(): Float = getInterpolation(fraction())

    open fun fraction(): Float {
        return if (computeDistance() > 0) offset else 1.0F - offset
    }

    open fun offsetInterpolation(): Float = getInterpolation(offset)
}