package com.vit.demomlkit

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel
import com.google.firebase.ml.custom.FirebaseModelInterpreter
import com.google.firebase.ml.custom.FirebaseModelOptions

class CustomModelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_model)


        initCustomModel()
    }

    private fun initCustomModel() {

        //remote model
        var conditionsBuilder: FirebaseModelDownloadConditions.Builder =
            FirebaseModelDownloadConditions.Builder().requireWifi()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Enable advanced conditions on Android Nougat and newer.
            conditionsBuilder = conditionsBuilder
                .requireCharging()
                .requireDeviceIdle()
        }
        val conditions = conditionsBuilder.build()


        val remoteSource = FirebaseRemoteModel.Builder(HOSTED_MODEL_NAME)
            .enableModelUpdates(true)
            .setInitialDownloadConditions(conditions)
            .setUpdatesDownloadConditions(conditions)
            .build()
        FirebaseModelManager.getInstance().registerRemoteModel(remoteSource)


        //local model
        val localSource = FirebaseLocalModel.Builder(LOCAL_MODEL_NAME)
            .setAssetFilePath(LOCAL_MODEL_PATH)
            .build()
        FirebaseModelManager.getInstance().registerLocalModel(localSource)

        //
        val options = FirebaseModelOptions.Builder()
            .setRemoteModelName(HOSTED_MODEL_NAME)
            .setLocalModelName(LOCAL_MODEL_NAME)
            .build()
        val interpreter = FirebaseModelInterpreter.getInstance(options)

    }

    companion object{
        const val HOSTED_MODEL_NAME = "umbala_detect"
        const val LOCAL_MODEL_PATH = "umbala-detect.tflite"
        const val LOCAL_MODEL_NAME = "asset"
    }
}
