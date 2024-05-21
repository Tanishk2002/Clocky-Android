package com.example.clocky.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.util.Calendar
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.example.clocky.R

class AnalogClockView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object{
        private const val DEFAULT_HOUR_HAND_COLOR = Color.GRAY
        private const val DEFAULT_MINUTE_HAND_COLOR = Color.GRAY
        private const val DEFAULT_SECOND_HAND_COLOR = Color.GRAY
        private const val DEFAULT_RING_COLOR = Color.GRAY
        private const val DEFAULT_CLOCK_BACKGROUND_COLOR = Color.DKGRAY
        private const val DEFAULT_CENTER_CIRCLE_COLOR = Color.LTGRAY

        private const val DEFAULT_HOUR_HAND_WIDTH = 10f
        private const val DEFAULT_MINUTE_HAND_WIDTH = 10f
        private const val DEFAULT_SECOND_HAND_WIDTH = 5f
        private const val DEFAULT_RING_WIDTH = 10f
        private const val DEFAULT_CENTER_CIRCLE_SIZE = 30f

        private const val DEFAULT_DISABLE_SECOND_HAND = false
    }

    private var hourHandColor = Color.GRAY
    private var minuteHandColor = Color.GRAY
    private var secondHandColor = Color.GRAY
    private var ringColor = Color.GRAY
    private var clockBackgroundColor = Color.DKGRAY
    private var centerCircleColor = Color.LTGRAY

    private var hourHandWidth = 10f
    private var minuteHandWidth = 10f
    private var secondHandWidth = 5f
    private var ringWidth = 10f
    private var centerCircleSize = 30f

    private var disableSecondHand = false

    private var size = 500

    private var radius = size / 2f

    private val paint = Paint()

    //not attributes
    private var hourHandLength = 0f
    private var minuteHandLength = 0f
    private var secondHandLength = 0f

    init {
        paint.isAntiAlias = true
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet)
    {
        val typedArr = context.theme.obtainStyledAttributes(attrs, R.styleable.AnalogClockView, 0, 0)

        hourHandColor = typedArr.getColor(R.styleable.AnalogClockView_hourHandColor, DEFAULT_HOUR_HAND_COLOR)
        minuteHandColor = typedArr.getColor(R.styleable.AnalogClockView_minuteHandColor, DEFAULT_MINUTE_HAND_COLOR)
        secondHandColor = typedArr.getColor(R.styleable.AnalogClockView_secondHandColor, DEFAULT_SECOND_HAND_COLOR)
        ringColor = typedArr.getColor(R.styleable.AnalogClockView_ringColor, DEFAULT_RING_COLOR)
        clockBackgroundColor = typedArr.getColor(R.styleable.AnalogClockView_clockBackgroundColor, DEFAULT_CLOCK_BACKGROUND_COLOR)
        centerCircleColor = typedArr.getColor(R.styleable.AnalogClockView_centerCircleColor, DEFAULT_CENTER_CIRCLE_COLOR)
        hourHandWidth = typedArr.getDimension(R.styleable.AnalogClockView_hourHandWidth, DEFAULT_HOUR_HAND_WIDTH)
        minuteHandWidth = typedArr.getDimension(R.styleable.AnalogClockView_minuteHandWidth, DEFAULT_MINUTE_HAND_WIDTH)
        secondHandWidth = typedArr.getDimension(R.styleable.AnalogClockView_secondHandWidth, DEFAULT_SECOND_HAND_WIDTH)
        ringWidth = typedArr.getDimension(R.styleable.AnalogClockView_ringWidth, DEFAULT_RING_WIDTH)
        centerCircleSize = typedArr.getDimension(R.styleable.AnalogClockView_centerCircleSize, DEFAULT_CENTER_CIRCLE_SIZE)
        disableSecondHand = typedArr.getBoolean(R.styleable.AnalogClockView_disableSecondHand, DEFAULT_DISABLE_SECOND_HAND)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)

        radius = (size / 2f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        hourHandLength = radius - (radius * 0.45f) - ringWidth
        minuteHandLength = radius - (radius * 0.35f) - ringWidth
        secondHandLength = radius - (radius * 0.20f) - ringWidth

        // Get current time
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY) % 12 // Ensure 12-hour format
        val minute = currentTime.get(Calendar.MINUTE)
        val second = currentTime.get(Calendar.SECOND)

        //Draw background
        drawBackground(canvas)

        // Draw hour hand
        paint.color = hourHandColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = hourHandWidth

        val hourAngle = Math.toRadians((hour * 30 + minute / 2).toDouble())
        drawHand(canvas, hourAngle, hourHandLength)

        // Draw minute hand
        paint.color = minuteHandColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = minuteHandWidth

        val minuteAngle = Math.toRadians((minute * 6 + second / 10).toDouble())
        drawHand(canvas, minuteAngle, minuteHandLength)

        if(!disableSecondHand) {
            // Draw second hand
            paint.color = secondHandColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = secondHandWidth

            val secondAngle = Math.toRadians((second * 6).toDouble())
            drawHand(canvas, secondAngle, secondHandLength)
        }

        //draw centerCircle
        paint.color = centerCircleColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(radius, radius, (centerCircleSize / 2f), paint)

        if(!disableSecondHand) {
            Handler(Looper.getMainLooper()).postDelayed({
                invalidate()
            }, 1000)
        }
    }

    private fun drawBackground(canvas: Canvas)
    {
        var centerX = radius
        var centerY = radius

        //draw filled background
        paint.color = clockBackgroundColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius - (ringWidth / 2), paint)

        //draw ring
        paint.color = ringColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = ringWidth
        canvas.drawCircle(centerX, centerY, radius - ringWidth, paint)
    }

    private fun drawHand(canvas: Canvas, angle: Double, length: Float) {
        val centerX = radius
        val centerY = radius
        val handEndX = (centerX + Math.sin(angle) * length).toFloat()
        val handEndY = (centerY - Math.cos(angle) * length).toFloat()
        canvas.drawLine(centerX, centerY, handEndX, handEndY, paint)
    }
}