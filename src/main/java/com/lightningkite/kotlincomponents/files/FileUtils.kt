package com.lightningkite.kotlincomponents.files

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

/**
 * Created by jivie on 8/14/15.
 */

public fun Uri.getRealPath(context: Context): String? {

    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, this)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument()) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex())
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }

            // TODO handle non-primary volumes
        } else if (isDownloadsDocument()) {

            val id = DocumentsContract.getDocumentId(this)
            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

            return context.getDataColumn(contentUri, null, null)
        } else if (isMediaDocument()) {
            val docId = DocumentsContract.getDocumentId(this)
            val split = docId.split(":".toRegex())
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else return null

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return context.getDataColumn(contentUri, selection, selectionArgs)
        }// MediaProvider
        // DownloadsProvider
    } else if ("content".equals(scheme, ignoreCase = true)) {
        return context.getDataColumn(this, null, null)
    } else if ("file".equals(scheme, ignoreCase = true)) {
        return path
    }// File
    // MediaStore (and general)

    return null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.

 * @param uri           The Uri to query.
 * *
 * @param selection     (Optional) Filter used in the query.
 * *
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * *
 * @return The value of the _data column, which is typically a file path.
 */
public fun Context.getDataColumn(uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {

    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = this.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return null
}


/**
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
public fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == authority
}

/**
 * @return Whether the Uri authority is DownloadsProvider.
 */
public fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == authority
}

/**
 * @return Whether the Uri authority is MediaProvider.
 */
public fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == authority
}