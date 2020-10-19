package com.phrase.android.wx.activity

import android.os.Bundle
import com.phrase.android.wx.R
import kotlinx.android.synthetic.main.welcome_activity.*

class WelcomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        got_it.setOnClickListener {
            finish()
        }
    }
}