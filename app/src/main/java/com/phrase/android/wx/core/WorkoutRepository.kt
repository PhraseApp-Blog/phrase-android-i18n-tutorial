package com.phrase.android.wx.core

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.FileOutputStream
import java.io.FileReader
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

class WorkoutRepository private constructor(val context: Context) {

    companion object {
        private var INSTANCE: WorkoutRepository? = null

        const val WORKOUTS_FILENAME = "workouts.json"
        const val LATEST_WORKOUTS_FILENAME = "latest_workouts.json"

        fun getInstance(context: Context): WorkoutRepository {
            if (INSTANCE == null) {
                INSTANCE = WorkoutRepository(context.applicationContext)
            }
            return INSTANCE as WorkoutRepository
        }
    }

    fun getWorkouts(): ArrayList<WorkoutItem> {
        try {
            val basePath = Paths.get(context.cacheDir.absolutePath, WORKOUTS_FILENAME)
            val reader = FileReader(basePath.toFile())
            val listType = object : TypeToken<List<WorkoutItem>>() {}.type
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()
            return gson.fromJson(reader, listType)
        } catch (e: Exception) {
        }
        return ArrayList()
    }

    fun getLatestWorkouts(): ArrayList<WorkoutItem> {
        try {
            val basePath = Paths.get(context.cacheDir.absolutePath, LATEST_WORKOUTS_FILENAME)
            val reader = FileReader(basePath.toFile())
            val listType = object : TypeToken<List<WorkoutItem>>() {}.type
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()
            return gson.fromJson(reader, listType)
        } catch (e: Exception) {
        }
        return ArrayList()
    }

    fun loadInitialData() {
        context.assets.open(WORKOUTS_FILENAME).use { input ->
            val targetFile = Paths.get(context.cacheDir.absolutePath, WORKOUTS_FILENAME)
            FileOutputStream(targetFile.toFile().absolutePath).use { output ->
                input.copyTo(output)
                output.flush()
            }
        }
        context.assets.open(LATEST_WORKOUTS_FILENAME).use { input ->
            val targetFile = Paths.get(context.cacheDir.absolutePath, LATEST_WORKOUTS_FILENAME)
            FileOutputStream(targetFile.toFile().absolutePath).use { output ->
                input.copyTo(output)
                output.flush()
            }
        }
    }

    fun addWorkout(workoutType: Int, value: Double, date: Calendar): Boolean {

        val workouts = getWorkouts()
        val workoutItem = WorkoutItem(workouts.size + 1, workoutType, value, Date(date.timeInMillis))
        workouts.add(workoutItem)

        val latestWorkouts = getLatestWorkouts()
        if (latestWorkouts.size >= 5) {
            latestWorkouts.removeAt(0)
        }
        latestWorkouts.add(workoutItem)

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()

        var jsonWorkouts: String = gson.toJson(workouts)
        val baseWorkoutsPath = Paths.get(context.cacheDir.absolutePath, WORKOUTS_FILENAME)
        baseWorkoutsPath.toFile().writeText(jsonWorkouts)

        var jsonLatestWorkouts: String = gson.toJson(latestWorkouts)
        val baseLatestWorkoutsPath =
            Paths.get(context.cacheDir.absolutePath, LATEST_WORKOUTS_FILENAME)
        baseLatestWorkoutsPath.toFile().writeText(jsonLatestWorkouts)

        return true
    }
}