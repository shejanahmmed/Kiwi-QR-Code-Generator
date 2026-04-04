package com.shejan.kiwi.logic

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

/**
 * Migration from version 1 to 2 of the [HistoryDatabase].
 * Adds the 'type' column to the 'history_items' table to distinguish between 
 * generated and scanned QR codes.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE history_items ADD COLUMN type TEXT NOT NULL DEFAULT 'generated'")
    }
}

/**
 * The Room database for storing QR code history.
 * This class follows the singleton pattern to ensure only one instance of the 
 * database is opened at a time.
 */
@Database(entities = [HistoryItem::class], version = 2, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    /**
     * Provides access to the [HistoryDao] for database operations.
     */
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        /**
         * Gets the singleton instance of the [HistoryDatabase].
         * Initializes the database if it hasn't been created yet.
         * 
         * @param context The application context.
         * @return The [HistoryDatabase] instance.
         */
        fun getDatabase(context: Context): HistoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java,
                    "history_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
