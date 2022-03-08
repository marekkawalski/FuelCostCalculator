package otherControllers

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import java.util.*

class SettingsController {
    fun loadSettings(appContext: Context, res: Resources) {

        var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        var theme: String? = sharedPreferences.getString("appThemeMode", "")
        var language: String? = sharedPreferences.getString("appLanguage", "")

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
        if (language != null) {
            setLocale(language, res)
        }
    }

    private fun setLocale(languageName: String, res: Resources) {
        Locale(languageName)
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(Locale(languageName))
        res.updateConfiguration(conf, dm)
    }
}
