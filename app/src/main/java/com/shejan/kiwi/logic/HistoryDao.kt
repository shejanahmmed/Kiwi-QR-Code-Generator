package com.shejan.kiwi.logic

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_items ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryItem)

    @Delete
    suspend fun delete(item: HistoryItem)

    @Query("DELETE FROM history_items")
    suspend fun deleteAll()
}
