package com.example.accessibilityversion_2.ui

import android.Manifest
import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.accessibilityversion_2.BuildConfig
import com.example.accessibilityversion_2.databinding.ActivityMainBinding
import com.example.accessibilityversion_2.util.TempStorage
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;


    private val PERMISSIONS = arrayOf(
        WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE,
        RECORD_AUDIO
    )

    companion object {
        private const val TAG = "MainActivity"
    }

    private var mediaRecorder: MediaRecorder? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        TempStorage.initSharedPreferences(this);
        if (TempStorage.getValue())
            audioRecordingRunning(true);
        else
            audioRecordingRunning(false)

        checkPermissions();
        checkStorageAccessPermissionForAndroidR();

        binding.btnAccessibility.setOnClickListener {
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).also {
                startActivity(it)
            }
        }


        binding.btnStart.setOnClickListener {
            startAudioRecorder();
        }

        binding.btnStop.setOnClickListener {
            stopRecording();
            audioRecordingRunning(false)
        }


    }


    private fun checkPermissions() {
        for (k in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, k) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
        }
    }


    private fun getFilePath(): String {
        var fileName =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath;
        fileName += File.separator + "AUD${System.currentTimeMillis()}.amr"
        return fileName;
    }

    private fun checkStorageAccessPermissionForAndroidR() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        }
    }

    private fun startAudioRecorder() {
        mediaRecorder = MediaRecorder().apply {
            reset()
            setAudioSource(MediaRecorder.AudioSource.MIC);
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            setOutputFile(getFilePath())
            setMaxDuration(5 * 1000)
            setOnInfoListener { mr, what, extra ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.e(TAG, "startAudioRecorder: Max Duration has been reached : ")
                    audioRecordingRunning(false);
                    stopRecording()
                }
            }
            try {
                prepare()
                start();
                audioRecordingRunning(true);
            } catch (ex: Exception) {
                Log.e(TAG, "startAudioRecorder: Exception : ", ex)
                ex.printStackTrace();
            }
        }
    }


    private fun audioRecordingRunning(running: Boolean) {
        TempStorage.saveValue(running);
        if (running) {
            binding.btnStart.isEnabled = false;
            binding.btnStop.isEnabled = true;
        } else {
            binding.btnStart.isEnabled = true;
            binding.btnStop.isEnabled = false;
        }
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


    override fun onDestroy() {
        super.onDestroy()
        stopRecording();
    }


}