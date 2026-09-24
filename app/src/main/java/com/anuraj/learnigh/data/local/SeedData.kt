package com.anuraj.learnigh.data.local

import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import java.util.Calendar

object SeedData {
    const val SEED_VERSION = 1
    const val SEED_VERSION_KEY = "sample_seed_version"

    fun sampleCourses(now: Long = System.currentTimeMillis()): List<CourseEntity> {
        fun daysFromNow(days: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = now
            calendar.add(Calendar.DAY_OF_YEAR, days)
            return calendar.timeInMillis
        }

        return listOf(
            CourseEntity(
                title = "Jetpack Compose Crash Course for Android with Kotlin",
                provider = "Udemy",
                sourceType = SourceType.PAID_COURSE.name,
                url = "https://www.udemy.com/course/jetpack-compose-masterclass/",
                purchaseOrStartDate = daysFromNow(-45),
                deadline = daysFromNow(21),
                status = CourseStatus.IN_PROGRESS.name,
                progressPercent = 42,
                notes = "Focus on Navigation + ViewModel first.",
                tags = "android,compose,kotlin",
                reminderEnabled = true,
                createdAt = daysFromNow(-45),
                updatedAt = daysFromNow(-1),
            ),
            CourseEntity(
                title = "Kotlin Coroutines Deep Dive",
                provider = "YouTube",
                sourceType = SourceType.YOUTUBE.name,
                url = "https://www.youtube.com/playlist?list=PLQkwcJG4YTCQcFEPuYGuv54nYai_lwil_",
                purchaseOrStartDate = daysFromNow(-14),
                deadline = daysFromNow(7),
                status = CourseStatus.IN_PROGRESS.name,
                progressPercent = 65,
                notes = "Philipp Lackner playlist — finish flow episodes.",
                tags = "kotlin,coroutines,youtube",
                reminderEnabled = true,
                createdAt = daysFromNow(-14),
                updatedAt = daysFromNow(-2),
            ),
            CourseEntity(
                title = "Machine Learning Specialization",
                provider = "Coursera",
                sourceType = SourceType.PAID_COURSE.name,
                url = "https://www.coursera.org/specializations/machine-learning-introduction",
                purchaseOrStartDate = daysFromNow(-90),
                deadline = daysFromNow(60),
                status = CourseStatus.PAUSED.name,
                progressPercent = 28,
                notes = "Andrew Ng — resume Week 3 when free.",
                tags = "ml,coursera,python",
                reminderEnabled = false,
                createdAt = daysFromNow(-90),
                updatedAt = daysFromNow(-20),
            ),
            CourseEntity(
                title = "Responsive Web Design Certification",
                provider = "freeCodeCamp",
                sourceType = SourceType.OTHER_FREE.name,
                url = "https://www.freecodecamp.org/learn/responsive-web-design-v9",
                purchaseOrStartDate = daysFromNow(-5),
                deadline = null,
                status = CourseStatus.NOT_STARTED.name,
                progressPercent = 0,
                notes = "Brush up CSS Grid before portfolio rebuild.",
                tags = "web,css,free",
                reminderEnabled = false,
                createdAt = daysFromNow(-5),
                updatedAt = daysFromNow(-5),
            ),
            CourseEntity(
                title = "UI Design Fundamentals",
                provider = "Domestika",
                sourceType = SourceType.PAID_COURSE.name,
                url = "https://www.domestika.org/en/courses/ux-design",
                purchaseOrStartDate = null,
                deadline = daysFromNow(30),
                status = CourseStatus.WISHLIST.name,
                progressPercent = 0,
                notes = "Wait for the next sale.",
                tags = "design,ui,wishlist",
                reminderEnabled = true,
                createdAt = daysFromNow(-3),
                updatedAt = daysFromNow(-3),
            ),
            CourseEntity(
                title = "Notion AI for Students",
                provider = "Notion",
                sourceType = SourceType.APP_SUBSCRIPTION.name,
                url = "https://www.notion.so/product/ai",
                purchaseOrStartDate = daysFromNow(-120),
                deadline = daysFromNow(10),
                status = CourseStatus.IN_PROGRESS.name,
                progressPercent = 80,
                notes = "Wrap up the learning templates before renewal.",
                tags = "productivity,subscription",
                reminderEnabled = true,
                createdAt = daysFromNow(-120),
                updatedAt = daysFromNow(-4),
            ),
        )
    }
}
