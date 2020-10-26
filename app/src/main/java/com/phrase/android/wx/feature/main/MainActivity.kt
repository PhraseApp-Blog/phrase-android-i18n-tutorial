package com.phrase.android.wx.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.phrase.android.wx.R
import com.phrase.android.wx.WXApp
import com.phrase.android.wx.core.WorkoutRepository
import com.phrase.android.wx.feature.SettingsActivity
import com.phrase.android.wx.feature.WelcomeActivity
import com.phrase.android.wx.feature.record.RecordActivity
import com.phrase.android.wx.widget.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    private var fab: FloatingActionButton? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (WXApp.isFirstLaunch()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            WorkoutRepository.getInstance(this).loadInitialData()
        }
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = MainAdapter(this)
        }

        val itemCount = recyclerView?.adapter?.itemCount
        if (itemCount != null) {
            summary.text =
                resources.getQuantityString(R.plurals.workout_quantity, itemCount, itemCount)
        } else {
            summary.text = resources.getQuantityString(R.plurals.workout_quantity, 0, 0)
        }

        fab = findViewById(R.id.fab)
        fab?.setOnClickListener {
            RecordActivity.launch(this)
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView?.also {
            it.adapter = MainAdapter(this)
            it.invalidate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> SettingsActivity.launch(this)
        }
        return super.onOptionsItemSelected(item)
    }
}