package abanobNageh.com.circulartimerclock

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class CircularSliderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var startGradientColor = 0
    private var endGradientColor = 0
    private var startHourAngle = 0
    private var startMinutesAngle = 0f
    private var endHourAngle = 0
    private var endMinutesAngle = 0f
    private var backgroundImage: Drawable? = null
    private var largestCenteredSquareLeft = 0
    private var largestCenteredSquareTop = 0
    private var largestCenteredSquareRight = 0
    private var largestCenteredSquareBottom = 0

    /**
     * Listener interface used to detect when slider moves around.
     */
    interface OnSliderRangeMovedListener {
        /**
         * This method is invoked when start thumb is moved, providing position of the start slider thumb.
         *
         * @param pos Value between 0 and 1 representing the current angle.<br></br>
         * `pos = (Angle - StartingAngle) / (2 * Pi)`
         */
        fun onStartSliderMoved(pos: Double, isThumbSlide: Boolean)

        /**
         * This method is invoked when end thumb is moved, providing position of the end slider thumb.
         *
         * @param pos Value between 0 and 1 representing the current angle.<br></br>
         * `pos = (Angle - StartingAngle) / (2 * Pi)`
         */
        fun onEndSliderMoved(pos: Double, isThumbSlide: Boolean)

        /**
         * This method is invoked when start slider is pressed/released.
         *
         * @param event Event represent state of the slider, it can be in two states: Pressed or Released.
         */
        fun onStartSliderEvent(event: ThumbEvent?)

        /**
         * This method is invoked when end slider is pressed/released.
         *
         * @param event Event represent state of the slider, it can be in two states: Pressed or Released.
         */
        fun onEndSliderEvent(event: ThumbEvent?)
    }

    private var mThumbStartX = 0
    private var mThumbStartY = 0
    private var mThumbEndX = 0
    private var mThumbEndY = 0
    private var mCircleCenterX = 0
    private var mCircleCenterY = 0
    private var mCircleRadius = 0
    private var mArcColor = 0
    private var mStartThumbImage: Drawable? = null
    private var mEndThumbImage: Drawable? = null
    private var mPadding = 0
    private var mStartThumbSize = 0
    private var mEndThumbSize = 0
    private var mStartThumbColor = 0
    private var mEndThumbColor = 0
    private var mBorderColor = 0
    private var mBorderThickness = 0
    private var mArcDashSize = 0
    private var mAngle = 0.0
    private var mAngleEnd = 0.0
    private var mIsThumbSelected = false
    private var mIsThumbEndSelected = false
    private val mPaint = Paint()
    private val mLinePaint = Paint()
    private val arcRectF = RectF()
    private val arcRect = Rect()
    private var mListener: OnSliderRangeMovedListener? = null
    private var linearGradient: LinearGradient? = null

    var isStartTimeAM = true
    var isEndTimeAM = true
    var prevSelectedStartAngle = 0f
    var currentSelectedStartAngle = 0f
    var prevSelectedEndAngle = 0f
    var currentSelectedEndAngle = 0f

    private var currentTouchAngle = 0.0
    private var prevTouchAngle = 0.0

    private enum class Thumb {
        START, END
    }

    // common initializer method
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0)
        // read all available attributes
        val startHour = a.getInteger(R.styleable.CircularSlider_start_hour, 0)
        val endHour = a.getInteger(R.styleable.CircularSlider_end_hour, 0)
        val startMinutes = a.getFloat(R.styleable.CircularSlider_start_minutes, 0f)
        val endMinutes = a.getFloat(R.styleable.CircularSlider_end_minutes, 0f)
        val thumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_thumb_size, 50)
        val startThumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_start_thumb_size, THUMB_SIZE_NOT_DEFINED)
        val endThumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_end_thumb_size, THUMB_SIZE_NOT_DEFINED)
        val thumbColor = a.getColor(R.styleable.CircularSlider_start_thumb_color, Color.GRAY)
        val thumbEndColor = a.getColor(R.styleable.CircularSlider_end_thumb_color, Color.GRAY)
        val borderThickness = a.getDimensionPixelSize(R.styleable.CircularSlider_border_thickness, 20)
        val arcDashSize = a.getDimensionPixelSize(R.styleable.CircularSlider_arc_dash_size, 60)
        val arcColor = a.getColor(R.styleable.CircularSlider_arc_color, 0)
        val startGradientColor = a.getColor(R.styleable.CircularSlider_arc_gradient_color_start, 0)
        val endGradientColor = a.getColor(R.styleable.CircularSlider_arc_gradient_color_end, 0)
        val borderColor = a.getColor(R.styleable.CircularSlider_border_color, Color.RED)
        val thumbImage = a.getDrawable(R.styleable.CircularSlider_start_thumb_image)
        val thumbEndImage = a.getDrawable(R.styleable.CircularSlider_end_thumb_image)
        val backgroundDrawable = a.getDrawable(R.styleable.CircularSlider_clock_background_image)

        this.startHourAngle = hourToHourAngle(startHour)
        this.startMinutesAngle = minutesToMinutesAngle(startMinutes)
        this.endHourAngle = hourToHourAngle(endHour)
        this.endMinutesAngle = minutesToMinutesAngle(endMinutes)
        this.borderThickness = borderThickness
        this.startThumbSize = startThumbSize
        this.endThumbSize = endThumbSize

        setStartAngle(startHourAngle + startMinutesAngle.toDouble())
        setEndAngle(endHourAngle + endMinutesAngle.toDouble())
        setBorderColor(borderColor)
        setThumbSize(thumbSize)
        setStartThumbImage(thumbImage)
        setEndThumbImage(thumbEndImage)
        setBackgroundImage(backgroundDrawable)
        setStartThumbColor(thumbColor)
        setArcColor(arcColor)
        setEndThumbColor(thumbEndColor)
        setArcDashSize(arcDashSize)
        setArcGradient(startGradientColor, endGradientColor)

        // assign padding - check for version because of RTL layout compatibility
        val padding: Int
        padding = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val all = paddingLeft + paddingRight + paddingBottom + paddingTop + paddingEnd + paddingStart
            all / 6
        } else {
            (paddingLeft + paddingRight + paddingBottom + paddingTop) / 4
        }
        setPadding(padding)
        if (isInEditMode) {
            return
        }
        a.recycle()
    }

    private fun setArcGradient(startGradientColor: Int, endGradientColor: Int) {
        this.startGradientColor = startGradientColor
        this.endGradientColor = endGradientColor
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { // use smaller dimension for calculations (depends on parent size)
        val smallerDim = if (w > h) h else w
        // find circle's rectangle points
        largestCenteredSquareLeft = (w - smallerDim) / 2
        largestCenteredSquareTop = (h - smallerDim) / 2
        largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim
        largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim
        // save circle coordinates and radius in fields
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2
        mCircleRadius = smallerDim / 2 - mBorderThickness / 2 - mPadding
        // works well for now, should we call something else here?
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // outer circle (ring)
        mPaint.color = mBorderColor
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mBorderThickness.toFloat()
        mPaint.isAntiAlias = true
        canvas.drawCircle(mCircleCenterX.toFloat(), mCircleCenterY.toFloat(), mCircleRadius.toFloat(), mPaint)
        // find thumb start position
        mThumbStartX = (mCircleCenterX + mCircleRadius * cos(mAngle)).toInt()
        mThumbStartY = (mCircleCenterY - mCircleRadius * sin(mAngle)).toInt()
        //find thumb end position
        mThumbEndX = (mCircleCenterX + mCircleRadius * cos(mAngleEnd)).toInt()
        mThumbEndY = (mCircleCenterY - mCircleRadius * sin(mAngleEnd)).toInt()
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeWidth = mArcDashSize.toFloat()
        mLinePaint.strokeCap = Paint.Cap.ROUND
        mLinePaint.color = if (mArcColor == 0) Color.RED else mArcColor
        mLinePaint.isAntiAlias = true
        if (linearGradient == null) {
            linearGradient = LinearGradient(0.toFloat(), 0.toFloat(), 0.toFloat(), height.toFloat(), startGradientColor, endGradientColor, Shader.TileMode.MIRROR)
            mLinePaint.shader = linearGradient
        }
        arcRect[mCircleCenterX - mCircleRadius, mCircleCenterY + mCircleRadius, mCircleCenterX + mCircleRadius] = mCircleCenterY - mCircleRadius
        arcRectF.set(arcRect)
        arcRectF.sort()
        val drawStart = toDrawingAngle(mAngle)
        val drawEnd = toDrawingAngle(mAngleEnd)
        var mThumbSize = startThumbSize
        canvas.drawArc(arcRectF, drawStart, (360 + drawEnd - drawStart) % 360, false, mLinePaint)
        if (mStartThumbImage != null) { // draw png
            mStartThumbImage!!.setBounds(mThumbStartX - mThumbSize / 2, mThumbStartY - mThumbSize / 2, mThumbStartX + mThumbSize / 2, mThumbStartY + mThumbSize / 2)
            mStartThumbImage!!.draw(canvas)
        } else { // draw colored circle
            mPaint.color = mStartThumbColor
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle(mThumbStartX.toFloat(), mThumbStartY.toFloat(), mThumbSize / 2.toFloat(), mPaint)
        }
        mThumbSize = endThumbSize
        if (mEndThumbImage != null) { // draw png
            mEndThumbImage!!.setBounds(mThumbEndX - mThumbSize / 2, mThumbEndY - mThumbSize / 2, mThumbEndX + mThumbSize / 2, mThumbEndY + mThumbSize / 2)
            mEndThumbImage!!.draw(canvas)
        } else {
            mPaint.style = Paint.Style.FILL
            mPaint.color = mEndThumbColor
            canvas.drawCircle(mThumbEndX.toFloat(), mThumbEndY.toFloat(), mThumbSize / 2.toFloat(), mPaint)
        }
        if (backgroundImage != null) { // draw png
            var bitmap = (backgroundImage as BitmapDrawable).bitmap
            bitmap = getRoundedShape(bitmap)
            canvas.drawBitmap(bitmap, borderThickness.toFloat(), borderThickness.toFloat(), mPaint)
        }
    }

    fun getRoundedShape(scaleBitmapImage: Bitmap): Bitmap {
        val targetWidth = width - 2 * borderThickness
        val targetHeight = height - 2 * borderThickness
        val targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(targetBitmap)
        val path = Path()
        path.addCircle((targetWidth.toFloat() - 1) / 2,
                (targetHeight.toFloat() - 1) / 2,
                targetWidth.toFloat().coerceAtMost(targetHeight.toFloat()) / 2,
                Path.Direction.CCW)
        canvas.clipPath(path)
        canvas.drawBitmap(scaleBitmapImage,
                Rect(0, 0, scaleBitmapImage.width,
                        scaleBitmapImage.height),
                Rect(0, 0, targetWidth, targetHeight), null)
        return targetBitmap
    }

    /**
     * Invoked when slider starts moving or is currently moving. This method calculates and sets position and angle of the thumb.
     *
     * @param touchX Where is the touch identifier now on X axis
     * @param touchY Where is the touch identifier now on Y axis
     */
    private fun updateSliderState(touchX: Int, touchY: Int, thumb: Thumb) {
        val distanceX = touchX - mCircleCenterX
        val distanceY = mCircleCenterY - touchY
        val c = sqrt(distanceX.toDouble().pow(2.0) + distanceY.toDouble().pow(2.0))
        var angle = acos(distanceX / c)
        if (distanceY < 0) angle = -angle
        if (thumb == Thumb.START) {
            mAngle = angle
        } else {
            mAngleEnd = angle
        }

        if (thumb == Thumb.START) {
            if (currentSelectedStartAngle != toDrawingAngle(angle)) {
                prevSelectedStartAngle = currentSelectedStartAngle
                mListener?.onStartSliderMoved(toDrawingAngle(angle).toDouble(), true)
                currentSelectedStartAngle = toDrawingAngle(angle)
            }
        } else {
            if (currentSelectedEndAngle != toDrawingAngle(angle)) {
                prevSelectedEndAngle = currentSelectedEndAngle
                currentSelectedEndAngle = toDrawingAngle(angle)
                mListener?.onEndSliderMoved(currentSelectedEndAngle.toDouble(), true)
            }
        }

    }

    fun updateSliderState(start: Double, end: Double) {
        mListener?.onStartSliderMoved(start, true)
        mListener?.onEndSliderMoved(end, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun toDrawingAngle(angleInRadians: Double): Float {
        var fixedAngle = Math.toDegrees(angleInRadians) % 360
        fixedAngle = if (angleInRadians > 0) 360 - fixedAngle else -fixedAngle
        return fixedAngle.toFloat()
    }

    private fun fromDrawingAngle(angleInDegrees: Double): Double {
        val radians = Math.toRadians(angleInDegrees)
        return -radians
    }

    /**
     * Set slider range moved listener.
     *
     * @param listener Instance of the slider range moved listener, or null when removing it
     */
    fun setOnSliderRangeMovedListener(listener: OnSliderRangeMovedListener?) {
        mListener = listener
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isFirstTouchPixelEqualZero(ev)) //if touch pixel equal 0 then do nothing
                    return false
                // start moving the thumb (this is the first touch)
                val x = ev.x.toInt()
                val y = ev.y.toInt()
                var mThumbSize = startThumbSize
                val isThumbStartPressed = x < mThumbStartX + mThumbSize && x > mThumbStartX - mThumbSize && y < mThumbStartY + mThumbSize && y > mThumbStartY - mThumbSize
                mThumbSize = endThumbSize
                val isThumbEndPressed = x < mThumbEndX + mThumbSize && x > mThumbEndX - mThumbSize && y < mThumbEndY + mThumbSize && y > mThumbEndY - mThumbSize
                if (isThumbStartPressed) {
                    mIsThumbSelected = true
                    updateSliderState(x, y, Thumb.START)
                } else if (isThumbEndPressed) {
                    mIsThumbEndSelected = true
                    updateSliderState(x, y, Thumb.END)
                }
                if (mIsThumbSelected) mListener?.onStartSliderEvent(ThumbEvent.THUMB_PRESSED)
                if (mIsThumbEndSelected) mListener?.onEndSliderEvent(ThumbEvent.THUMB_PRESSED)

            }
            MotionEvent.ACTION_MOVE -> {
                // still moving the thumb (this is not the first touch)
                if (mIsThumbSelected) {
                    val x = ev.x.toInt()
                    val y = ev.y.toInt()
                    updateSliderState(x, y, Thumb.START) //touch in start thumb
                } else if (mIsThumbEndSelected) {
                    val x = ev.x.toInt()
                    val y = ev.y.toInt()
                    updateSliderState(x, y, Thumb.END) //touch in end thumb
                } else {
                    val touchAngle = toDrawingAngle(getTouchAngle(ev)).toDouble() // calc touch angle (0-359)
                    if (isTouchOnGreyArea(touchAngle.toInt())) //if touch angle isn't on green area
                        return false
                    prevSelectedStartAngle = currentSelectedStartAngle
                    currentSelectedStartAngle = toDrawingAngle(mAngle)
                    prevSelectedEndAngle = currentSelectedEndAngle
                    currentSelectedEndAngle = toDrawingAngle(mAngleEnd)
                    //init prev and current angles
                    if (prevTouchAngle == currentTouchAngle && prevTouchAngle == 0.0) {
                        currentTouchAngle = touchAngle
                        prevTouchAngle = currentTouchAngle
                    }
                    prevTouchAngle = currentTouchAngle
                    currentTouchAngle = touchAngle
                    //if diff > 0 than shift clockwise, otherwise shift opposite clock direction
                    val diff = currentTouchAngle - prevTouchAngle
                    mAngle -= diff * (Math.PI / 180)
                    mAngleEnd -= diff * (Math.PI / 180)
                    //update start and end thumbs and also time.
                    mListener?.onStartSliderMoved(toDrawingAngle(mAngle).toDouble(), false)
                    mListener?.onEndSliderMoved(toDrawingAngle(mAngleEnd).toDouble(), false)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsThumbSelected) mListener?.onStartSliderEvent(ThumbEvent.THUMB_RELEASED)
                if (mIsThumbEndSelected) mListener?.onEndSliderEvent(ThumbEvent.THUMB_RELEASED)

                // finished moving (this is the last touch)
                mIsThumbSelected = false
                mIsThumbEndSelected = false
                currentTouchAngle = 0.0
                prevTouchAngle = currentTouchAngle
            }
        }
        invalidate()
        return true
    }

    private fun isTouchOnGreyArea(touchAngle: Int): Boolean {
        val prevThumbStartAngle = toDrawingAngle(mAngle).toDouble()
        val prevThumbEndAngle = toDrawingAngle(mAngleEnd).toDouble()
        val distance = (360 + prevThumbEndAngle - prevThumbStartAngle).toInt() % 360
        var isGreenTouch = false
        var i = prevThumbStartAngle.toInt()
        while (i < prevThumbStartAngle + distance) {
            if (i % 360 == touchAngle) {
                isGreenTouch = true
                break
            } else if (i % 360.toDouble() == prevThumbEndAngle) {
                isGreenTouch = false
                break
            }
            i++
        }
        return !isGreenTouch
    }

    fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun isFirstTouchPixelEqualZero(ev: MotionEvent): Boolean {
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        val bitmap = getBitmapFromView(this)!!
        val width = bitmap.width
        val height = bitmap.height
        if (x < width && y < height && x > 0 && y > 0) {
            val pixel = bitmap.getPixel(ev.x.toInt(), ev.y.toInt())
            if (pixel == 0) {
                return true
            }
        }
        return false
    }

    private fun getTouchAngle(ev: MotionEvent): Double {
        val distanceX = ev.x.toInt() - mCircleCenterX
        val distanceY = mCircleCenterY - ev.y.toInt()
        val c = sqrt(distanceX.toDouble().pow(2.0) + distanceY.toDouble().pow(2.0))
        var touchAngle = acos(distanceX / c)
        if (distanceY < 0) {
            touchAngle = -touchAngle
        }
        return touchAngle
    }

    fun setStartTime(startHour: Int, startMinute: Int) {
        this.startHourAngle = hourToHourAngle(startHour)
        this.startMinutesAngle = minutesToMinutesAngle(startMinute.toFloat())

        setStartAngle(startHourAngle + startMinutesAngle.toDouble())
        invalidate()
    }

    fun setEndTime(endHour: Int, endMinute: Int) {
        this.endHourAngle = hourToHourAngle(endHour)
        this.endMinutesAngle = minutesToMinutesAngle(endMinute.toFloat())

        setEndAngle(endHourAngle + endMinutesAngle.toDouble())
        invalidate()
    }

    val startThumbAngle: Double
        get() = toDrawingAngle(mAngle).toDouble()

    val endThumbAngle: Double
        get() = toDrawingAngle(mAngleEnd).toDouble()
    /* ***** Setters ***** */
    /**
     * Set start angle in degrees.
     * An angle of 0 degrees correspond to the geometric angle of 0 degrees (3 o'clock on a watch.)
     *
     * @param startAngle value in degrees.
     */
    fun setStartAngle(startAngle: Double) {
        mAngle = fromDrawingAngle(startAngle)
    }

    /**
     * Set end angle in degrees.
     * An angle of 0 degrees correspond to the geometric angle of 0 degrees (3 o'clock on a watch.)
     *
     * @param angle value in degrees.
     */
    fun setEndAngle(angle: Double) {
        mAngleEnd = fromDrawingAngle(angle)
    }

    fun setThumbSize(thumbSize: Int) {
        startThumbSize = thumbSize
        endThumbSize = thumbSize
    }

    var startThumbSize: Int
        get() = mStartThumbSize
        set(thumbSize) {
            if (thumbSize == THUMB_SIZE_NOT_DEFINED) return
            mStartThumbSize = thumbSize
        }

    var endThumbSize: Int
        get() = mEndThumbSize
        set(thumbSize) {
            if (thumbSize == THUMB_SIZE_NOT_DEFINED) return
            mEndThumbSize = thumbSize
        }

    var borderThickness: Int
        get() = mBorderThickness + mPadding
        set(circleBorderThickness) {
            mBorderThickness = circleBorderThickness
        }

    fun setBorderColor(color: Int) {
        mBorderColor = color
    }

    fun setStartThumbImage(drawable: Drawable?) {
        mStartThumbImage = drawable
    }

    fun setEndThumbImage(drawable: Drawable?) {
        mEndThumbImage = drawable
    }

    fun setStartThumbColor(color: Int) {
        mStartThumbColor = color
    }

    fun setEndThumbColor(color: Int) {
        mEndThumbColor = color
    }

    fun setPadding(padding: Int) {
        mPadding = padding
    }

    fun setArcDashSize(value: Int) {
        mArcDashSize = value
    }

    fun setArcColor(color: Int) {
        mArcColor = color
    }

    fun hourToHourAngle(hour: Int): Int {
        var hourAngle = (hour - 3) * 30
        if (hourAngle < 0) {
            hourAngle += 360
        }
        return hourAngle
    }

    fun setBackgroundImage(backgroundImage: Drawable?) {
        this.backgroundImage = backgroundImage
    }

    fun minutesToMinutesAngle(minutes: Float): Float {
        return minutes / 2
    }

    companion object {
        private const val THUMB_SIZE_NOT_DEFINED = -1
    }

    init {
        init(context, attrs!!, defStyleAttr)
    }
}