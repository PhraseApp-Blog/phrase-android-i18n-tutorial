package com.phrase.android.wx.widget

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.phrase.android.wx.utils.LocaleUtils

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleUtils.setLocale(base))
    }
}