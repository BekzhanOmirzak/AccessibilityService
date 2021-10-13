package com.example.accessibilityversion_2.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object Util {

    private val TAG = javaClass.name.toString();

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun permissionToReadAndCreateFile(activity: Activity) {

        PERMISSIONS_STORAGE.forEach { permission ->
            val checked_permission = ActivityCompat.checkSelfPermission(
                activity,
                permission
            )
            if (checked_permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    1
                );
            }
        }


    }


}


/*
if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            Toast.makeText(this, "${event.text}", Toast.LENGTH_SHORT).show()
            Log.e(TAG, " Text :  ${event.text}")
        } else if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val first_text = event.text[event.text.size - 1].toString();
            current = if (first_text != "Сообщение") {
                val letter = first_text[first_text.length - 1];
                if (current < first_text.length)
                    Toast.makeText(this, "Буква : $letter", Toast.LENGTH_SHORT).show()
                first_text.length;
            } else
                0;
        }
*/

/*
private fun listenToClickEveryTime(event: AccessibilityEvent) {
        try {
            if (rootInActiveWindow == null)
                return

            val rootInActiveWindow = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow);

            Log.e(TAG, "performClickEveryTime: InitialViews $rootInActiveWindow")

            val sendMessageNodeInfoList =
                rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");

            if (sendMessageNodeInfoList == null || sendMessageNodeInfoList.isEmpty())
                return

            for (k in sendMessageNodeInfoList) {
                Log.e(TAG, "onAccessibilityEvent: Views : ${k}")
            }

            val sendMessageButton = sendMessageNodeInfoList.get(0);

            if (!sendMessageButton.isVisibleToUser)
                return

            //sendMessageButton    I need to listen to clicks to this view

//            sendMessageButton.performAction(AccessibilityNodeInfo.ACTION_CLICK); This clicks the imageButton automatically.

        } catch (ex: Exception) {
            Log.e(TAG, "onAccessibilityEvent: ", ex)
        }
    }
 */