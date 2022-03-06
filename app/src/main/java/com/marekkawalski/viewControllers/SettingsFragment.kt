package com.marekkawalski.viewControllers

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.marekkawalski.fuelcostcalculator.R


class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        // Get the switch preference
        val switchDarkMode: SwitchPreferenceCompat? = findPreference("darkMode")

        // Switch preference change listener
        switchDarkMode?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        TODO("Not yet implemented")
    }

}