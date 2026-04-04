package com.shejan.kiwi.logic

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to the QR history data source.
 * Acts as a mediator between the [HistoryDao] and the UI Layer.
 * 
 * @property historyDao The Data Access Object for history items.
 */
class HistoryRepository(private val historyDao: HistoryDao) {
    /**
     * A [Flow] that emits the list of all history items from the database.
     */
    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()

    /**
     * Inserts a new QR code entry into the history.
     * 
     * @param url The content string of the QR code.
     * @param type The type of entry ("generated" or "scanned").
     */
    suspend fun insert(url: String, type: String = "generated") {
        if (url.isNotEmpty()) {
            historyDao.insert(HistoryItem(url = url, type = type))
        }
    }

    /**
     * Deletes a specific item from the history.
     * @param item The [HistoryItem] to remove.
     */
    suspend fun delete(item: HistoryItem) {
        historyDao.delete(item)
    }

    /**
     * Clears all entries from the history database.
     */
    suspend fun clearAll() {
        historyDao.deleteAll()
    }
}
