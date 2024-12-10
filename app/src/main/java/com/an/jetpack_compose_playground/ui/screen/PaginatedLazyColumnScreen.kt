package com.an.jetpack_compose_playground.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.an.jetpack_compose_playground.ui.common.MainScaffold
import com.an.jetpack_compose_playground.ui.component.PaginatedLazyColumn
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.an.jetpack_compose_playground.R

@Composable
fun PaginatedLazyColumnScreen() {
    // Immutable list to hold the items
    var items by remember { mutableStateOf<PersistentList<String>>(persistentListOf()) }
    // Tracks if more items are being loaded
    var isLoading by remember { mutableStateOf(false) }
    // Tracks the current page of data
    var currentPage by remember { mutableIntStateOf(1) }
    // Determines whether there are more items to load
    val totalPages = 5 // Example total pages
    val listState = rememberLazyListState()

    // Simulate loading more items
    fun loadMoreItems() {
        if (isLoading || currentPage > totalPages) return // Prevent duplicate calls or loading beyond the last page

        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            // Simulate a network delay
            delay(1000)

            // Add new items for the current page
            val newItems = List(50) { "Item ${(currentPage - 1) * 50 + it + 1}" }
            items = items.addAll(newItems.toPersistentList())

            // Increment the page count
            currentPage++
            isLoading = false // Reset the loading state
        }
    }

    // Load initial data when the screen is launched
    LaunchedEffect(Unit) {
        loadMoreItems()
    }

    MainScaffold(R.string.btn_txt_paginated_list) { innerPadding ->
        PaginatedLazyColumn(
            items = items,
            loadMoreItems = { loadMoreItems() },
            listState = listState,
            isLoading = isLoading,
            modifier = Modifier.padding(innerPadding)
        ) { index, item ->
            // Define how each item should be displayed
            Text(
                text = "[$index] $item",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}