package com.shejan.kiwi.logic

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the history items database.
 * Provides methods for interacting with the persistent QR history storage.
 */
@Dao
interface HistoryDao {
    /**
     * Retrieves all history items sorted by timestamp in descending order.
     * @return A [Flow] encompassing a list of [HistoryItem] objects.
     */
    @Query("SELECT * FROM history_items ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    /**
     * Inserts a [HistoryItem] into the database. If the item already exists, it will be replaced.
     * @param item The history item to persist.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryItem)

    /**
     * Deletes a specific [HistoryItem] from the database.
     * @param item The history item to remove.
     */
    @Delete
    suspend fun delete(item: HistoryItem)

    /**
     * Deletes all entries from the history table.
     */
    @Query("DELETE FROM history_items")
    suspend fun deleteAll()
}
