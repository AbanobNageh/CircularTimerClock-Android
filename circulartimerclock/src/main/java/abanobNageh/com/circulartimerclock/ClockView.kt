package abanobNageh.com.circulartimerclock;

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attrs, defStyleAttr) {
    private var padding = 0
    private var radius = 0
    private var paint: Paint? = null
    private var isInit = false
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()
    var cX = x + width / 2.toDouble()
    var cY = y + height / 2.toDouble()
    private var tickColor = 0
    private var hourColor = 0
    private var fontSize = 0
    private var tickInterval = 0

    private fun initProperties(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0)
        val tickColor = a.getColor(R.styleable.CircularSlider_clock_tick_color, ContextCompat.getColor(this.context, R.color.dialer_clock))
        val tickInterval = a.getInteger(R.styleable.CircularSlider_clock_tick_interval, 5)
        val hourColor = a.getColor(R.styleable.CircularSlider_clock_hour_color, ContextCompat.getColor(this.context, R.color.dialer_clock))
        val fontSize = a.getDimensionPixelSize(R.styleable.CircularSlider_hours_size, 10)
        this.tickInterval = tickInterval
        this.tickColor = tickColor
        this.hourColor = hourColor
        this.fontSize = fontSize

        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                cX = x + width / 2.toDouble()
                cY = y + height / 2.toDouble()
                isInit = false
                this@ClockView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        setWillNotDraw(false)
        a.recycle()
    }

    private fun initClock() {
        cX = width / 2.toDouble()
        cY = height / 2.toDouble()
        val numeralSpacing = 0
        padding = numeralSpacing + 60
        val min = height.coerceAtMost(width)
        radius = min / 2 - padding
        paint = Paint()
        isInit = true
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInit) {
            initClock()
        }
        canvas.drawColor(ContextCompat.getColor(this.context, R.color.transparent))
        drawNumeral(canvas)
        drawTickMarks(canvas)
        super.onDraw(canvas)
    }

    private fun drawTickMarks(canvas: Canvas) {
        paint!!.reset()
        paint!!.color = tickColor
        paint!!.strokeWidth = 5f
        paint!!.style = Paint.Style.STROKE
        paint!!.isAntiAlias = true

        var angleIndex: Double = 0.0
        while (angleIndex < 360.0) {
            val angle = Math.toRadians(angleIndex).toFloat() // Need to convert to radians first

            val startX = (cX + (radius + padding) * sin(angle.toDouble())).toFloat()
            val startY = (cY - (radius + padding) * cos(angle.toDouble())).toFloat()
            var pad = 35
            if (angleIndex % 30 != 0.0) {
                pad = 50
            }

            val stopX = (cX + (radius + pad) * sin(angle.toDouble())).toFloat()
            val stopY = (cY - (radius + pad) * cos(angle.toDouble())).toFloat()

            canvas.drawLine(startX, startY, stopX, stopY, paint!!)
            angleIndex += (0.5 * tickInterval)
        }
    }

    private fun drawNumeral(canvas: Canvas) {
        paint!!.reset()
        paint!!.strokeWidth = 2f
        paint!!.isAntiAlias = true
        paint!!.color = hourColor
        paint!!.textSize = fontSize.toFloat()

        for (number in numbers) {
            val tmp = number.toString()
            paint!!.getTextBounds(tmp, 0, tmp.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (width / 2 + cos(angle) * radius - rect.width() / 2).toInt()
            val y = (height / 2 + sin(angle) * radius + rect.height() / 2).toInt()
            canvas.drawText(tmp, x.toFloat(), y.toFloat(), paint!!)
        }
    }

    fun setTickColor(tickColor: Int) {
        this.tickColor = tickColor
    }

    fun setHourColor(hourColor: Int) {
        this.hourColor = hourColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setFontSize(fontSize: Int) {
        this.fontSize = fontSize
    }

    fun setTickInterval(tickInterval: Int) {
        this.tickInterval = tickInterval
    }

    init {
        initProperties(context, attrs!!, defStyleAttr)
    }
}
