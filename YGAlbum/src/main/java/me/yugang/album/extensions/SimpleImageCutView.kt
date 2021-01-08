package me.yugang.album.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatImageView

class SimpleImageCutView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attributes, defStyleAttr) {

    private var mCutLeft = 0
    private var mCutTop = 0
    private var mCutRight = 0
    private var mCutBottom = 0
    private val mCutWidth get() = mCutRight - mCutLeft
    private val mCutHeight get() = mCutBottom - mCutTop

    private val mScroller by lazy { Scroller(context) }

    init {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // TODO: 2021/1/8
    }

    private fun smoothScrollBy(dx: Int, dy: Int) {
        smoothScrollBy(dx, dy, DEFAULT_SCROLL_DURATION)
    }

    private fun smoothScrollBy(dx: Int, dy: Int, duration: Int) {
        mScroller.startScroll(scrollX, scrollY, dx, dy, duration)
        postInvalidate()
    }

    companion object {
        private const val DEFAULT_SCROLL_DURATION = 200
    }

    private enum class Action {
        None, Move, PointLT, PointLB, PointRT, PointRB
    }
}