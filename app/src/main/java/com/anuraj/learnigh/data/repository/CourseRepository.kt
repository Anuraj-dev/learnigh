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
        query = query,
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
            dao.update(course.copy(updatedAt = now))
            course.id
        }
    }

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun count(): Int = dao.count()

    suspend fun seedIfEmpty(seed: List<CourseEntity>) {
        if (dao.count() == 0) {
            dao.insertAll(seed)
        }
    }
}
