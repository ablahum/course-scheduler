package com.dicoding.courseschedule.ui.setting

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder
import com.dicoding.courseschedule.util.NightMode

class SettingsFragment : PreferenceFragmentCompat() {

    private val TAG = "SettingsFragment"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>(getString(R.string.pref_key_dark))?.setOnPreferenceChangeListener { _, newValue ->
            handleDarkModeChange(newValue.toString())
            true
        }

        val switchPreference =
            findPreference<SwitchPreference>(getString(R.string.pref_key_notify))

        switchPreference?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                requestNotificationPermission()
            } else {
                handleDailyReminderChange(false)
                showToast("Daily reminder disabled.")
            }
            true
        }
    }

    private fun requestNotificationPermission() {
        Log.d(TAG, "Requesting notification permission")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission not granted. Requesting permission...")

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.d(TAG, "Permission already granted. Enabling daily reminder.")
                handleDailyReminderChange(true)
                showToast("Daily reminder enabled.")
            }
        } else {
            Log.d(TAG, "Device doesn't need notification permission. Enabling daily reminder.")
            handleDailyReminderChange(true)
            showToast("Daily reminder enabled.")
        }
    }

    private fun handleDarkModeChange(newValue: String) {
        val selectedTheme = when (newValue) {
            getString(R.string.pref_dark_auto) -> NightMode.AUTO.value
            getString(R.string.pref_dark_on) -> NightMode.ON.value
            getString(R.string.pref_dark_off) -> NightMode.OFF.value
            else -> AppCompatDelegate.getDefaultNightMode()
        }

        if (selectedTheme != AppCompatDelegate.getDefaultNightMode()) {
            Log.d(TAG, "Selected theme: $selectedTheme")
            updateTheme(selectedTheme)
            showToast("Dark mode changed.")
        }
    }

    private fun handleDailyReminderChange(isEnabled: Boolean) {
        DailyReminder.setDailyReminder(requireContext(), isEnabled)
    }

    private fun updateTheme(mode: Int) {
        if (isAdded) {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "Notification permission granted. Enabling daily reminder.")
                handleDailyReminderChange(true)
                showToast("Daily reminder enabled.")
            } else {
                Log.d(TAG, "Notification permission denied. Disabling daily reminder.")
                handleDailyReminderChange(false)
                showToast("Daily reminder disabled.")
            }
        }
}
