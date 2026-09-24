package com.anuraj.learnigh.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id")
    fun observeById(id: Long): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getById(id: Long): CourseEntity?

    @Query(
        """
        SELECT * FROM courses
        WHERE (:status IS NULL OR status = :status)
          AND (:sourceType IS NULL OR sourceType = :sourceType)
          AND (
            :query = '' OR
            LOWER(title) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(provider) LIKE '%' || LOWER(:query) || '%' OR
            LOWER(tags) LIKE '%' || LOWER(:query) || '%'
          )
        ORDER BY
          CASE WHEN deadline IS NULL THEN 1 ELSE 0 END,
          deadline ASC,
          updatedAt DESC
        """
    )
    fun observeFiltered(
        query: String,
        status: String?,
        sourceType: String?,
    ): Flow<List<CourseEntity>>

    @Query(
        """
        SELECT * FROM courses
        WHERE status = 'IN_PROGRESS'
        ORDER BY updatedAt DESC
        """
    )
    fun observeInProgress(): Flow<List<CourseEntity>>

    @Query(
        """
        SELECT * FROM courses
        WHERE deadline IS NOT NULL
          AND status NOT IN ('COMPLETED', 'EXPIRED')
        ORDER BY deadline ASC
        """
    )
    fun observeWithDeadlines(): Flow<List<CourseEntity>>

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun count(): Int

    @Query("SELECT storedValue FROM app_metadata WHERE `key` = :key LIMIT 1")
    suspend fun getMetadataValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(course: CourseEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMetadata(metadata: AppMetadataEntity)

    @Update
    suspend fun update(course: CourseEntity): Int

    @Query(
        """
        UPDATE courses
        SET progressPercent = :progressPercent,
            status = :status,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateProgress(
        id: Long,
        progressPercent: Int,
        status: String,
        updatedAt: Long,
    ): Int

    @Query(
        """
        UPDATE courses
        SET status = :status,
            progressPercent = :progressPercent,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateStatus(
        id: Long,
        status: String,
        progressPercent: Int,
        updatedAt: Long,
    ): Int

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Transaction
    suspend fun seedSamplesIfNeeded(seedVersion: Int, courses: List<CourseEntity>) {
        val currentVersion = getMetadataValue(SeedData.SEED_VERSION_KEY)?.toIntOrNull() ?: 0
        if (currentVersion >= seedVersion) return
        if (count() == 0) insertAll(courses)
        upsertMetadata(AppMetadataEntity(SeedData.SEED_VERSION_KEY, seedVersion.toString()))
    }
}
