package com.phrase.android.wx.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.phrase.android.wx.WXApp
import java.util.*

class LocaleUtils {

    companion object {
        fun getDisplayLanguage(languageTag: String?, displayLocale: Locale? = null): String {
            val builder = StringBuilder()
            if (languageTag != null && !languageTag.isEmpty()) {
                val locale = Locale.forLanguageTag(languageTag)
                if (displayLocale == null) {
                    builder.append(locale.displayLanguage)
                } else {
                    builder.append(locale.getDisplayLanguage(displayLocale))
                }
                if (locale.country != null && !locale.country.isEmpty()) {
                    builder.append(", ")
                    if (displayLocale == null) {
                        builder.append(locale.displayCountry)
                    } else {
                        builder.append(locale.getDisplayCountry(displayLocale))
                    }
                }
            }
            return toSentenceCase(builder.toString())
        }

        fun setLocale(context: Context?): Context? {
            if (context != null) {
                val config = Configuration(context.resources.configuration)

                val languageTag = WXApp.getCustomLocale()
                val locale = if (languageTag == null) {
                    Locale.getDefault()
                } else {
                    Locale.forLanguageTag(languageTag)
                }
                Locale.setDefault(locale)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    config.setLocale(locale)
                    config.setLayoutDirection(locale)
                } else {
                    config.locale = locale;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return context.createConfigurationContext(config)
                } else {
                    val resources = context.resources
                    @Suppress("DEPRECATION")
                    resources?.updateConfiguration(config, resources.displayMetrics)
                    return context
                }
            }
            return null
        }

        private fun toSentenceCase(content: String): String {
            if (content.isEmpty()) {
                return content
            }
            val first = content.offsetByCodePoints(0, 1)
            return content.substring(0, first)
                .toUpperCase(Locale.getDefault()) + content.substring(first)
        }
    }
}
