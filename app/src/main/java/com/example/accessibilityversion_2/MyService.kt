package com.example.accessibilityversion_2

import android.accessibilityservice.AccessibilityService
import android.media.AudioManager
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import java.lang.Exception

class MyService : AccessibilityService() {

    private val TAG = "MyService"

    override fun onServiceConnected() {
        Log.e(TAG, "onServiceConnected: Service is connected...")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.e(TAG, "onAccessibilityEvent: Event  : $event")
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Toast.makeText(this, "${event.text}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, " Text :  ${event.text}")
        } else if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            if (event.text.size != 0) {
                val first_text = event.text[event.text.size - 1].toString();
                try {
                    val letter = first_text[first_text.length - 1];
                    Toast.makeText(this, "Буква : $letter", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {

                }
            }
        }


    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action;
        val code = event.keyCode;
        Log.e(TAG, "onKeyEvent: Action   : $action")
        Log.e(TAG, "onKeyEvent: Key_Code : $code")
        if (action == KeyEvent.ACTION_UP) {
            when (code) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    Toast.makeText(this, "Уменшение грамкость", Toast.LENGTH_SHORT).show()
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    Toast.makeText(this, "Увеличение грамкость", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true;
    }

    override fun onInterrupt() {

    }


}