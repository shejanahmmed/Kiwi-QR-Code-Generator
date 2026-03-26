package com.shejan.kiwi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shejan.kiwi.logic.HistoryItem
import com.shejan.kiwi.ui.theme.AmoledBlack
import com.shejan.kiwi.ui.theme.DarkGrey
import com.shejan.kiwi.ui.theme.KiwiGreen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val historyItems by viewModel.allHistory.collectAsState(initial = emptyList())
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "History",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KiwiGreen
            )
            
            if (historyItems.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearAll() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
                        tint = Color.Gray
                    )
                }
            }
        }

        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "History is empty",
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(historyItems) { item ->
                    HistoryCard(
                        item = item,
                        date = dateFormatter.format(Date(item.timestamp)),
                        onDelete = { viewModel.deleteItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem, date: String, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = DarkGrey,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = KiwiGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.url,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
