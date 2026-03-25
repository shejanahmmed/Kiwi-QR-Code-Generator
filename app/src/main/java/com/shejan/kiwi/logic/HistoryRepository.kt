package com.shejan.kiwi.logic

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()

    suspend fun insert(url: String) {
        if (url.isNotEmpty()) {
            historyDao.insert(HistoryItem(url = url))
        }
    }

    suspend fun delete(item: HistoryItem) {
        historyDao.delete(item)
    }

    suspend fun clearAll() {
        historyDao.deleteAll()
    }
}
