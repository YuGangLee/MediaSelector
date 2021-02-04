package co.yugang.album.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class SimpleImageCutView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attributes, defStyleAttr) {

    // private param
    private val mCutRect = RectF(0f, 0f, DEFAULT_CUT_VIEW_WIDTH, DEFAULT_CUT_VIEW_HEIGHT)
    private val mCutLeft get() = mCutRect.left
    private val mCutTop get() = mCutRect.top
    private val mCutRight get() = mCutRect.right
    private val mCutBottom get() = mCutRect.bottom
    private val mCutWidth get() = mCutRect.width()
    private val mCutHeight get() = mCutRect.height()
    private val mPosition = IntArray(2)
    private val mPaint = Paint().also {
        it.style = Paint.Style.FILL_AND_STROKE
    }
    private val mCornerRect = RectF()
    private val mDownPoint = PointF()
    private var mAction = Action.None
    private var mRatio = 1f
    private var mIsDirty = false

    // public set param
    private var mMinWidth = 200
    private var mMinHeight = 200
    private var mCornerSize = 100f
        set(value) {
            field = value
            mMinWidth = (2 * field).roundToInt()
            mMinHeight = (2 * field).roundToInt()
        }
    private var mCornerWidth = 10f
    private var mIsRatioMode = false
    private var mCutRatio = 1f
        set(value) {
            field = value
            mIsDirty = true
            requestLayout()
        }

    fun getCutBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            mCutWidth.roundToInt(),
            mCutHeight.roundToInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.save()
        canvas.translate(-mCutLeft, -mCutTop)
        draw(canvas)
        canvas.restore()
        return bitmap
    }

    override fun setImageDrawable(drawable: Drawable?) {
        drawable?.let {
            mIsDirty = true
            mRatio = it.intrinsicWidth / it.intrinsicHeight.toFloat()
        }
        super.setImageDrawable(drawable)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val h = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        val imgWidth: Int
        val imgHeight: Int
        if (w / mRatio > h) {
            imgHeight = h
            imgWidth = (h * mRatio).roundToInt()
        } else {
            imgWidth = w
            imgHeight = (w / mRatio).roundToInt()
        }
        if (mIsDirty) {
            if (mIsRatioMode) {
                val cw: Int
                val ch: Int
                if (imgWidth / mCutRatio > imgHeight) {
                    ch = imgHeight
                    cw = (imgHeight * mCutRatio).roundToInt()
                } else {
                    cw = imgWidth
                    ch = (imgWidth / mCutRatio).roundToInt()
                }
                val l = (imgWidth - cw) / 2f + paddingLeft
                val t = (imgHeight - ch) / 2f + paddingTop
                mCutRect.set(l, t, l + cw, t + ch)
            } else {
                mCutRect.set(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    paddingLeft.toFloat() + imgWidth,
                    paddingTop.toFloat() + imgHeight
                )
            }
            mIsDirty = false
        }
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(imgWidth + paddingLeft + paddingRight, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(imgHeight + paddingTop + paddingBottom, MeasureSpec.EXACTLY)
        )
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        val left = (r - l - measuredWidth) / 2 + l
        val top = (b - t - measuredHeight) / 2 + t
        super.layout(left, top, left + measuredWidth, top + measuredHeight)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            getLocationOnScreen(mPosition)
            it.setLocation(
                (it.rawX - mPosition[0]) / scaleX,
                (it.rawY - mPosition[1]) / scaleY
            )
            dispatchEvent(it)
        }
        return true
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let {
            val id = it.saveLayer(null, null)
            it.drawRect(
                0f,
                0f,
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                mPaint.also { paint ->
                    paint.xfermode = null
                    paint.color = MASK_COLOR
                })
            mPaint.let { paint ->
                paint.xfermode = null
                paint.color = Color.WHITE
            }
            //
            val realSize = mCornerSize / scaleX
            val realWidth = mCornerWidth / scaleX
            mCornerRect.set(0f, 0f, realSize, realSize)
            // lt
            mCornerRect.offsetTo(
                mCutLeft - realWidth,
                mCutTop - realWidth
            )
            it.drawRect(mCornerRect, mPaint)
            // rt
            mCornerRect.offsetTo(
                mCutRight + realWidth - realSize,
                mCutTop - realWidth
            )
            it.drawRect(mCornerRect, mPaint)
            // lb
            mCornerRect.offsetTo(
                mCutLeft - realWidth,
                mCutBottom + realWidth - realSize
            )
            it.drawRect(mCornerRect, mPaint)
            // rb
            mCornerRect.offsetTo(
                mCutRight + realWidth - realSize,
                mCutBottom + realWidth - realSize
            )
            it.drawRect(mCornerRect, mPaint)

            it.drawRect(mCutRect, mPaint.also { paint ->
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                paint.color = 0
            })
            it.restoreToCount(id)
        }
    }

    private fun dispatchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mDownPoint.set(event.x, event.y)
                mAction = when {
                    getDistance(
                        mCutRight,
                        mCutBottom,
                        mDownPoint.x,
                        mDownPoint.y
                    ) <= mCornerSize -> Action.PointRB
                    getDistance(
                        mCutLeft,
                        mCutBottom,
                        mDownPoint.x,
                        mDownPoint.y
                    ) <= mCornerSize -> Action.PointLB
                    getDistance(
                        mCutRight,
                        mCutTop,
                        mDownPoint.x,
                        mDownPoint.y
                    ) <= mCornerSize -> Action.PointRT
                    getDistance(
                        mCutLeft,
                        mCutTop,
                        mDownPoint.x,
                        mDownPoint.y
                    ) <= mCornerSize -> Action.PointLT
                    else -> Action.None
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount != 1) return
                val dx = event.x - mDownPoint.x
                val dy = event.y - mDownPoint.y
                when (mAction) {
                    Action.None -> mAction = Action.Move
                    Action.Move -> dispatchCutAreaMove(dx, dy)
                    else -> dispatchActionMove(dx, dy)
                }
                mDownPoint.set(event.x, event.y)
                postInvalidate()
            }
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                mDownPoint.set(event.x, event.y)
                mAction = Action.None
            }
        }
    }

    private fun dispatchCutAreaMove(dx: Float, dy: Float) {
        var realDx = dx
        var realDy = dy
        if (mCutLeft < paddingLeft && mCutRight > measuredWidth - paddingRight) {
            realDx = 0f
        }
        if (mCutTop < paddingTop && mCutBottom > measuredHeight - paddingBottom) {
            realDy = 0f
        }
        if (mCutLeft + dx < paddingLeft) {
            realDx = -mCutLeft + paddingLeft
        }
        if (mCutRight + dx > measuredWidth - paddingRight) {
            realDx = measuredWidth - mCutRight - paddingRight
        }
        if (mCutTop + dy < paddingTop) {
            realDy = -mCutTop + paddingTop
        }
        if (mCutBottom + dy > measuredHeight - paddingBottom) {
            realDy = measuredHeight - mCutBottom - paddingBottom
        }
        mCutRect.offset(realDx, realDy)
    }

    /**
     * todo: 优化边界判断逻辑
     */
    private fun dispatchActionMove(dx: Float, dy: Float) {
        var realDx = dx
        var realDy = dy
        when (mAction) {
            Action.PointLT -> {
                if (mCutLeft + dx < paddingLeft) {
                    realDx = -mCutLeft + paddingLeft
                }
                if (mCutLeft + dx > mCutRight - mMinWidth) {
                    realDx = mCutRight - mMinWidth - mCutLeft
                }
                if (mCutTop + dy < paddingTop) {
                    realDy = -mCutTop + paddingTop
                }
                if (mCutTop + dy > mCutBottom - mMinHeight) {
                    realDy = mCutBottom - mMinHeight - mCutTop
                }
                if (mIsRatioMode) {
                    if (realDx == 0f || realDy == 0f) {
                        realDx = 0f
                        realDy = 0f
                    }
                    val sx: Float
                    val sy: Float
                    if (abs(realDx / mCutRatio) > abs(realDy)) {
                        sy = realDy
                        sx = realDy * mCutRatio
                    } else {
                        sx = realDx
                        sy = realDx / mCutRatio
                    }
                    if (mCutRatio > mRatio) {
                        when {
                            mCutLeft + sx < paddingLeft -> {
                                realDx = -mCutLeft + paddingLeft
                                realDy = realDx / mCutRatio
                            }
                            mCutLeft + sx > mCutRight - mMinWidth -> {
                                realDx = mCutRight - mMinWidth - mCutLeft
                                realDy = realDx / mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    } else {
                        when {
                            mCutTop + sy < paddingTop -> {
                                realDy = -mCutTop + paddingTop
                                realDx = realDy * mCutRatio
                            }
                            mCutTop + sy > mCutBottom - mMinHeight -> {
                                realDy = mCutBottom - mMinHeight - mCutTop
                                realDx = realDy * mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    }
                }
                mCutRect.left += realDx
                mCutRect.top += realDy
            }
            Action.PointRT -> {
                if (mCutRight + dx > measuredWidth - paddingRight) {
                    realDx = measuredWidth - mCutRight - paddingRight
                }
                if (mCutRight + dx < mCutLeft + mMinWidth) {
                    realDx = mCutLeft + mMinWidth - mCutRight
                }
                if (mCutTop + dy < paddingTop) {
                    realDy = -mCutTop + paddingTop
                }
                if (mCutTop + dy > mCutBottom - mMinHeight) {
                    realDy = mCutBottom - mMinHeight - mCutTop
                }
                if (mIsRatioMode) {
                    if (realDx == 0f || realDy == 0f) {
                        realDx = 0f
                        realDy = 0f
                    }
                    val sx: Float
                    val sy: Float
                    if (abs(realDx / mCutRatio) > abs(realDy)) {
                        sy = realDy
                        sx = -realDy * mCutRatio
                    } else {
                        sx = realDx
                        sy = -realDx / mCutRatio
                    }
                    if (mCutRatio > mRatio) {
                        when {
                            mCutRight + sx > measuredWidth - paddingRight -> {
                                realDx = measuredWidth - mCutRight - paddingRight
                                realDy = realDx / mCutRatio
                            }
                            mCutRight + sx < mCutLeft + mMinWidth -> {
                                realDx = mCutLeft + mMinWidth - mCutRight
                                realDy = realDx / mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    } else {
                        when {
                            mCutTop + sy < paddingTop -> {
                                realDy = -mCutTop + paddingTop
                                realDx = realDy * mCutRatio
                            }
                            mCutTop + sy > mCutBottom - mMinHeight -> {
                                realDy = mCutBottom - mMinHeight - mCutTop
                                realDx = realDy * mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    }
                }
                mCutRect.right += realDx
                mCutRect.top += realDy
            }
            Action.PointLB -> {
                if (mCutLeft + dx < paddingLeft) {
                    realDx = -mCutLeft + paddingLeft
                }
                if (mCutLeft + dx > mCutRight - mMinWidth) {
                    realDx = mCutRight - mMinWidth - mCutLeft
                }
                if (mCutBottom + dy > measuredHeight - paddingBottom) {
                    realDy = measuredHeight - mCutBottom - paddingBottom
                }
                if (mCutBottom + dy < mCutTop + mMinHeight) {
                    realDy = mCutTop + mMinHeight - mCutBottom
                }
                if (mIsRatioMode) {
                    if (realDx == 0f || realDy == 0f) {
                        realDx = 0f
                        realDy = 0f
                    }
                    val sx: Float
                    val sy: Float
                    if (abs(realDx / mCutRatio) > abs(realDy)) {
                        sy = realDy
                        sx = -realDy * mCutRatio
                    } else {
                        sx = realDx
                        sy = -realDx / mCutRatio
                    }
                    if (mCutRatio > mRatio) {
                        when {
                            mCutLeft + sx < paddingLeft -> {
                                realDx = -mCutLeft + paddingLeft
                                realDy = realDx / mCutRatio
                            }
                            mCutLeft + sx > mCutRight - mMinWidth -> {
                                realDx = mCutRight - mMinWidth - mCutLeft
                                realDy = realDx / mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    } else {
                        when {
                            mCutBottom + sy > measuredHeight - paddingBottom -> {
                                realDy = measuredHeight - mCutBottom - paddingBottom
                                realDx = realDy * mCutRatio
                            }
                            mCutBottom + sy < mCutTop + mMinHeight -> {
                                realDy = mCutTop + mMinHeight - mCutBottom
                                realDx = realDy * mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    }
                }
                mCutRect.left += realDx
                mCutRect.bottom += realDy
            }
            Action.PointRB -> {
                if (mCutRight + dx > measuredWidth - paddingRight) {
                    realDx = measuredWidth - mCutRight - paddingRight
                }
                if (mCutRight + dx < mCutLeft + mMinWidth) {
                    realDx = mCutLeft + mMinWidth - mCutRight
                }
                if (mCutBottom + dy > measuredHeight - paddingBottom) {
                    realDy = measuredHeight - mCutBottom - paddingBottom
                }
                if (mCutBottom + dy < mCutTop + mMinHeight) {
                    realDy = mCutTop + mMinHeight - mCutBottom
                }
                if (mIsRatioMode) {
                    if (realDx == 0f || realDy == 0f) {
                        realDx = 0f
                        realDy = 0f
                    }
                    val sx: Float
                    val sy: Float
                    if (abs(realDx / mCutRatio) > abs(realDy)) {
                        sy = realDy
                        sx = realDy * mCutRatio
                    } else {
                        sx = realDx
                        sy = realDx / mCutRatio
                    }
                    if (mCutRatio > mRatio) {
                        when {
                            mCutRight + sx > measuredWidth - paddingRight -> {
                                realDx = measuredWidth - mCutRight - paddingRight
                                realDy = realDx / mCutRatio
                            }
                            mCutRight + sx < mCutLeft + mMinWidth -> {
                                realDx = mCutLeft + mMinWidth - mCutRight
                                realDy = realDx / mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    } else {
                        when {
                            mCutBottom + sy > measuredHeight - paddingBottom -> {
                                realDy = measuredHeight - mCutBottom - paddingBottom
                                realDx = realDy * mCutRatio
                            }
                            mCutBottom + sy < mCutTop + mMinHeight -> {
                                realDy = mCutTop + mMinHeight - mCutBottom
                                realDx = realDy * mCutRatio
                            }
                            else -> {
                                realDx = sx
                                realDy = sy
                            }
                        }
                    }
                }
                mCutRect.right += realDx
                mCutRect.bottom += realDy
            }
            else -> {
            }
        }
    }

    private fun getDistance(startX: Float, startY: Float, endX: Float, endY: Float) =
        sqrt((startX - endX).pow(2) + (startY - endY).pow(2))

    private fun getDistance(startPoint: PointF, endPoint: PointF) =
        sqrt((startPoint.x - endPoint.x).pow(2) + (startPoint.y - endPoint.y).pow(2))

    /**
     * 设置边角框线长度（单位px）
     */
    var cornerSize
        get() = mCornerSize
        set(value) {
            mCornerSize = value
        }

    /**
     * 设置边角框线粗细（单位px）
     */
    var cornerWidth
        get() = mCornerWidth
        set(value) {
            mCornerWidth = value
        }

    /**
     * 设置是否为等比例缩放模式
     */
    var isRatioMode
        get() = mIsRatioMode
        set(value) {
            mIsRatioMode = value
        }

    /**
     * 设置裁剪比例值：宽度/高度（仅等比例缩放模式下可用）
     */
    var cutRatio
        get() = mCutRatio
        set(value) {
            mCutRatio = value
        }

    companion object {
        private const val MASK_COLOR = 0x99000000.toInt()
        private const val DEFAULT_CUT_VIEW_WIDTH = 300f
        private const val DEFAULT_CUT_VIEW_HEIGHT = 300f
    }

    private enum class Action {
        None, Move, PointLT, PointLB, PointRT, PointRB
    }
}