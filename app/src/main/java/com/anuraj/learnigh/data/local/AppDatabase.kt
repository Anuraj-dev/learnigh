package com.anuraj.learnigh.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CourseEntity::class, AppMetadataEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao

    companion object {
        const val DATABASE_NAME = "learnigh.db"

        private val MIGRATION_1_2 = Migration(1, 2) { database ->
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `app_metadata` " +
                    "(`key` TEXT NOT NULL, `storedValue` TEXT NOT NULL, PRIMARY KEY(`key`))",
            )
            database.execSQL(
                "INSERT OR REPLACE INTO `app_metadata` (`key`, `storedValue`) " +
                    "VALUES ('sample_seed_version', '1')",
            )
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
