package com.lightningkite.kotlincomponents.ui

import android.content.res.Resources
import com.lightningkite.kotlincomponents.versionOn

/**
 * Created by jivie on 5/4/16.
 */
fun Resources.getColorCompat(resources: Int): Int {
    return versionOn(23, { getColor(resources, null) }, { getColor(resources) })
}