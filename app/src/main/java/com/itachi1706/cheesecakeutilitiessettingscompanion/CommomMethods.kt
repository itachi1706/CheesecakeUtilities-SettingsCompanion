package com.itachi1706.cheesecakeutilitiessettingscompanion

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * Created by Kenneth on 7/2/2020.
 * for com.itachi1706.cheesecakeutilitiessettingscompanion in Cheesecake Utilities Settings Companion
 */
object CommomMethods {

    var version = "???"
    var versionCode: Long = 0

    @Suppress("DEPRECATION")
    private fun getAppVersion(context: Context) {
        try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = pInfo.versionName
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode else pInfo.versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getVersion(context: Context): String {
        if (version == "???") getAppVersion(context)
        return version
    }

    @JvmStatic
    fun getVersionCode(context: Context): Long {
        if (versionCode <= 0) getAppVersion(context)
        return versionCode
    }
}