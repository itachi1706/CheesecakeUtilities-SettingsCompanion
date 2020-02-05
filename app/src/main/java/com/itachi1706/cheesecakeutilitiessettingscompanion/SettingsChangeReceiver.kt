package com.itachi1706.cheesecakeutilitiessettingscompanion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.*

class SettingsChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action == null) {
            Log.e(TAG, "No Actions in Intent received. Not continuing")
            return
        } // No Action

        when (intent.action) {
            ACTION_CHECK -> {
                val random = Random().nextLong()
                val reply = Intent().apply {
                    action = ACTION_REPLY
                    putExtra(DATA_RESULT, true)
                    putExtra(DATA_EXTRA_DATA, "Ping Check: ID $random")
                }
                Log.i(TAG, "Received check request, pinging back $random to app")
                context.sendBroadcast(reply)
            }
            ACTION_CHANGE -> {
                Log.i(TAG, "Received Settings Change Request")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) { Log.e(TAG, "Invalid Android Version. Ignoring"); return }
                if (!Settings.System.canWrite(context)) {
                    Log.e(TAG, "WRITE_SETTINGS permission not granted. Please grant it");
                    val errorIntent = Intent().apply {
                        action = ACTION_REPLY
                        putExtra(DATA_RESULT, false)
                        putExtra(DATA_EXTRA_DATA, "Permission not granted")
                    }
                    context.sendBroadcast(errorIntent)
                    return
                }
            }
        }
    }

    companion object {
        private const val TAG = "SettingsChange"
        private const val ACTION_CHECK = "com.itachi1706.cheesecakeutilitiessettingscompanion.CHECK"
        private const val ACTION_CHANGE = "com.itachi1706.cheesecakeutilitiessettingscompanion.CHANGE_SETTING"
        private const val ACTION_REPLY = "com.itachi1706.cheesecakeutilitiessettingscompanion.REPLY" // Other app must listen to this
        private const val DATA_RESULT = "result"
        private const val DATA_EXTRA_DATA = "extradata"
    }
}
