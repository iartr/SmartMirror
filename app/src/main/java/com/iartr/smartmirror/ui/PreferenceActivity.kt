package com.iartr.smartmirror.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.iartr.smartmirror.R
import kotlin.system.exitProcess

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<SwitchPreference>("pref_camera_is_active")
                ?.setOnPreferenceChangeListener { preference, newValue ->
                    showRestartAppDialog()
                    true
                }
        }

        private fun showRestartAppDialog() {
            val listener = DialogInterface.OnClickListener { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss()
                } else {
                    killProcess()
                }
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Настройки изменены")
                .setMessage("Настройки будут применены при следующем запуске")
                .setPositiveButton("OK", listener)
                .setNegativeButton("Kill app", listener)
                .show()
        }

        private fun killProcess() {
            try {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            } catch (internalEx: Throwable) { /* no op */
            }
        }
    }
}