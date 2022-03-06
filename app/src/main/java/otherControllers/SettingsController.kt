package otherControllers

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class SettingsController {

    fun loadSettings(appContext: Context) {

        var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        var nightModeEnabled: Boolean = sharedPreferences.getBoolean("darkMode", false)

        if (nightModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}