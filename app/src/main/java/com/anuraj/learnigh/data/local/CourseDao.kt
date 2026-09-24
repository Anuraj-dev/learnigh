package com.anuraj.learnigh.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: CourseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Update
    suspend fun update(course: CourseEntity)

    @Delete
    suspend fun delete(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :id")
    suspend fun deleteById(id: Long)
}
