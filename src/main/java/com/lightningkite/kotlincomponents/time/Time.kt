package com.lightningkite.kotlincomponents.time

import org.joda.time.Interval
import org.joda.time.Period

/**
 * Created by jivie on 11/4/15.
 */
fun Period.humanize():String{
    if(years > 1) return "$years years"
    if(years > 0) return "a year"
    if(months > 1) return "$months months"
    if(months > 0) return "a month"
    if(days > 1) return "$days days"
    if(days > 0) return "a day"
    if(hours > 1) return "$hours hours"
    if(hours > 0) return "an hour"
    if(minutes > 1) return "$minutes minutes"
    if(minutes > 0) return "a minute"
    if(seconds > 1) return "$seconds seconds"
    if(seconds > 0) return "a second"
    return "less than a second"
}
fun Period.humanizeToMinute():String{
    if(years > 1) return "$years years"
    if(years > 0) return "a year"
    if(months > 1) return "$months months"
    if(months > 0) return "a month"
    if(days > 1) return "$days days"
    if(days > 0) return "a day"
    if(hours > 1) return "$hours hours"
    if(hours > 0) return "an hour"
    if(minutes > 1) return "$minutes minutes"
    if(minutes > 0) return "a minute"
    return "less than a minute"
}