package com.lightningkite.kotlincomponents.animation

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import com.lightningkite.kotlincomponents.math.degreesTo
import java.lang.ref.WeakReference

/**
 * Created by jivie on 9/28/15.
 */
public class ActionAnimator<T, V>(
        target: T,
        public var startValue: V,
        public val action: T.(value: V) -> Unit,
        public var interpolator: ((startVal: V, progress: Float, endVal: V) -> V)
) {
    public val weak: WeakReference<T> = WeakReference(target)
    public val handler: Handler = Handler(Looper.getMainLooper())

    private var endValue: V? = null
    private var duration: Long = 0
    private var delta: Long = 20
    private var timeElapsed: Long = 0
    private var shouldRun: Boolean = false

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (!shouldRun || endValue == null) return

            timeElapsed = Math.min(timeElapsed + delta, duration)
            val currentVal = interpolator(startValue, timeElapsed.toFloat() / duration, endValue!!)
            weak.get()?.action(currentVal)

            if (timeElapsed < duration && weak.get() != null) {
                handler.postDelayed(runnable, delta)
            } else {
                startValue = currentVal
            }
        }
    }

    public fun animate(
            to: V,
            newDuration: Long,
            newDelta: Long = 20L
    ) {
        stop()
        delta = newDelta
        duration = newDuration
        timeElapsed = 0
        endValue = to
        shouldRun = true
        handler.postDelayed(runnable, delta)
    }

    public fun stop() {
        if (endValue != null) {
            startValue = interpolator(startValue, timeElapsed.toFloat() / duration, endValue!!)
        }
        shouldRun = false
    }
}

public fun interpolateARGB(from: Int, interpolationValue: Float, to: Int): Int {
    val a1 = Color.alpha(from)
    val r1 = Color.red(from)
    val g1 = Color.green(from)
    val b1 = Color.blue(from)

    val a2 = Color.alpha(to)
    val r2 = Color.red(to)
    val g2 = Color.green(to)
    val b2 = Color.blue(to)

    val inv = 1 - interpolationValue

    return Color.argb(
            (a2 * interpolationValue + a1 * inv).toInt(),
            (r2 * interpolationValue + r1 * inv).toInt(),
            (g2 * interpolationValue + g1 * inv).toInt(),
            (b2 * interpolationValue + b1 * inv).toInt()
    )
}

public fun interpolateHSV(from: Int, interpolationValue: Float, to: Int): Int {
    val thisHSV: FloatArray = FloatArray(3)
    Color.colorToHSV(from, thisHSV)
    val toHSV: FloatArray = FloatArray(3)
    Color.colorToHSV(to, toHSV)

    val fromA = Color.alpha(from)
    val toA = Color.alpha(to)

    val diff = thisHSV[0].degreesTo(toHSV[0])

    val inv = 1 - interpolationValue

    val interpHSV: FloatArray = floatArrayOf(
            thisHSV[0] + diff * interpolationValue,
            thisHSV[1] * inv + toHSV[1] * interpolationValue,
            thisHSV[2] * inv + toHSV[2] * interpolationValue
    )

    return Color.HSVToColor((toA * interpolationValue + fromA * inv).toInt(), interpHSV)
}