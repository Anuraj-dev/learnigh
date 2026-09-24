package com.anuraj.learnigh.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val provider: String,
    val sourceType: String,
    val url: String = "",
    val purchaseOrStartDate: Long? = null,
    val deadline: Long? = null,
    val status: String = "NOT_STARTED",
    val progressPercent: Int = 0,
    val notes: String = "",
    val tags: String = "",
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "app_metadata")
data class AppMetadataEntity(
    @PrimaryKey val key: String,
    val storedValue: String,
)
