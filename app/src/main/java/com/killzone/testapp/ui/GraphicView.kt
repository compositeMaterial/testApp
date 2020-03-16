package com.killzone.testapp.ui;

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.ViewCompat
import com.killzone.testapp.network.Coordinates
import kotlin.math.abs

class GraphicView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    deffStyleAttr: Int = 0
) : View(context, attrs, deffStyleAttr) {


    private lateinit var points: Array<PointF>
    private var size = ""
    private var command = false

    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 20.0f
        color = Color.BLACK
    }


    private val backgroudColor = Color.WHITE

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (::bitmap.isInitialized) bitmap.recycle()

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(backgroudColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            save()
            scale(mScaleFactor, mScaleFactor)

            canvas.drawBitmap(bitmap, 0f, 0f, null)

            canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, paint)
            canvas.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), paint)

            if (command == true) {
                drawChart(canvas)
            }

            restore()

        }

    }


    fun drawChart(canvas: Canvas) {

        canvas.save()
        canvas.translate(width / 2f, height / 2f)

        val x = width / 2.toFloat()
        val y = height / 2.toFloat()

        var k = 150f
        val r = 5f

        for (i in 1 until 13) {

            if (i < 4) {
                canvas.drawCircle(-k * i, 0f, r, paint)
                canvas.drawText((i * (-10)).toString(), -k * i, -10f, paint)

            } else if (i > 3 && i < 7) {
                canvas.drawCircle(k * (i - 3), 0f, r, paint)
                canvas.drawText(((i - 3) * 10).toString(), k * (i - 3), -10f, paint)

            } else if (i > 6 && i < 10) {
                canvas.drawCircle(0f, -k * (i - 6), r, paint)
                canvas.drawText(((i - 6) * (10)).toString(), 30f, -k * (i - 6), paint)

            } else if (i > 9 && i < 13) {
                canvas.drawCircle(0f, k * (i - 9), r, paint)
                canvas.drawText(((i - 9) * 10).toString(), 30f, k * (i - 9), paint)
            }
        }

        k = 15f


        for (i in points.indices) {
            if (i == 0) {
                canvas.drawCircle((points[i].x) * k, ((points[i].y * k) * (-1)), r, paint)
            } else {
                canvas.drawCircle((points[i].x) * k, ((points[i].y * k) * (-1)), r, paint)
                canvas.drawLine(
                    (points[i - 1].x) * k, ((points[i - 1].y * k) * (-1)),
                    (points[i].x) * k, ((points[i].y * k) * (-1)), paint
                )
            }
        }

        canvas.restore()
    }


    fun setPoints(c: Array<PointF>?) {
        points = c ?: return
        size = points.size.toString()
        command = true
        invalidate()
    }

    private var mScaleFactor = 1f

    private val AXIS_X_MIN = -1f
    private val AXIS_X_MAX = -1f
    private val AXIS_Y_MIN = -1f
    private val AXIS_Y_MAX = -1f

   /* private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mGestureDetector: GestureDetector

    private val mCurrentViewport = RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX)
    private val mContentRect: Rect? = null*/

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))

            invalidate()
            return true
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev)
        return true
    }



    /*private fun hitTest(x: Float, y: Float, dest: PointF): Boolean {
        if (!mContentRect?.contains(x.toInt(), y.toInt())!!) {
            return false
        }

        dest.set(mCurrentViewport.left + mCurrentViewport.width()
                * (x - mContentRect.left) / mContentRect.width(),
            mCurrentViewport.top + mCurrentViewport.height()
                    * (y - mContentRect.bottom) / -mContentRect.height()
        )
        return true
    }




    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mScaleGestureDetector.onTouchEvent(event)
                || mGestureDetector.onTouchEvent(event)
                || super.onTouchEvent(event)
    }


    private val mScaleGestureListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            private val viewportFocus = PointF()
            private var lastSpanX: Float = 0f
            private var lastSpanY: Float = 0f

            // Detects that new pointers are going down.
            override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
                lastSpanX = scaleGestureDetector.currentSpanX
                lastSpanY = scaleGestureDetector.currentSpanY
                return true
            }

            override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
                val spanX: Float = scaleGestureDetector.currentSpanX
                val spanY: Float = scaleGestureDetector.currentSpanY

                val newWidth: Float = lastSpanX / spanX * mCurrentViewport.width()
                val newHeight: Float = lastSpanY / spanY * mCurrentViewport.height()

                val focusX: Float = scaleGestureDetector.focusX
                val focusY: Float = scaleGestureDetector.focusY
                // Makes sure that the chart point is within the chart region.
                // See the sample for the implementation of hitTest().
                hitTest(focusX, focusY, viewportFocus)

                mContentRect?.apply {
                    mCurrentViewport.set(
                        viewportFocus.x - newWidth * (focusX - left) / width(),
                        viewportFocus.y - newHeight * (bottom - focusY) / height(),
                        0f,
                        0f
                    )
                }
                mCurrentViewport.right = mCurrentViewport.left + newWidth
                mCurrentViewport.bottom = mCurrentViewport.top + newHeight
                // Invalidates the View to update the display.
                ViewCompat.postInvalidateOnAnimation(this@GraphicView)

                lastSpanX = spanX
                lastSpanY = spanY
                return true
            }
        }*/

}

