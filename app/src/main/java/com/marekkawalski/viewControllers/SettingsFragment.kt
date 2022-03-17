package com.marekkawalski.viewControllers

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.marekkawalski.fuelcostcalculator.R

/**
 * Settings fragment
 * @author Marek Kawalski
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}