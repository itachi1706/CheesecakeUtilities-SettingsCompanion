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
                Log.i(TAG, "Received check request, pinging back $random to app")
                sendReplyBroadcast(true, "Ping Check: ID $random", context)
            }
            ACTION_CHANGE -> {
                Log.i(TAG, "Received Settings Change Request")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) { Log.e(TAG, "Invalid Android Version. Ignoring"); return }
                if (!Settings.System.canWrite(context)) {
                    Log.e(TAG, "WRITE_SETTINGS permission not granted. Please grant it")
                    sendReplyBroadcast(false, "Permission not granted", context)
                    return
                }

                if (!(intent.extras != null && intent.hasExtra(DATA_SETTING_NAME) && intent.hasExtra(DATA_SETTING_TYPE) && intent.hasExtra(DATA_SETTING_VAL))) {
                    Log.e(TAG, "Required data not available")
                    sendReplyBroadcast(false, "Please send all data required for CHANGE_SETTING", context)
                    return
                }

                if (intent.extras!!.getInt(DATA_SETTING_TYPE, DATA_CONST_GLOBAL) != DATA_CONST_SYSTEM) {
                    Log.e(TAG, "Unsupported Setting Change")
                    sendReplyBroadcast(false, "Change Code not supported for now", context)
                    return
                }

                val name = intent.extras!!.getString(DATA_SETTING_NAME)
                val value = intent.extras!!.getString(DATA_SETTING_VAL)
                val origValue = Settings.System.getString(context.contentResolver, name)
                Log.i(TAG, "Changing System Setting $name to $value from $origValue")
                val result = Settings.System.putString(context.contentResolver, name, value)
                sendReplyBroadcast(result, "Changed Setting $name to $value (formerly $origValue)", context)
            }
        }
    }

    private fun sendReplyBroadcast(result: Boolean, msg: String, context: Context) {
        val error = Intent().apply {
            action = ACTION_REPLY
            putExtra(DATA_RESULT, result)
            putExtra(DATA_EXTRA_DATA, msg)
        }
        context.sendBroadcast(error)
    }

    companion object {
        private const val TAG = "SettingsChange"
        private const val ACTION_CHECK = "com.itachi1706.cheesecakeutilitiessettingscompanion.CHECK"
        private const val ACTION_CHANGE = "com.itachi1706.cheesecakeutilitiessettingscompanion.CHANGE_SETTING"
        private const val ACTION_REPLY = "com.itachi1706.cheesecakeutilitiessettingscompanion.REPLY" // Other app must listen to this
        private const val DATA_RESULT = "result"
        private const val DATA_EXTRA_DATA = "extradata"
        private const val DATA_SETTING_NAME = "settingname"
        private const val DATA_SETTING_TYPE = "settingtype" // 0 - Global, 1 - Secure, 2 - System
        private const val DATA_SETTING_VAL = "settingval"
        private const val DATA_CONST_GLOBAL = 0
        private const val DATA_CONST_SECURE = 1
        private const val DATA_CONST_SYSTEM = 2
    }
}
