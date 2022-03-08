package com.marekkawalski.viewControllers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.marekkawalski.fuelcostcalculator.R
import java.util.*

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.app_full_name)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, SettingsFragment())
                .commit()
        }
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

    }

    override fun onSharedPreferenceChanged(
        sharedPreference: SharedPreferences?,
        key: String?
    ) {
        if (key == "appThemeMode") {
            val prefs = sharedPreference?.getString(key, "1")
            when (prefs?.toInt()) {
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                }
                3 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                }
                4 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        } else if (key == "appLanguage") {

            when (sharedPreference?.getString(key, "default")) {
                "default" -> {

                    setLocale("en", resources)
                }
                "en" -> {

                    setLocale("en", resources)
                }
                "pl" -> {

                    setLocale("pl", resources)
                }
            }

            val intent = Intent(this, MainActivity::class.java)
// set the new task and clear flags
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setLocale(languageName: String, res: Resources) {
        Locale(languageName)
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(languageName))
        res.updateConfiguration(conf, dm)
    }
}
