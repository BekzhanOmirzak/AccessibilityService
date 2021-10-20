package com.example.accessibilityversion_2.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.accessibilityversion_2.R
import java.io.File

class SoundRecorderService : Service() {


    private var mediaRecorder: MediaRecorder? = null;
    private val CHANNEL_ID = "First Channel";
    private val NOTIFICATION_ID = 101;

    companion object {
        private const val TAG = "SoundRecorderService"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val message = intent.extras?.getString("message");

        if (message == "start") {
            createNotification();
            startAudioRecorder();
        } else if (message == "stop") {
            stopRecording();
        }
        return START_STICKY;
    }


    private fun createNotification() {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Аудио запись channel",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Запись в процессе")
            .setContentText("Будьте осторожны, ваш голос записывается")
            .setSmallIcon(R.drawable.ic_settings)
            .setDefaults(Notification.DEFAULT_SOUND)
            .build();

        startForeground(NOTIFICATION_ID, notification);

    }

    private fun startAudioRecorder() {
        mediaRecorder = MediaRecorder().apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.MIC);
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            setOutputFile(getFilePath())
            setMaxDuration(60 * 1000 * 5)
            setOnInfoListener { mr, what, extra ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.e(TAG, "startAudioRecorder: Max Duration has been reached : ")
                    stopRecording()
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
        var fileName =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath;
        fileName += File.separator + "AUD${System.currentTimeMillis()}.amr"
        return fileName;
    }

    private fun stopRecording() {

        try {
            mediaRecorder?.let {
                it.stop();
                it.release();
            }
        } catch (ex: Exception) {
            Log.e(TAG, "stopRecording: Exception has been thrown :", ex)
            ex.printStackTrace();
        }
    }

    override fun onBind(intent: Intent?) = null;


    override fun onDestroy() {
        super.onDestroy()
        stopRecording();
    }

}