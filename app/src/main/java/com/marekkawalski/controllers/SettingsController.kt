package com.marekkawalski.controllers

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

/**
 * Class which is responsible for managing settings
 * @constructor Create empty Settings controller
 */
class SettingsController {
    /**
     * Load settings
     * Method loads settings. It is to be invoked by all activities
     * @param appContext current activity instance
     */
    fun loadSettings(appContext: Context) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        val theme: String? = sharedPreferences.getString("appThemeMode", "1")

        //load chosen theme
        when (theme?.toInt()) {
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
    }
}
