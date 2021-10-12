package com.example.accessibilityversion_2

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

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
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action;
        val code = event.keyCode;
        Log.e(TAG, "onKeyEvent: Action :  $action")
        Log.e(TAG, "onKeyEvent: Key_Code : $code")
//        if (action == KeyEvent.ACTION_UP) {
//            if(code==)
//        }
        return true;
    }

    override fun onInterrupt() {

    }


}