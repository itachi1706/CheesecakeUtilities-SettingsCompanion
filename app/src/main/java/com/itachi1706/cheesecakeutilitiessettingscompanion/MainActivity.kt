package com.itachi1706.cheesecakeutilitiessettingscompanion

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.itachi1706.cheesecakeutilitiessettingscompanion.CommomMethods.getVersion
import com.itachi1706.cheesecakeutilitiessettingscompanion.CommomMethods.getVersionCode
import com.itachi1706.cheesecakeutilitiessettingscompanion.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appVersion.text = getString(R.string.app_version, getVersion(this), getVersionCode(this))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            AlertDialog.Builder(this).setTitle("App incompatible with this version")
                .setMessage("This app is designed to be a companion of the CheesecakeUtilities application. However, " +
                        "the utility that requires this companion app requires your Android Version to be at least Android 9.0 Pie")
                .setCancelable(false)
                .setPositiveButton(R.string.uninstall_app) {_,_ ->
                    val uninstallIntent = Intent().apply {
                        action = Intent.ACTION_UNINSTALL_PACKAGE
                        data = Uri.fromParts("package", packageName, null)
                    }
                    Log.v("Companion", "Attempting to uninstall app as it is unsupported")
                    startActivity(uninstallIntent)
                }.show()
            return
        }

        binding.grantButton.setOnClickListener {
            Toast.makeText(this, "Granting ability to write settings", Toast.LENGTH_SHORT).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName")))
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return // NO-OP

        val isAllowed = Settings.System.canWrite(this)
        binding.grantStatus.text = if (isAllowed) "Granted" else "Not Granted"
        binding.grantStatus.setTextColor(ContextCompat.getColor(this, if (isAllowed) R.color.green else R.color.red))
        binding.grantButton.isEnabled = !isAllowed
    }
}
