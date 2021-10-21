package com.example.accessibilityversion_2.ui

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.accessibilityversion_2.BuildConfig
import com.example.accessibilityversion_2.databinding.ActivityMainBinding
import com.example.accessibilityversion_2.util.TempStorage

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        checkPermissions();
        checkStorageAccessPermissionForAndroidR();

        binding.btnAccessibility.setOnClickListener {
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).also {
                startActivity(it)
            }
        }


    }


    private fun checkPermissions() {
        for (k in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, k) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
        }
    }


    private fun checkStorageAccessPermissionForAndroidR() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val uri: Uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}