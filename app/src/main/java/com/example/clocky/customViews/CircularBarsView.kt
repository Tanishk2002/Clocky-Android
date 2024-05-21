package com.example.clocky.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.example.clocky.R
import kotlin.math.cos
import kotlin.math.sin

class CircularBarsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object{
        private const val DEFAULT_BARS_COLOR = Color.BLUE
        private const val DEFAULT_ANIMATION_BAR_COLOR = Color.RED
        private const val DEFAULT_DISABLED_BARS_COLOR = Color.GRAY
        private const val DEFAULT_BARS_COUNT = 30
        private const val DEFAULT_BAR_LENGTH = 50f
        private const val DEFAULT_BARS_ROTATION_SPEED = 5
        private const val DEFAULT_STROKE_WIDTH = 10f
        private const val DEFAULT_CIRCULAR_BARS_DIR = -1
        private const val DEFAULT_ANIMATE_BARS = false
    }

    private var barsColor = DEFAULT_BARS_COLOR
    private var disabledBarsColor = DEFAULT_DISABLED_BARS_COLOR
    private var animationBarColor = DEFAULT_ANIMATION_BAR_COLOR
    private var barsCount = DEFAULT_BARS_COUNT
    private var barLength = DEFAULT_BAR_LENGTH
    private var barsRotationSpeed = DEFAULT_BARS_ROTATION_SPEED
    private var strokeWidth = DEFAULT_STROKE_WIDTH
    private var circularBarsDir = DEFAULT_CIRCULAR_BARS_DIR
    private var animateBars = DEFAULT_ANIMATE_BARS

    private var rotationAngle = 0

    private var size = 300

    private val paint = Paint()

    private var radius = size / 2

    private var index = 0

    init {
        paint.isAntiAlias = true
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet)
    {
        val typedArr = context.theme.obtainStyledAttributes(attrs, R.styleable.CircularBarsView, 0, 0)

        barsColor = typedArr.getColor(R.styleable.CircularBarsView_barsColor, DEFAULT_BARS_COLOR)
        animationBarColor = typedArr.getColor(R.styleable.CircularBarsView_animationBarColor, DEFAULT_ANIMATION_BAR_COLOR)
        disabledBarsColor = typedArr.getColor(R.styleable.CircularBarsView_disabledBarsColor, DEFAULT_DISABLED_BARS_COLOR)
        barsCount = typedArr.getInt(R.styleable.CircularBarsView_barsCount, DEFAULT_BARS_COUNT)
        barLength = typedArr.getDimension(R.styleable.CircularBarsView_barsLength, DEFAULT_BAR_LENGTH)
        circularBarsDir = typedArr.getInt(R.styleable.CircularBarsView_circularBarsDir, DEFAULT_CIRCULAR_BARS_DIR)
        barsRotationSpeed = typedArr.getInt(R.styleable.CircularBarsView_barsRotationSpeed, DEFAULT_BARS_ROTATION_SPEED)
        strokeWidth = typedArr.getDimension(R.styleable.CircularBarsView_strokeWidth, DEFAULT_STROKE_WIDTH)
        animateBars = typedArr.getBoolean(R.styleable.CircularBarsView_animateBars, DEFAULT_ANIMATE_BARS)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)

        radius = (size / 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(isAnimating) {
            paint.color = barsColor
            paint.strokeWidth = strokeWidth
            paint.style = Paint.Style.FILL

            val centerX = width / 2f
            val centerY = height / 2f
            val angleStep = 360f / barsCount

            for (i in 0 until barsCount) {
                val angle = Math.toRadians(((i * angleStep) + rotationAngle).toDouble())
                val startX = centerX + (radius - barLength) * cos(angle).toFloat()
                val startY = centerY + (radius - barLength) * sin(angle).toFloat()
                val endX = centerX + radius * cos(angle).toFloat()
                val endY = centerY + radius * sin(angle).toFloat()

                if (animateBars && i <= index)
                    paint.color = animationBarColor
                else
                    paint.color = barsColor
                canvas.drawLine(startX, startY, endX, endY, paint)
            }

            if(animateBars)
                index = (index + 1) % barsCount

            rotationAngle += (barsRotationSpeed * circularBarsDir)
        }

        //draw the defaults
        else
        {
            paint.color = disabledBarsColor
            paint.strokeWidth = strokeWidth
            paint.style = Paint.Style.FILL

            val centerX = width / 2f
            val centerY = height / 2f
            val angleStep = 360f / barsCount

            for (i in 0 until barsCount) {
                val angle = Math.toRadians(((i * angleStep)).toDouble())
                val startX = centerX + (radius - barLength) * cos(angle).toFloat()
                val startY = centerY + (radius - barLength) * sin(angle).toFloat()
                val endX = centerX + radius * cos(angle).toFloat()
                val endY = centerY + radius * sin(angle).toFloat()

                canvas.drawLine(startX, startY, endX, endY, paint)
            }
        }
    }



    //-----FOR ANIMATION------

    private var isAnimating = false
    private val handler = Handler(Looper.getMainLooper())
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (isAnimating) {
                // Call the function to update the UI and invalidate the view
                invalidate()
                // Schedule the next animation frame
                handler.postDelayed(this, 50)
            }
        }
    }

    fun startAnimation()
    {
        if(!isAnimating)
        {
            isAnimating = true
            handler.post(animationRunnable)
        }
    }

    fun stopAnimation()
    {
        isAnimating = false
        handler.removeCallbacks(animationRunnable)
        invalidate()
    }
}