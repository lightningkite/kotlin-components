package com.lightningkite.kotlincomponents.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import org.jetbrains.anko.notificationManager

/**
 * Used to conveniently make notifications.
 * Created by jivie on 9/14/15.
 */

public fun Context.makeNotification(start: Notification.Builder.() -> Unit): Notification {
    val builder = Notification.Builder(this)
    builder.start()
    return builder.build()
}

public fun Context.notification(id: Int = 0, tag: String = "", n: Notification) {
    notificationManager.notify(tag, id, n)
}

public fun Context.notification(id: Int = 0, tag: String = "", setup: Notification.Builder.() -> Unit) {
    notificationManager.notify(tag, id, makeNotification(setup))
}

public var Notification.Builder.contentIntent: PendingIntent
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentIntent(value)
    }

public var Notification.Builder.autoCancel: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setAutoCancel(value)
    }

public var Notification.Builder.category: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setCategory(value)
    }

public var Notification.Builder.color: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setColor(value)
    }

public var Notification.Builder.content: RemoteViews
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContent(value)
    }

public var Notification.Builder.contentInfo: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentInfo(value)
    }

public var Notification.Builder.contentText: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentText(value)
    }

public var Notification.Builder.contentTitle: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setContentTitle(value)
    }

public var Notification.Builder.defaults: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setDefaults(value)
    }

public var Notification.Builder.deleteIntent: PendingIntent
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setDeleteIntent(value)
    }

public var Notification.Builder.extras: Bundle
    get() = getExtras() ?: { val bundle = Bundle(); setExtras(bundle); bundle }()
    set(value) {
        setExtras(value)
    }

public var Notification.Builder.fullscreenIntent: PendingIntent
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setFullScreenIntent(value, false)
    }

public var Notification.Builder.group: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setGroup(value)
    }

public var Notification.Builder.groupSummary: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setGroupSummary(value)
    }

public var Notification.Builder.largeIcon: Bitmap
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setLargeIcon(value)
    }

public var Notification.Builder.number: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setNumber(value)
    }

public var Notification.Builder.ongoing: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setOngoing(value)
    }

public var Notification.Builder.onlyAlertOnce: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setOnlyAlertOnce(value)
    }

public var Notification.Builder.priority: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setPriority(value)
    }

public var Notification.Builder.progress: Float
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setProgress(1000, (progress * 1000).toInt(), false)
    }

public var Notification.Builder.progressing: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        if (value) setProgress(2, 1, true)
    }

public var Notification.Builder.localOnly: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setLocalOnly(value)
    }

public var Notification.Builder.publicVersion: Notification
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setPublicVersion(value)
    }

public var Notification.Builder.showWhen: Boolean
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setShowWhen(value)
    }

public var Notification.Builder.sortKey: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSortKey(value)
    }

public var Notification.Builder.smallIcon: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSmallIcon(value)
    }

public var Notification.Builder.sound: Uri
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSound(value)
    }

public var Notification.Builder.style: Notification.Style
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setStyle(value)
    }

public var Notification.Builder.subText: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setSubText(value)
    }

public var Notification.Builder.ticker: String
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setTicker(value)
    }

public var Notification.Builder.vibrate: LongArray
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setVibrate(value)
    }

public var Notification.Builder.visibility: Int
    get() = throw Exception("This is not accessible publicly.")
    set(value) {
        setVisibility(value)
    }