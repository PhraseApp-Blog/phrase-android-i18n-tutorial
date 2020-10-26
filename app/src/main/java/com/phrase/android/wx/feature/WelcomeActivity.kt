package com.phrase.android.wx.feature

import android.os.Bundle
import com.phrase.android.wx.R
import com.phrase.android.wx.widget.BaseActivity
import kotlinx.android.synthetic.main.welcome_activity.*

class WelcomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        got_it.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        // Do nothing
    }
}