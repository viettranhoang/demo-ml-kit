package com.vit.demomlkit.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object IntentUtils {

    const val RC_GALLERY = 123
    const val RC_CAMERA = 456

    fun shareText(activity: Activity, text: String, subject: String = "") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        activity.startActivity(Intent.createChooser(intent, "Chia sẻ với"))
    }

    fun shareToMessenger(activity: Activity, text: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.facebook.orca")
        activity.startActivity(sendIntent)
    }

    fun shareToInstagram(activity: Activity, file: File) {
        val type = "image/*"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = type
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
            activity,
            activity.applicationContext.packageName + ".provider",
            file))
        shareIntent.setPackage("com.instagram.android")
        activity.startActivity(shareIntent)
    }

    fun galleryIntent(): Intent {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }

    fun pickImageFromGallery(fragment: Fragment) {
        fragment.startActivityForResult(galleryIntent(), RC_GALLERY)
    }

    fun pickImageFromGallery(activity: Activity) {
        activity.startActivityForResult(galleryIntent(), RC_GALLERY)
    }

    fun takePhoto(activity: Activity) {
        activity.startActivityForResult(cameraIntent(activity), RC_CAMERA)
    }

    fun cameraIntent(context: Context): Intent {
        val filephoto = try {
            createImageFile(context)
        } catch (e: IOException) {
            Log.i("IntentUtils", "cameraIntent: ")
            null
        }
        val imageURI = FileProvider.getUriForFile(context, "com.vit.demomlkit.provider", filephoto!!)
        Log.i("IntentUtils", "cameraIntent: $imageURI")

        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
        return pictureIntent
    }

    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HH").format(Date())
//        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_TEMP", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        )

        val f = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + File.separator + "IMAGE_TEMP.jpg")
        if (!f.exists()) f.parentFile.mkdirs()
        return f
    }

    fun getUriImageFileFromCamera(context: Context): Uri {
        return  FileProvider.getUriForFile(context, "com.vit.demomlkit.provider", createImageFile(context))
    }
}