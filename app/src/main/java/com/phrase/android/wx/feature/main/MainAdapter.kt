package com.phrase.android.wx.feature.main

import android.content.Context
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.phrase.android.wx.R
import com.phrase.android.wx.WXApp
import com.phrase.android.wx.core.WorkoutItem
import com.phrase.android.wx.core.WorkoutRepository
import com.phrase.android.wx.utils.DateTimeUtils
import kotlinx.android.synthetic.main.grid_workout.view.*
import kotlinx.android.synthetic.main.item_workout.view.*
import java.util.*

class MainAdapter(context: Context) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val LAYOUT_SIMPLE = 0
        const val LAYOUT_GRID = 1

        fun getWorkoutName(context: Context, typeIndex: Int): String {
            val workouts = context.resources.getStringArray(R.array.workout_types)
            return workouts[typeIndex]
        }
    }

    private var _latests: List<WorkoutItem> =
        WorkoutRepository.getInstance(context).getLatestWorkouts().reversed()
    private var _workouts: List<WorkoutItem> = WorkoutRepository.getInstance(context).getWorkouts()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            LAYOUT_GRID -> GridHolder(LayoutInflater.from(parent.context), parent)
            else -> ItemHolder(LayoutInflater.from(parent.context), R.layout.item_workout, parent)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is GridHolder -> holder.bind(_latests)
            is ItemHolder -> holder.bind(_workouts[position])
        }
    }

    override fun getItemCount() = _workouts.size

    override fun getItemViewType(position: Int) = when (position) {
        0 -> LAYOUT_GRID
        else -> LAYOUT_SIMPLE
    }

    class ItemHolder(inflater: LayoutInflater, layout: Int, parent: ViewGroup) :
        ViewHolder(inflater.inflate(layout, parent, false)) {
        val context: Context = parent.context
        fun bind(item: WorkoutItem) {
            itemView.text1.text = getWorkoutName(context, item.type)
            val mf = MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.WIDE)
            itemView.text2.text = mf.formatMeasures(Measure(item.value, WXApp.getUnit()))
            itemView.text3.text = DateTimeUtils.getRelativeDateFormat(WXApp.appContext!!, item.date)
            itemView.setOnClickListener {
                // todo: send WorkoutItem to RecordActivity
//                RecordActivity.launch(itemView.context)
            }
        }
    }

    class GridItemHolder(inflater: LayoutInflater, layout: Int, parent: ViewGroup) :
        ViewHolder(inflater.inflate(layout, parent, false)) {
        val context: Context = parent.context
        fun bind(item: WorkoutItem) {
            itemView.text1.text = getWorkoutName(context, item.type)
            val mf = MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.SHORT)
            itemView.text2.text = mf.formatMeasures(Measure(item.value, WXApp.getUnit()))
            itemView.setOnClickListener {
                // todo: send WorkoutItem to RecordActivity
//                RecordActivity.launch(itemView.context)
            }
        }
    }

    class GridHolder(inflater: LayoutInflater, parent: ViewGroup) :
        ViewHolder(inflater.inflate(R.layout.grid_workout, parent, false)) {
        fun bind(items: List<WorkoutItem>) {
            itemView.recycler_view?.also {
                it.layoutManager = LinearLayoutManager(itemView.context, HORIZONTAL, false)
                it.adapter = GridAdapter(items)
            }
        }
    }

    class GridAdapter(private val items: List<WorkoutItem>) :
        RecyclerView.Adapter<GridItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemHolder {
            return GridItemHolder(
                LayoutInflater.from(parent.context),
                R.layout.item_grid_workout,
                parent
            )
        }

        override fun onBindViewHolder(holder: GridItemHolder, position: Int) {
            return holder.bind(items[position])
        }

        override fun getItemCount() = items.size
    }
}

