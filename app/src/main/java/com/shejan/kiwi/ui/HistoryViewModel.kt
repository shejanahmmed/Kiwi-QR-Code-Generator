package com.shejan.kiwi.ui

import android.app.Application
import androidx.lifecycle.*
import com.shejan.kiwi.logic.HistoryDatabase
import com.shejan.kiwi.logic.HistoryItem
import com.shejan.kiwi.logic.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel for the [HistoryScreen].
 * Manages the state of QR code history and provides methods to interact with the [HistoryRepository].
 * 
 * @param application The application context.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository

    /**
     * An observable stream of history items, sorted by most recent.
     */
    val allHistory: Flow<List<HistoryItem>>

    init {
        val historyDao = HistoryDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
        allHistory = repository.allHistory
    }

    /**
     * Saves a URL or text content to the history database.
     * 
     * @param url The content to save.
     * @param type The source type ("generated" or "scanned").
     */
    fun saveUrl(url: String, type: String = "generated") {
        viewModelScope.launch {
            repository.insert(url, type)
        }
    }

    /**
     * Deletes a specific item from the history.
     * @param item The item to delete.
     */
    fun deleteItem(item: HistoryItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    /**
     * Updates an existing history item's custom name (label).
     * 
     * @param item The existing history item.
     * @param newLabel The new custom name/label for the item.
     */
    fun updateItem(item: HistoryItem, newLabel: String) {
        viewModelScope.launch {
            repository.update(item, newLabel)
        }
    }

    /**
     * Clears all history entries from the database.
     */
    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
