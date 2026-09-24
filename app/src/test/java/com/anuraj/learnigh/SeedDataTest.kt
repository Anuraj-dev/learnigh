package com.anuraj.learnigh

import com.anuraj.learnigh.data.local.SeedData
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedDataTest {
    private val courses = SeedData.sampleCourses(now = 1_700_000_000_000)

    @Test
    fun sampleCoursesCoverRequiredProviders() {
        val providers = courses.map { it.provider }.toSet()

        assertTrue(providers.containsAll(setOf("Udemy", "YouTube", "Coursera", "freeCodeCamp")))
    }

    @Test
    fun sampleCoursesUseValidStoredValues() {
        val sourceTypes = SourceType.entries.map { it.name }.toSet()
        val statuses = CourseStatus.entries.map { it.name }.toSet()

        assertTrue(courses.all { it.sourceType in sourceTypes })
        assertTrue(courses.all { it.status in statuses })
        assertTrue(courses.all { it.progressPercent in 0..100 })
        assertTrue(courses.all { it.title.isNotBlank() && it.provider.isNotBlank() })
    }

    @Test
    fun sampleCoursesUseUniqueHttpsLinks() {
        val urls = courses.map { it.url }

        assertEquals(urls.size, urls.toSet().size)
        assertTrue(urls.all { it.startsWith("https://") })
        assertTrue(courses.first { it.provider == "YouTube" }.url.contains("playlist?list="))
    }
}
