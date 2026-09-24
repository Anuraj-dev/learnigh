package com.anuraj.learnigh

import android.app.Application
import com.anuraj.learnigh.data.local.AppDatabase
import com.anuraj.learnigh.data.local.SeedData
import com.anuraj.learnigh.data.local.SettingsDataStore
import com.anuraj.learnigh.data.repository.CourseRepository
import com.anuraj.learnigh.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LearnighApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: CourseRepository
        private set
    lateinit var settings: SettingsDataStore
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = CourseRepository(database.courseDao())
        settings = SettingsDataStore(this)
        appScope.launch {
            repository.seedIfNeeded(SeedData.SEED_VERSION, SeedData.sampleCourses())
            settings.bumpStreak(DateUtils.todayKey())
        }
    }
}
