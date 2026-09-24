package com.anuraj.learnigh.data.repository

import com.anuraj.learnigh.data.local.CourseDao
import com.anuraj.learnigh.data.local.CourseEntity
import com.anuraj.learnigh.data.model.CourseStatus
import com.anuraj.learnigh.data.model.SourceType
import kotlinx.coroutines.flow.Flow

class CourseRepository(private val dao: CourseDao) {
    fun observeAll(): Flow<List<CourseEntity>> = dao.observeAll()

    fun observeById(id: Long): Flow<CourseEntity?> = dao.observeById(id)

    fun observeFiltered(
        query: String = "",
        status: CourseStatus? = null,
        sourceType: SourceType? = null,
    ): Flow<List<CourseEntity>> = dao.observeFiltered(
        query = query.trim(),
        status = status?.name,
        sourceType = sourceType?.name,
    )

    fun observeInProgress(): Flow<List<CourseEntity>> = dao.observeInProgress()

    fun observeWithDeadlines(): Flow<List<CourseEntity>> = dao.observeWithDeadlines()

    suspend fun getById(id: Long): CourseEntity? = dao.getById(id)

    suspend fun upsert(course: CourseEntity): Long {
        val now = System.currentTimeMillis()
        return if (course.id == 0L) {
            dao.insert(course.copy(createdAt = now, updatedAt = now))
        } else {
            val updated = course.copy(updatedAt = now)
            if (dao.update(updated) > 0) course.id else dao.insert(updated.copy(id = 0, createdAt = now))
        }
    }

    suspend fun updateProgress(id: Long, percent: Int) {
        val current = dao.getById(id) ?: return
        val clamped = percent.coerceIn(0, 100)
        val status = when {
            clamped >= 100 -> CourseStatus.COMPLETED.name
            clamped > 0 && current.status in listOf(
                CourseStatus.NOT_STARTED.name,
                CourseStatus.WISHLIST.name,
            ) -> CourseStatus.IN_PROGRESS.name
            else -> current.status
        }
        dao.updateProgress(id, clamped, status, System.currentTimeMillis())
    }

    suspend fun updateStatus(id: Long, status: CourseStatus) {
        val current = dao.getById(id) ?: return
        val progress = if (status == CourseStatus.COMPLETED) 100 else current.progressPercent
        dao.updateStatus(id, status.name, progress, System.currentTimeMillis())
    }

    suspend fun delete(id: Long): Boolean = dao.deleteById(id) > 0

    suspend fun count(): Int = dao.count()

    suspend fun seedIfNeeded(seedVersion: Int, seed: List<CourseEntity>) {
        dao.seedSamplesIfNeeded(seedVersion, seed)
    }
}
