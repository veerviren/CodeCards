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
    private val mIndicatorItemRadius = DP * 4 // Radius of the dots
    private val mIndicatorItemPadding = DP * 4

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
        val totalIndicatorWidth = (mIndicatorItemRadius * 2 * itemCount) +
                (mIndicatorItemPadding * (itemCount - 1))

        // Calculate the starting X position to center the indicators
        val indicatorStartX = (parent.width - totalIndicatorWidth) / 2f

        // Calculate the Y position for the indicators at the bottom of the RecyclerView
        val indicatorPosY = parent.height - mIndicatorHeight / 2f

        // Find the active page (which should be highlighted)
        val layoutManager = parent.layoutManager as LinearLayoutManager?
        val activePosition = layoutManager?.findFirstVisibleItemPosition() ?: RecyclerView.NO_POSITION
        if (activePosition == RecyclerView.NO_POSITION) {
            return
        }

        // Calculate the swipe progress
        val activeChild = layoutManager?.findViewByPosition(activePosition)
        val left = activeChild?.left ?: 0
        val width = activeChild?.width ?: 0
        val progress: Float = mInterpolator.getInterpolation(left * -1 / width.toFloat())

        // Calculate the current active position based on progress
        val currentActivePosition = if (progress < 0.5) activePosition else activePosition + 1

        // Draw the indicators
        drawIndicators(c, indicatorStartX, indicatorPosY, itemCount, currentActivePosition)
    }

    private fun drawIndicators(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int,
        activePosition: Int
    ) {
        val itemWidth = mIndicatorItemRadius * 2 + mIndicatorItemPadding

        for (i in 0 until itemCount) {
            val centerX = indicatorStartX + itemWidth * i
            val centerY = indicatorPosY

            if (i == activePosition) {
                // Draw the active indicator as a solid sphere
                mPaint.color = colorActive
                mPaint.style = Paint.Style.FILL // Set the paint style to fill
                c.drawCircle(centerX, centerY, mIndicatorItemRadius, mPaint)
            } else {
                // Draw inactive indicators as solid spheres
                mPaint.color = colorInactive
                mPaint.style = Paint.Style.FILL // Set the paint style to fill
                c.drawCircle(centerX, centerY, mIndicatorItemRadius, mPaint)
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
