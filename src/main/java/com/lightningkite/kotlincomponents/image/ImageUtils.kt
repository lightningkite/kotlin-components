package com.lightningkite.kotlincomponents.image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.lightningkite.kotlincomponents.files.getRealPath
import com.lightningkite.kotlincomponents.files.toImageContentUri
import com.lightningkite.kotlincomponents.viewcontroller.implementations.VCActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Functions for dealing with images.
 * Created by jivie on 8/14/15.
 */

/**
 * Pops up a dialog for getting an image from the gallery, returning it in [onResult].
 */
fun VCActivity.getImageUriFromGallery(onResult: (Uri?) -> Unit) {
    try {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        this.startIntent(chooserIntent) { code, data ->
            try {
                if (code != Activity.RESULT_OK) {
                    //cancelled
                    onResult(null)
                    return@startIntent
                }
                if (data == null) {
                    onResult(null)
                    return@startIntent
                }
                val imageUri = data.data
                onResult(imageUri)
            } catch(e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    } catch(e: Exception) {
        e.printStackTrace()
        onResult(null)
    }
}

/**
 * Opens the camera to take a picture, returning it in [onResult].
 */
fun VCActivity.getImageUriFromCamera(onResult: (Uri?) -> Unit) {
    try {
        val folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (folder == null) {
            onResult(null)
            return;
        }

        folder.mkdir()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File.createTempFile(timeStamp, ".jpg", folder)
        val potentialFile: Uri = Uri.fromFile(file)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, potentialFile)
        this.startIntent(intent) { code, data ->
            try {
                if (code != Activity.RESULT_OK) {
                    onResult(null)
                    return@startIntent
                }
                val fixedUri = File((data?.data ?: potentialFile).getRealPath(this)).toImageContentUri(this)
                onResult(fixedUri)
            } catch(e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    } catch(e: Exception) {
        e.printStackTrace()
        onResult(null)
    }
}

/**
 * Pops up a dialog for getting an image from the gallery, returning it in [onResult].
 */
fun VCActivity.getImageFromGallery(maxDimension: Int, onResult: (Bitmap?) -> Unit) {
    val getIntent = Intent(Intent.ACTION_GET_CONTENT)
    getIntent.type = "image/*"

    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    pickIntent.type = "image/*"

    val chooserIntent = Intent.createChooser(getIntent, "Select Image")
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

    this.startIntent(chooserIntent) { code, data ->
        if (code != Activity.RESULT_OK) {
            onResult(null); return@startIntent
        }
        if (data == null) return@startIntent
        val imageUri = data.data
        onResult(getBitmapFromUri(imageUri, maxDimension))
    }
}

/**
 * Opens the camera to take a picture, returning it in [onResult].
 */
fun VCActivity.getImageFromCamera(maxDimension: Int, onResult: (Bitmap?) -> Unit) {
    val folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (folder == null) {
        onResult(null)
        return;
    }

    folder.mkdir()

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val file = File(folder, "image_" + timeStamp + "_raw.jpg")
    val potentialFile: Uri = Uri.fromFile(file)

    intent.putExtra(MediaStore.EXTRA_OUTPUT, potentialFile)
    this.startIntent(intent) { code, data ->
        if (code != Activity.RESULT_OK) {
            onResult(null); return@startIntent
        }
        onResult(getBitmapFromUri(potentialFile, maxDimension))
    }
}

/**
 * Rotates a bitmap, creating a new bitmap.  Beware of memory allocations.
 */
fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix()
    val w = width
    val h = height

    matrix.postRotate(degrees.toFloat())

    return Bitmap.createBitmap(this, 0, 0, w, h, matrix, true)
}

/**
 * Gets a bitmap from a Uri, scaling it down if necessary.
 */
fun Context.getBitmapFromUri(inputUri: Uri, maxDimension: Int): Bitmap? {
    val initialBitmap = lessResolution(this, inputUri, maxDimension, maxDimension) ?: return null
    var bitmap: Bitmap = initialBitmap
    try {
        val exif = ExifInterface(inputUri.getRealPath(this))
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                bitmap = initialBitmap.rotate(90)
                initialBitmap.recycle()
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                bitmap = initialBitmap.rotate(180)
                initialBitmap.recycle()
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                bitmap = initialBitmap.rotate(270)
                initialBitmap.recycle()
            }
        }
        //When choosing from the photos app on my phone it throws a IllegalArgumentException
        //saying that the filename is null.  But in this instance we don't need to change the
        //orientation and the bitmap is not null.
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: NullPointerException) {
        e.printStackTrace()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }

    return bitmap
}

/**
 * Saves a bitmap to a file with a certain compression level between 0 and 100.
 */
private fun saveBitmap(outputFile: File, bitmap: Bitmap, compression: Int) {
    var out: FileOutputStream? = null
    try {
        Log.d("ImageFileManipulation", bitmap.toString())
        Log.d("ImageFileManipulation", outputFile.toString())
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, out)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (out != null) {
                out.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

private fun lessResolution(context: Context, fileUri: Uri, width: Int, height: Int): Bitmap? {
    var inputStream: InputStream? = null
    try {
        val options = BitmapFactory.Options()

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true

        inputStream = context.contentResolver.openInputStream(fileUri)
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream!!.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        inputStream = context.contentResolver.openInputStream(fileUri)
        val returnValue = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream!!.close()

        return returnValue

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (inputStream != null) {
                inputStream.close()
            }
        } catch (e: Exception) {
            /*squish*/
        }

    }
    return null
}

fun BitmapFactory_decodeByteArraySized(array: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {
    val measureOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(array, 0, array.size, measureOptions)
    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(measureOptions, reqWidth, reqHeight)
    }
    return BitmapFactory.decodeByteArray(array, 0, array.size, options)
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        // Calculate ratios of height and width to requested height and width
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}

@Suppress("NOTHING_TO_INLINE")
inline fun calculateInSampleSizeMax(options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int): Int {
    var inSampleSize = 1

    if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
        // Calculate ratios of height and width to requested height and width
        val heightRatio = Math.ceil(options.outHeight / maxHeight.toDouble()).toInt()
        val widthRatio = Math.ceil(options.outWidth / maxWidth.toDouble()).toInt()

        // Choose the bigger ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions smaller than or equal to the
        // requested height and width.
        inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}