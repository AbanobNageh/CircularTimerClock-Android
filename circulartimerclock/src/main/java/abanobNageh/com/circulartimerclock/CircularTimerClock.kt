package abanobNageh.com.circulartimerclock;

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import java.util.*

class CircularTimerClock @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    interface ontTimeChanged {
        fun onStartTimeChange(time: String?, hour: Int, minutes: Int, isAM: Boolean)
        fun onEndTimeChange(time: String?, hour: Int, minutes: Int, isAM: Boolean)
    }

    private var interval = 1
    private var isStartTImeAM = false
    private var isEndTimeAM = false
    private var isClockInside = true
    private var circularSliderView: CircularSliderView? = null
    private var clockView: ClockView? = null
    private val startTimeSB = StringBuilder()
    private val endTimeSB = StringBuilder()
    private val locale = Locale("en_US")
    private var circularSliderWrapper: FrameLayout? = null
    private var startMinutes = 0
    private var startHour = 0
    private var endMinutes = 0
    private var endHour = 0
    private var ontTimeChangedListener: CircularTimerClock.ontTimeChanged? = null

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0)
        isStartTImeAM = a.getBoolean(R.styleable.CircularSlider_start_time_is_am, true)
        isEndTimeAM = a.getBoolean(R.styleable.CircularSlider_end_time_is_am, true)
        isClockInside = a.getBoolean(R.styleable.CircularSlider_is_clock_inside, true)
        interval = a.getInteger(R.styleable.CircularSlider_clock_time_interval, Companion.INTERVAL_1M)
        a.recycle()

        val layout = LayoutInflater.from(getContext()).inflate(R.layout.circular_timer_clock_layout, this)
        circularSliderWrapper = layout.findViewById(R.id.circular_slider_wrapper)
        circularSliderView = CircularSliderView(getContext(), attrs, defStyleAttr)
        circularSliderWrapper!!.addView(circularSliderView)
        clockView = ClockView(getContext(), attrs, defStyleAttr)

        circularSliderView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val lp: LayoutParams
                if (isClockInside) {
                    lp = LayoutParams(circularSliderView!!.width - 2 * circularSliderView!!.borderThickness, circularSliderView!!.height - 2 * circularSliderView!!.borderThickness)
                } else {
                    lp = LayoutParams(circularSliderView!!.width, circularSliderView!!.height)
                }

                lp.gravity = Gravity.CENTER
                clockView!!.layoutParams = lp

                circularSliderWrapper!!.addView(clockView)
                circularSliderView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        circularSliderView!!.isStartTimeAM = isStartTImeAM
        circularSliderView!!.isEndTimeAM = isEndTimeAM
        circularSliderView!!.updateSliderState(circularSliderView!!.startThumbAngle, circularSliderView!!.endThumbAngle)

        circularSliderView!!.setOnSliderRangeMovedListener(object : CircularSliderView.OnSliderRangeMovedListener {
            override fun onStartSliderMoved(pos: Double, isThumbSlide: Boolean) {
                val min = pos / 0.5 % 60
                startMinutes = (min / interval).toInt() * interval
                startHour = ((pos / 30f + 2f) % 12f).toInt() + 1
                if (circularSliderView!!.prevSelectedStartAngle > 90 && circularSliderView!!.currentSelectedStartAngle > 90 && (circularSliderView!!.prevSelectedStartAngle < 270 && circularSliderView!!.currentSelectedStartAngle >= 270 ||
                                circularSliderView!!.prevSelectedStartAngle > 270 && circularSliderView!!.currentSelectedStartAngle <= 270)) {
                    circularSliderView!!.isStartTimeAM = !circularSliderView!!.isStartTimeAM
                }
                startTimeSB.delete(0, startTimeSB.length)
                startTimeSB.append(startHour)
                        .append(":")
                        .append(if (startMinutes < 10) String.format(locale, "%02d", startMinutes) else startMinutes)
                        .append(if (circularSliderView!!.isStartTimeAM) Companion.AM else Companion.PM)
                ontTimeChangedListener?.onStartTimeChange(startTimeSB.toString(), startHour, startMinutes, isStartTImeAM)
            }

            override fun onEndSliderMoved(pos: Double, isThumbSlide: Boolean) {
                val min = pos / 0.5 % 60
                endMinutes = (min / interval).toInt() * interval
                endHour = ((pos / 30f + 2f) % 12f).toInt() + 1
                if (circularSliderView!!.prevSelectedEndAngle > 90 && circularSliderView!!.currentSelectedEndAngle > 90 && (circularSliderView!!.prevSelectedEndAngle < 270 && circularSliderView!!.currentSelectedEndAngle >= 270 ||
                                circularSliderView!!.prevSelectedEndAngle > 270 && circularSliderView!!.currentSelectedEndAngle <= 270)) {
                    circularSliderView!!.isEndTimeAM = !circularSliderView!!.isEndTimeAM
                }
                endTimeSB.delete(0, endTimeSB.length)
                endTimeSB.append(endHour)
                        .append(":")
                        .append(if (endMinutes < 10) String.format(locale, "%02d", endMinutes) else endMinutes)
                        .append(if (circularSliderView!!.isEndTimeAM) Companion.AM else Companion.PM)
                ontTimeChangedListener?.onEndTimeChange(endTimeSB.toString(), endHour, endMinutes, isEndTimeAM)
            }

            override fun onStartSliderEvent(event: ThumbEvent?) {
            }

            override fun onEndSliderEvent(event: ThumbEvent?) {
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        circularSliderView!!.onTouchEvent(event!!)
        return false
    }

    /**
     *  Sets the start time on the clock.
     *  @param startHour The start hour, must be between 1 and 12, if it is smaller than 1 it will be set to 1
     *  and if it's bigger than 12 it will be set to 12.
     *  @param startMinute The start minute, must be between 0 and 59, if it is smaller than 0 it will be set to 0
     *  and if it's bigger than 59 it will be set to 59.
     */
    fun setStartTime(startHour: Int, startMinute: Int) {
        var hour: Int = startHour
        var minute: Int = startMinute

        if (hour < 1) {
            hour = 1
        } else if (hour > 12) {
            hour = 12
        }

        if (minute < 0) {
            minute = 0
        } else if (minute > 59) {
            minute = 59
        }

        circularSliderView?.setStartTime(hour, minute)
    }

    /**
     *  Sets the end time on the clock.
     *  @param endHour The end hour, must be between 1 and 12, if it is smaller than 1 it will be set to 1
     *  and if it's bigger than 12 it will be set to 12.
     *  @param endMinute The end minute, must be between 0 and 59, if it is smaller than 0 it will be set to 0
     *  and if it's bigger than 59 it will be set to 59.
     */
    fun setEndTime(endHour: Int, endMinute: Int) {
        var hour: Int = endHour
        var minute: Int = endMinute

        if (hour < 1) {
            hour = 1
        } else if (hour > 12) {
            hour = 12
        }

        if (minute < 0) {
            minute = 0
        } else if (minute > 59) {
            minute = 59
        }

        circularSliderView?.setEndTime(hour, minute)
    }

    fun isStartTimeAM(): Boolean {
        return circularSliderView!!.isStartTimeAM
    }

    fun isEndTimeAM(): Boolean {
        return circularSliderView!!.isEndTimeAM
    }

    fun getStartMinutes(): Int {
        return startMinutes
    }

    fun getEndMinutes(): Int {
        return endMinutes
    }

    fun getEndHour(): Int {
        return endHour
    }

    fun getStartHour(): Int {
        return startHour
    }

    fun setClockStyle(isClockInside: Boolean) {
        this.isClockInside = isClockInside
    }

    fun isClockInside(): Boolean {
        return isClockInside
    }

    fun setOnTimeChangedListener(listener: CircularTimerClock.ontTimeChanged) {
        ontTimeChangedListener = listener
    }

    init {
        init(context, attrs!!, defStyleAttr)
    }

    companion object {
        private const val INTERVAL_1M = 1
        private const val AM = " AM"
        private const val PM = " PM"
    }
}
