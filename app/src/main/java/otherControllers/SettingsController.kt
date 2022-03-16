package otherControllers

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import java.util.*

/**
 * Settings controller
 * Class which is responsible for managing settings
 * @constructor Create empty Settings controller
 */
class SettingsController {
    /**
     * Load settings
     * Method loads settings. It is to be invoked by all activities
     * @param appContext current activity instance
     * @param res activity resources
     */
    fun loadSettings(appContext: Context, res: Resources) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        val theme: String? = sharedPreferences.getString("appThemeMode", "1")
        val language: String? = sharedPreferences.getString("appLanguage", "default")

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
        //load chosen language
        if (language != null) {
            setLocale(language, res)
        }
    }

    /**
     * Set locale
     * Method changes app language according to what user choose
     * @param languageName what language to choose app language
     * @param res activity resources
     */
    private fun setLocale(languageName: String, res: Resources) {
        Locale(languageName)
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(languageName))
        res.updateConfiguration(conf, dm)
    }
}
