package com.lightningkite.kotlincomponents.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import org.jetbrains.anko.notificationManager

/**
 * Used to conveniently make notifications.
 * Created by jivie on 9/14/15.
 */

fun Context.makeNotification(start: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this)
    builder.start()
    return builder.build()
}

fun Context.notification(id: Int = 0, tag: String = "", n: Notification) {
    notificationManager.notify(tag, id, n)
}

fun Context.notification(id: Int = 0, tag: String = "", setup: NotificationCompat.Builder.() -> Unit) {
    notificationManager.notify(tag, id, makeNotification(setup))
}

var NotificationCompat.Builder.contentIntent: PendingIntent
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentIntent(value)
    }

var NotificationCompat.Builder.autoCancel: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setAutoCancel(value)
    }

var NotificationCompat.Builder.category: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setCategory(value)
    }

var NotificationCompat.Builder.color: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setColor(value)
    }

var NotificationCompat.Builder.content: RemoteViews
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContent(value)
    }

var NotificationCompat.Builder.contentInfo: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentInfo(value)
    }

var NotificationCompat.Builder.contentText: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentText(value)
    }

var NotificationCompat.Builder.contentTitle: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentTitle(value)
    }

var NotificationCompat.Builder.defaults: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setDefaults(value)
    }

var NotificationCompat.Builder.deleteIntent: PendingIntent
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setDeleteIntent(value)
    }

var NotificationCompat.Builder.fullscreenIntent: PendingIntent
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setFullScreenIntent(value, false)
    }

var NotificationCompat.Builder.group: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setGroup(value)
    }

var NotificationCompat.Builder.groupSummary: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setGroupSummary(value)
    }

var NotificationCompat.Builder.largeIcon: Bitmap
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setLargeIcon(value)
    }

var NotificationCompat.Builder.number: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setNumber(value)
    }

var NotificationCompat.Builder.ongoing: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setOngoing(value)
    }

var NotificationCompat.Builder.onlyAlertOnce: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setOnlyAlertOnce(value)
    }

var NotificationCompat.Builder.priority: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setPriority(value)
    }

var NotificationCompat.Builder.progress: Float
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setProgress(1000, (progress * 1000).toInt(), false)
    }

var NotificationCompat.Builder.progressing: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        if (value) setProgress(2, 1, true)
    }

var NotificationCompat.Builder.localOnly: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setLocalOnly(value)
    }

var NotificationCompat.Builder.publicVersion: Notification
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setPublicVersion(value)
    }

var NotificationCompat.Builder.showWhen: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setShowWhen(value)
    }

var NotificationCompat.Builder.sortKey: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSortKey(value)
    }

var NotificationCompat.Builder.smallIcon: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSmallIcon(value)
    }

var NotificationCompat.Builder.sound: Uri
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSound(value)
    }

var NotificationCompat.Builder.style: NotificationCompat.Style
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setStyle(value)
    }

var NotificationCompat.Builder.subText: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSubText(value)
    }

var NotificationCompat.Builder.ticker: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setTicker(value)
    }

var NotificationCompat.Builder.vibrate: LongArray
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setVibrate(value)
    }

var NotificationCompat.Builder.visibility: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setVisibility(value)
    }