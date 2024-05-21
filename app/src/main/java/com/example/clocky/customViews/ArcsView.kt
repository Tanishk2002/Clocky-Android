package com.example.clocky.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.example.clocky.R

class ArcsView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object{
        private const val DEFAULT_ARC_COLOR1 = Color.RED
        private const val DEFAULT_ARC_COLOR2 = Color.RED
        private const val DEFAULT_DISABLED_ARC_COLOR = Color.GRAY
        private const val DEFAULT_STROKE_WIDTH1 = 10f
        private const val DEFAULT_STROKE_WIDTH2 = 10f

        private const val DEFAULT_arcDir = 1

        private const val DEFAULT_SPEED = 2

        private const val DEFAULT_START_ANGLE1 = 0f
        private const val DEFAULT_START_ANGLE2 = 180f
        private const val DEFAULT_SWEEP_ANGLE1 = 100f
        private const val DEFAULT_SWEEP_ANGLE2 = 100f
    }

    private var arcColor1 = DEFAULT_ARC_COLOR1
    private var arcColor2 = DEFAULT_ARC_COLOR2
    private var disabledArcColor = DEFAULT_DISABLED_ARC_COLOR
    private var strokeWidth1 = DEFAULT_STROKE_WIDTH1
    private var strokeWidth2 = DEFAULT_STROKE_WIDTH2

    private var arcDir = DEFAULT_arcDir

    private var speed = DEFAULT_SPEED

    private var startAngle1 = DEFAULT_START_ANGLE1
    private var startAngle2 = DEFAULT_START_ANGLE2
    private var sweepAngle1 = DEFAULT_SWEEP_ANGLE1
    private var sweepAngle2 = DEFAULT_SWEEP_ANGLE2

    private var size = 0

    private val paint = Paint()

    private lateinit var rect1 : RectF

    init {
        paint.isAntiAlias = true
        setAttributes(attrs)
    }

    private fun setAttributes(attrs: AttributeSet)
    {
        val typedArr = context.theme.obtainStyledAttributes(attrs, R.styleable.ArcsView, 0, 0)

        arcColor1 = typedArr.getColor(R.styleable.ArcsView_arcColor1, DEFAULT_ARC_COLOR1)
        arcColor2 = typedArr.getColor(R.styleable.ArcsView_arcColor2, DEFAULT_ARC_COLOR2)
        disabledArcColor = typedArr.getColor(R.styleable.ArcsView_disabledArcColor, DEFAULT_DISABLED_ARC_COLOR)
        strokeWidth1 = typedArr.getDimension(R.styleable.ArcsView_strokeWidth1, DEFAULT_STROKE_WIDTH1)
        strokeWidth2 = typedArr.getDimension(R.styleable.ArcsView_strokeWidth2, DEFAULT_STROKE_WIDTH2)
        arcDir = typedArr.getInt(R.styleable.ArcsView_arcDir, DEFAULT_arcDir)
        speed = typedArr.getInt(R.styleable.ArcsView_speed, DEFAULT_SPEED)
        startAngle1 = typedArr.getInt(R.styleable.ArcsView_startAngle1, DEFAULT_START_ANGLE1.toInt()).toFloat()
        startAngle2 = typedArr.getInt(R.styleable.ArcsView_startAngle2, DEFAULT_START_ANGLE2.toInt()).toFloat()
        sweepAngle1 = typedArr.getInt(R.styleable.ArcsView_sweepAngle1, DEFAULT_SWEEP_ANGLE1.toInt()).toFloat()
        sweepAngle2 = typedArr.getInt(R.styleable.ArcsView_sweepAngle2, DEFAULT_SWEEP_ANGLE2.toInt()).toFloat()
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)

        allocate()
    }

    private fun allocate(){
        rect1 = RectF(
            size - size * 0.95f,
            size - size * 0.95f,
            size - size * 0.05f,
            size - size * 0.05f
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(isAnimating) {
            //arc1
            paint.color = arcColor1
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth1

            canvas.drawArc(rect1, startAngle1, sweepAngle1, false, paint)

            //arc2
            paint.color = arcColor2
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth2
            canvas.drawArc(rect1, startAngle2, sweepAngle2, false, paint)

            startAngle1 = (startAngle1 + (speed * arcDir)) % 360
            startAngle2 = (startAngle2 + (speed * arcDir)) % 360
        }

        //draw the defaults
        else
        {
            //arc1
            paint.color = disabledArcColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth1

            canvas.drawArc(rect1, startAngle1, sweepAngle1, false, paint)

            //arc2
            paint.color = disabledArcColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth2
            canvas.drawArc(rect1, startAngle2, sweepAngle2, false, paint)
        }

//        //incase want to add an inner arc, specify a property padding
//        paint.color = arcColor2
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = strokeWidth2
//
//        val rect2 = RectF(size - size * 0.95f + padding,size - size * 0.95f + padding,size - size * 0.5f - padding,size - size * 0.5f - padding)
//        canvas.drawArc(rect2, startAngle11, sweepAngle11, false, paint)
//        canvas.drawArc(rect2, startAngle22, sweepAngle22, false, paint)
//
//        startAngle11 = (startAngle11 + step2) % 360
//        startAngle22 = (startAngle22 + step2) % 360
    }


    //-----FOR ANIMATION------

    private var isAnimating = false
    private val animationDelay = 10L // Animation delay in milliseconds
    private val handler = Handler(Looper.getMainLooper())
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (isAnimating) {
                // Call the function to update the UI and invalidate the view
                invalidate()
                // Schedule the next animation frame
                handler.postDelayed(this, animationDelay)
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