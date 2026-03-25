package com.shejan.kiwi.ui

import android.app.Application
import androidx.lifecycle.*
import com.shejan.kiwi.logic.HistoryDatabase
import com.shejan.kiwi.logic.HistoryItem
import com.shejan.kiwi.logic.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository
    val allHistory: Flow<List<HistoryItem>>

    init {
        val historyDao = HistoryDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
        allHistory = repository.allHistory
    }

    fun saveUrl(url: String) {
        viewModelScope.launch {
            repository.insert(url)
        }
    }

    fun deleteItem(item: HistoryItem) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
