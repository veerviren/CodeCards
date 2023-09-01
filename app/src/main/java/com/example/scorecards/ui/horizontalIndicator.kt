package com.example.scorecards.ui

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class HorizontalIndicator : ItemDecoration() {
    private val colorActive = -0x1
    private val colorInactive = 0x66FFFFFF

    private val mIndicatorHeight = (DP * 16).toInt()
    private val mIndicatorStrokeWidth = DP * 2
    private val mIndicatorItemLength = DP * 16
    private val mIndicatorItemPadding = DP * 4
    private val mMaxVisibleIndicators = 3 // Maximum of 3 indicators (2 inactive dots and 1 active line)

    private val mInterpolator: AccelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
    private val mPaint = Paint()

    init {
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = mIndicatorStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = parent.adapter?.itemCount ?: 0

        // Calculate the total width of all indicators and spacing
        val totalIndicatorWidth = (mIndicatorItemLength * itemCount) +
                (mIndicatorItemPadding * (itemCount - 1))

        // Calculate the starting X position to center the indicators
        val indicatorStartX = (parent.width - totalIndicatorWidth) / 2f

        // Center vertically in the allotted space
        val indicatorPosY = parent.height - mIndicatorHeight / 2f

        // Find the active page (which should be highlighted)
        val layoutManager = parent.layoutManager as LinearLayoutManager?
        val activePosition = layoutManager?.findFirstVisibleItemPosition() ?: RecyclerView.NO_POSITION
        if (activePosition == RecyclerView.NO_POSITION) {
            return
        }

        // Find offset of active page (if the user is scrolling)
        val activeChild = layoutManager?.findViewByPosition(activePosition)
        val left = activeChild?.left ?: 0
        val width = activeChild?.width ?: 0

        // Calculate the swipe progress
        val progress: Float = mInterpolator.getInterpolation(left * -1 / width.toFloat())

        // Calculate the current active position based on progress
        val currentActivePosition = if (progress < 0.5) activePosition else activePosition + 1

        // Draw the indicators
        drawIndicators(c, indicatorStartX, indicatorPosY, itemCount, currentActivePosition, progress)
    }
    private fun drawIndicators(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int,
        activePosition: Int,
        progress: Float
    ) {
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding

        for (i in 0 until itemCount) {
            // Calculate the position of the active indicator (line) and its width based on progress
            val activeIndicatorStartX = indicatorStartX + itemWidth * i
            val activeIndicatorWidth = if (i == activePosition) {
                mIndicatorItemLength + progress * itemWidth
            } else {
                mIndicatorItemLength
            }

            // Calculate the position of the first inactive indicator (dot)
            val firstInactiveIndicatorCenterX = indicatorStartX + itemWidth / 2

            // Calculate the position of the last inactive indicator (dot)
            val lastInactiveIndicatorCenterX =
                indicatorStartX + itemWidth * (itemCount - 1) + itemWidth / 2

            if (i == activePosition) {
                // Draw the active indicator as a line in the middle
                mPaint.color = colorActive
                c.drawLine(
                    activeIndicatorStartX, indicatorPosY,
                    activeIndicatorStartX + activeIndicatorWidth, indicatorPosY, mPaint
                )
            } else if (i == 0) {
                // Draw the first inactive indicator as a dot
                mPaint.color = colorInactive
                val dotRadius = mIndicatorStrokeWidth / 2
                c.drawCircle(
                    firstInactiveIndicatorCenterX, indicatorPosY, dotRadius, mPaint
                )
            } else if (i == itemCount - 1) {
                // Draw the last inactive indicator as a dot
                mPaint.color = colorInactive
                val dotRadius = mIndicatorStrokeWidth / 2
                c.drawCircle(
                    lastInactiveIndicatorCenterX, indicatorPosY, dotRadius, mPaint
                )
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = mIndicatorHeight
    }

    companion object {
        private val DP = Resources.getSystem().displayMetrics.density
    }
}
