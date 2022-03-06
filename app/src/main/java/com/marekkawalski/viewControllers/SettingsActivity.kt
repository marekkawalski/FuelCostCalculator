package com.marekkawalski.viewControllers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.marekkawalski.fuelcostcalculator.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.app_full_name)

        // Get the preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Get the user dark theme settings
        val isDarkTheme = prefs.getBoolean("darkMode", false)


        // Load the settings fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, SettingsFragment())
            .commit()

    }
}
