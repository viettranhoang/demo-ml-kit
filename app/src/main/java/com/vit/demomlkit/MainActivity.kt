package com.vit.demomlkit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Pair
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.vit.demomlkit.codelab.TextGraphic
import com.vit.demomlkit.utils.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val textElements = ArrayList<FirebaseVisionText.Element>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        initView()
    }

    private fun initView() {
        buttonLibrary.setOnClickListener { IntentUtils.pickImageFromGallery(this) }
        buttonCamera.setOnClickListener { IntentUtils.takePhoto(this) }

        graphicOverlay.setListener(object : OnClickGraphicListener {
            override fun onClickElement(position: Int) {
                Toast.makeText(
                    this@MainActivity,
                    textElements[position].text.currencyFormat(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val uriImage = when (requestCode) {
                IntentUtils.RC_GALLERY -> data!!.data
                IntentUtils.RC_CAMERA -> IntentUtils.getUriImageFileFromCamera(this)
                else -> null
            }.let {
                val bitmap = scaleBitmap(ImageUtils.getBitmap(it!!, this))
                image.setImageResource(0)
                image.setImageBitmap(bitmap)
                recognizeText(bitmap)
            }
        }
    }


    private fun recognizeText(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        FirebaseVision.getInstance()
            .onDeviceTextRecognizer.processImage(image).addOnSuccessListener {
            var text = ""
            it.textBlocks.forEach { block -> text += "${block.text}\n" }
            inputResult.setText(text)

            processTextRecognitionResult(it)
        }
    }

    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            return
        }
        graphicOverlay.clear()
        textElements.clear()
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (element in elements) {
                    if (element.text.isCurrency()) {
                        textElements.add(element)
                        val textGraphic = TextGraphic(graphicOverlay, element.boundingBox, element)
                        graphicOverlay.add(textGraphic)
                    }
                }
            }
        }
        Toast.makeText(this, "Tổng: ${textElements.getTotalPay()}", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ACTION)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) image.isEnabled = true
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_ACTION
            )
        }
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        // Get the dimensions of the View
        val targetedSize = getTargetedWidthHeight()

        val targetWidth = targetedSize.first
        val maxHeight = targetedSize.second

        // Determine how much to scale down the image
        val scaleFactor = Math.max(
            bitmap.width.toFloat() / targetWidth.toFloat(),
            bitmap.height.toFloat() / maxHeight.toFloat()
        )

        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width / scaleFactor).toInt(),
            (bitmap.height / scaleFactor).toInt(),
            true
        )
    }

    // Gets the targeted width / height.
    private fun getTargetedWidthHeight(): Pair<Int, Int> {
        val targetWidth: Int
        val targetHeight: Int
        val maxWidthForPortraitMode = image.width
        val maxHeightForPortraitMode = image.height
        targetWidth = maxWidthForPortraitMode
        targetHeight = maxHeightForPortraitMode
        return Pair(targetWidth, targetHeight)
    }

    companion object {
        private val PERMISSION_ACTION = 2002
    }
}
