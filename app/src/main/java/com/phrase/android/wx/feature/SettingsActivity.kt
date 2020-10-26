package com.phrase.android.wx.feature

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.icu.text.MeasureFormat
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.view.MenuItem
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.preference.*
import com.phrase.android.wx.R
import com.phrase.android.wx.WXApp
import com.phrase.android.wx.feature.main.MainActivity
import com.phrase.android.wx.feature.record.RecordActivity
import com.phrase.android.wx.utils.LocaleUtils
import com.phrase.android.wx.widget.BaseActivity
import java.util.*


class SettingsActivity : BaseActivity() {

    companion object {
        const val PREF_SWITCH_LANGUAGE = "custom_language_switch"
        const val PREF_DROPDOWN_CUSTOM_LANGUAGE = "custom_language"
        const val PREF_LIST_UNIT = "custom_unit"

        fun launch(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }

    fun restart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity()
            MainActivity.launch(this)
        } else {
            finishAffinity(this)
            MainActivity.launch(this)
        }
        // Alternative code
//        val packageManager = context?.packageManager
//        val intent =
//            context?.packageName?.let { packageManager!!.getLaunchIntentForPackage(it) }
//        val componentName = intent!!.component
//        val mainIntent: Intent = makeRestartActivityTask(componentName)
//        context?.startActivity(mainIntent)
//        System.exit(0)
    }


    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            setPreferencesFromResource(R.xml.settings_root_preferences, rootKey)
            val switchLanguage = findPreference<SwitchPreferenceCompat>(PREF_SWITCH_LANGUAGE)
            val dropDownLanguage = findPreference<DropDownPreference>(PREF_DROPDOWN_CUSTOM_LANGUAGE)
            val listPreference = findPreference<ListPreference>(PREF_LIST_UNIT)

            val languagesValues = resources.getStringArray(R.array.language_entries_values)
            val languageEntries = arrayOfNulls<String>(languagesValues.size)
            for (i in languageEntries.indices) {
                languageEntries[i] =
                    LocaleUtils.getDisplayLanguage(languagesValues[i], Locale.getDefault())
            }
            dropDownLanguage!!.setEntries(languageEntries)
            dropDownLanguage.setEntryValues(R.array.language_entries_values)

            val mf = MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.WIDE)
            val unitValues = arrayOfNulls<String>(WXApp.DEFAULT_UNITS.size)
            val unitEntries = mutableListOf<Int>(unitValues.size)
            for (i in unitValues.indices) {
                unitValues[i] = mf.getUnitDisplayName(WXApp.DEFAULT_UNITS[i])
                // Output: kilograms (en-US) | kilograms (en-GB), kilogramos (es-419), kilogramos (es)
                // Output: pounds (en-US) | pounds (en-GB), libras (es-419), libras (es)
                unitEntries.add(i)
            }

            listPreference!!.entries = unitValues
            listPreference.setEntryValues(unitValues)
            val defaultUnit = WXApp.getCustomUnit()
            listPreference.value = unitValues.get(defaultUnit)

            val customLocale = WXApp.getCustomLocale()
            val defaultLocale = WXApp.getDefaultLocale().toLanguageTag()
            if (customLocale != null && !customLocale.equals(defaultLocale)) {
                dropDownLanguage.value = customLocale
                switchLanguage!!.isChecked = true
            } else {
                dropDownLanguage.value = null
                switchLanguage!!.isChecked = false
            }
            dropDownLanguage.isVisible = switchLanguage.isChecked

            dropDownLanguage.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val lastStatus = (preference as DropDownPreference).value
                    if (lastStatus != null && !newValue.toString()
                            .equals(Locale.getDefault().toLanguageTag())
                    ) {
                        shouldRestartApp(newValue.toString(),
                            {
                                WXApp.setCustomLocale(newValue.toString())
                                (activity as SettingsActivity).restart()
                            },
                            {
                                dropDownLanguage.value = lastStatus
                            })
                    }
                    true
                }

            switchLanguage.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val result = newValue as Boolean
                    switchLanguage.isChecked = result
                    if (result) {
                        dropDownLanguage.isVisible = true
                    } else {
                        val customLocale = WXApp.getCustomLocale()
                        val defaultLocale = WXApp.getDefaultLocale()
                            .toLanguageTag() //Locale.getDefault().toLanguageTag()
                        if (customLocale != null && !customLocale.equals(defaultLocale)) {
                            shouldRestartApp(null, {
                                WXApp.removeCustomLocale()
                                (activity as SettingsActivity).restart()
                            }, {
                                switchLanguage.isChecked = !result
                            })
                        }
                        dropDownLanguage.isVisible = switchLanguage.isChecked
                    }
                    true
                }

            listPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val index = unitValues.indexOf(newValue.toString())
                    WXApp.setCustomUnit(index)
                    true
                }
        }

        private fun shouldRestartApp(
            languageTag: String?,
            posFunction: () -> Unit,
            negFunction: () -> Unit
        ) {
            val builder = AlertDialog.Builder(context)

            val localeDisplayName = if (languageTag == null) {
                LocaleUtils.getDisplayLanguage(WXApp.getDefaultLocale().toLanguageTag())
            } else {
                LocaleUtils.getDisplayLanguage(languageTag)
            }

            val text: String = getString(R.string.dialog_restart_app, localeDisplayName)
            val styledText: Spanned = Html.fromHtml(text, FROM_HTML_MODE_LEGACY)
            builder.setMessage(styledText)
                .setPositiveButton(R.string.yes) { dialog, id ->
                    posFunction()
                }
                .setNegativeButton(R.string.no) { dialog, id ->
                    negFunction()
                }
            builder.setCancelable(false)
            builder.create().show();
        }
    }
}