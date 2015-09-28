package com.lightningkite.kotlincomponents.math

/**
 * Created by jivie on 9/28/15.
 */

public fun Float.degreesTo(to: Float): Float {
    return ((to - this + 180).remainder(360f)) - 180
}

public fun Float.radiansTo(to: Float): Float {
    return (((to - this + Math.PI).remainder(Math.PI * 2)) - Math.PI).toFloat()
}

public fun Float.degreesTo(to: Double): Double {
    return ((to - this + 180).remainder(360.0)) - 180
}

public fun Float.radiansTo(to: Double): Double {
    return ((to - this + Math.PI).remainder(Math.PI * 2)) - Math.PI
}

public fun Float.remainder(divisor: Float): Float {
    return this - Math.floor(this.toDouble() / divisor).toFloat() * divisor
}

public fun Double.remainder(divisor: Double): Double {
    return this - Math.floor(this / divisor) * divisor
}