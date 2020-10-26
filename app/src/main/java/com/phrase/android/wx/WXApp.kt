package com.phrase.android.wx

import android.app.Application
import android.content.Context
import android.icu.util.MeasureUnit
import java.util.*

class WXApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = getApplicationContext()
        saveDefaultLocale()
    }

    companion object {
        val DEFAULT_UNITS = listOf(MeasureUnit.KILOGRAM, MeasureUnit.POUND)
        private val KEY_PREFERENCES = "preferences"
        private val KEY_FIRST_LAUNCH = "first_launch"
        private val KEY_CUSTOM_LOCALE = "custom_locale"
        private val KEY_DEFAULT_LOCALE = "default_locale"
        private val KEY_UNIT = "unit"
        private val DEFAULT_UNIT = 0 // kilogram (default unit)

        private var context: Context? = null

        val appContext: Context?
            get() = context

        fun isFirstLaunch(): Boolean {
            val sharedPref =
                appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
            val isFirstLaunch = sharedPref.getBoolean(KEY_FIRST_LAUNCH, true)
            if (isFirstLaunch) {
                with(sharedPref.edit()) {
                    putBoolean(KEY_FIRST_LAUNCH, false)
                    apply()
                }
            }
            return isFirstLaunch
        }

        fun getCustomLocale(): String? {
            if (appContext != null) {
                val sharedPref =
                    appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
                return sharedPref.getString(KEY_CUSTOM_LOCALE, null)
            }
            return null
        }

        fun getLocale(): Locale {
            val customLocale = getCustomLocale()
            if (customLocale == null) {
                return Locale.getDefault()
            } else {
                return Locale.forLanguageTag(customLocale)
            }
        }

        fun setCustomLocale(languageTag: String) {
            val sharedPref =
                appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(KEY_CUSTOM_LOCALE, languageTag)
                apply()
            }
        }

        fun removeCustomLocale(): Boolean {
            val sharedPref =
                appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
            return with(sharedPref.edit()) {
                remove(KEY_CUSTOM_LOCALE)
                commit()
            }
        }

        fun getDefaultLocale(): Locale {
            if (appContext != null) {
                val sharedPref =
                    appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
                val languageTag = sharedPref.getString(KEY_DEFAULT_LOCALE, null)
                if (languageTag != null) {
                    return Locale.forLanguageTag(languageTag)
                }
            }
            return Locale.ROOT
        }

        fun saveDefaultLocale() {
            val sharedPref =
                appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(KEY_DEFAULT_LOCALE, Locale.getDefault().toLanguageTag())
                apply()
            }
        }

        fun getCustomUnit(): Int {
            if (appContext != null) {
                val sharedPref =
                    appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
                return sharedPref.getInt(KEY_UNIT, DEFAULT_UNIT)
            }
            return DEFAULT_UNIT
        }

        fun setCustomUnit(unit: Int) {
            val sharedPref =
                appContext!!.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt(KEY_UNIT, unit)
                apply()
            }
        }

        fun getUnit(): MeasureUnit {
            val customUnit = getCustomUnit()
            return DEFAULT_UNITS.get(customUnit)
        }
    }
}