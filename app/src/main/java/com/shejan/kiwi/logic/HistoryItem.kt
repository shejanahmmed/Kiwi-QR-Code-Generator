package com.shejan.kiwi.logic

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single QR code entry in the history database.
 * 
 * @property id Unique identifier for the history item (auto-generated).
 * @property url The content embedded in the QR code (URL or text).
 * @property timestamp The time when the item was created or scanned.
 * @property type The source of the item: "generated" for created QRs, "scanned" for scanned ones.
 */
@Entity(tableName = "history_items")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val url: String,
    val label: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "generated"
)
