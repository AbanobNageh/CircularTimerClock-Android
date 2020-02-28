# CircularTimerClock-Android
This is a fork from [this repository](https://github.com/giliy/CircularTimerClock-Android).

Circular clock view that can set timer range between hours.
CircularTimerClock is an android version based on the ios view of the build-in clock.
With CircularTimeView you can set timer between hours and minutes. 
Your can change the interval of of the clock between 1 minute to 60 minutes and retrieve current timer

![main](https://user-images.githubusercontent.com/22684921/75572497-8f42c000-5a63-11ea-88f2-82fb25c2befa.gif)

The following has been updated:
V0.2.0:
1. The library was rewritten in Kotlin.
2. The library now targets SDK version 29.
3. Fixed minor bug in SDK 29.
4. Added the ability to set the start and end time programmatically.

![set time](https://user-images.githubusercontent.com/22684921/75571362-ae405280-5a61-11ea-9f39-ac96919c07a1.gif)

## Attributes

| Attr  | format | decription |
| ------------- | ------------- | ------------- |
| start_hour  | integer  | An integer between 1 to 12  |
| end_hour  | integer  | An integer between 1 to 12  |
| start_minutes  | integer  | An integer between 0 to 59  |
| end_minutes  | integer  | An integer between 0 to 59  |
| clock_time_interval  | integer  | An integer between 0 to 59. The interval of the clock jump that time can be display|
| clock_tick_interval  | integer  | An integer between 0 to 59. The clock tick interval of the clock |
| clock_hour_color  | color  | The color of the hours clock  |
| clock_tick_color  | color  | The color of the clock ticks  |
| border_color  | color  | The color of the clock border  |
| arc_gradient_color_start  | color  | The start arc color gradient  |
| arc_gradient_color_end  | color  | the end color gradient  |
| start_thumb_color  | color  | The color of the start thumb |
| end_thumb_color  | color  | The color of the end thumb  |
|  arc_color | color  | The arc color between start and end thumbs  |
|  start_thumb_size | dimention  | The start thumb size  |
|  end_thumb_size | dimention  | The end thumb size  |
| arc_dash_size  | dimention  | The thickness of the line between the start and end  |
| border_thickness  | dimention  | The thickness of the clock border  |
| thumb_size  | dimention  | The size of the thumbs  |
| hours_size  | dimention  | The size of the clock hours  |
| start_time_is_am  | boolean  | Is the start time is AM  |
| end_time_is_am  | boolean  | Is the end time is AM  |
| is_clock_inside  | boolean  | Is the clock view insie  |
| start_thumb_image  | reference  | the image drawable of the start thumb  |
| end_thumb_image  | reference  | the image drawable of the end thumb  |
| clock_background_image  | reference  | The image drawable of the backgound clock  |

# How to use

### Add this to root build.gradle
```
repositories {
     maven {
         url "https://jitpack.io"
     }
}
```

### Add this to build.gradle
```
dependencies {
    implementation 'com.github.AbanobNageh:CircularTimerClock-Android:0.2.0'
}
```

### To use it in your code
Simply add the View to your layout xml

```
 <abanobNageh.com.circulartimerclock.CircularTimerClock
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/circular_clock"
        android:layout_gravity="center"
        timer:arc_dash_size="40dp"
        timer:start_hour="5"
        timer:start_minutes="20"
        timer:end_hour="9"
        timer:end_minutes="30"
        timer:clock_time_interval="5"
        timer:clock_tick_interval="15"
        timer:clock_hour_color="#FFF"
        timer:clock_tick_color="#FFF"
        timer:hours_size="18sp"
        timer:start_time_is_am="true"
        timer:end_time_is_am="false"
        timer:is_clock_inside="true"
        timer:border_thickness="40dp"
        timer:border_color="#e7e7e7"
        timer:arc_gradient_color_start="#78f8b8"
        timer:arc_gradient_color_end="#00964b"
        timer:clock_background_image="@drawable/sunrise"
        timer:start_thumb_image="@drawable/circle_toggle_shape"
        timer:end_thumb_image="@drawable/circle_toggle_shape"
        timer:thumb_size="35dp" >

    </abanobNageh.com.circulartimerclock.CircularTimerClock>
```
To retrieve the time simply call the view from your activity like this
```kotlin
val clock = findViewById<View>(R.id.circular_clock) as CircularTimerClock
clock.setOnTimeChangedListener(object : CircularTimerClock.ontTimeChanged {
     override fun onStartTimeChange(time: String?, hour: Int, minutes: Int, isAM: Boolean) {
          Log.d("start time: ", "" + time)
     }

     override fun onEndTimeChange(time: String?, hour: Int, minutes: Int, isAM: Boolean) {
          Log.d("end time: ", "" + time)
     }
})
```
To set the clock time programmatically you can use these functions
```kotlin
val clock = findViewById<View>(R.id.circular_clock) as CircularTimerClock
clock.setStartTime(startHour, startMinute)
clock.setEndTime(endHour, endMinute)
```
