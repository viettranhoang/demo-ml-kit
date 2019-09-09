package com.vit.demomlkit.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object ImageUtils {

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    @Throws(IOException::class)
    fun saveBitmap(bmp: Bitmap): File {
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
        val f = File((Environment.getExternalStorageDirectory()).toString() + File.separator + "UmbalaTv.jpg")
        if (!f.exists()) f.parentFile.mkdirs()
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()
        return f
    }

    fun getBitmapOffset(img: ImageView, includeLayout: Boolean): IntArray {
        val offset = IntArray(2)
        val values = FloatArray(9)

        val m = img.imageMatrix
        m.getValues(values)

        offset[0] = values[5].toInt()
        offset[1] = values[2].toInt()

        if (includeLayout) {
            val lp = img.layoutParams as ViewGroup.MarginLayoutParams
            val paddingTop = img.paddingTop
            val paddingLeft = img.paddingLeft

            offset[0] += paddingTop + lp.topMargin
            offset[1] += paddingLeft + lp.leftMargin
        }

        return offset
    }

    fun compressImage(actualFile: File, context: Context): File {
        try {
            return Compressor(context)
                .setMaxHeight(1280)
                .setMaxWidth(720)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFile(actualFile)

        } catch (e: IOException) {
            Log.i("ImageUtils", "compressImage: ")
            
            e.printStackTrace()
        }

        return actualFile
    }

    fun getBitmap(file: File) = BitmapFactory.decodeFile(file.absolutePath)

    fun getBitmap(uri: Uri, context: Context) = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

    fun getBitmap(bytes: ByteArray) = BitmapFactory.decodeByteArray(bytes, 0, bytes.size);

}