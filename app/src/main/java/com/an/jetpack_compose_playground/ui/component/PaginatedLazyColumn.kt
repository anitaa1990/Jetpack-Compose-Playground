package com.an.jetpack_compose_playground.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * A reusable composable function for displaying a paginated list using LazyColumn in Jetpack Compose.
 *
 * This composable handles:
 * - Displaying a list of items with efficient scrolling using `LazyColumn`.
 * - Automatically triggering the `loadMoreItems` function when the user scrolls near the end of the list.
 * - Showing a loading indicator while new items are being fetched.
 * - Using an immutable `PersistentList` to ensure efficient updates and state management.
 *
 * @param items A [PersistentList] of items to display in the list.
 * @param loadMoreItems A callback function to load more items when pagination is triggered.
 * @param listState The [LazyListState] for tracking the scroll position of the list.
 * @param buffer An optional parameter to define how many items from the end will trigger loading more items (default: 2).
 * @param isLoading A flag indicating whether items are currently being loaded.
 * @param modifier A [Modifier] for customizing the appearance of the [LazyColumn].
 * @param content A lambda defining how each item should be displayed. Takes the index and the item as parameters.
 */
@Composable
fun <T> PaginatedLazyColumn(
    // PersistentList ensures immutability, making state updates safe and predictable. Modifications
    // like `add` or `remove` return a new list, preserving the original. Persistent lists are
    // optimized for minimal copying, enhancing performance in this scenario where the list is
    // updated frequently.
    items: PersistentList<T>,
    loadMoreItems: () -> Unit, // Function to load more items
    listState: LazyListState,  // Tracks the scroll position of the LazyColumn
    buffer: Int = 2,           // Triggers loading more items when within this buffer from the end
    isLoading: Boolean,        // Indicates if items are being loaded
    modifier: Modifier = Modifier, // Allows customization of the LazyColumn's appearance
    content: @Composable (Int, T) -> Unit // Defines how each item is rendered
) {
    // A derived state to determine when to load more items based on scroll position
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount // Total number of items in the list
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0  // Last visible item's index
            // Triggers loading when we scroll near the end of the list and items are not already being loaded
            lastVisibleItemIndex >= (totalItemsCount - buffer) && !isLoading
        }
    }

    // LaunchedEffect to trigger loading more items when necessary. It ensures the effect is
    // canceled and restarted whenever the dependency (`listState`) changes,
    // preventing stale data flow.
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value } // Observes changes in the derived state
            .distinctUntilChanged()           // Ensures the effect only triggers on unique changes
            .filter { it }                    // Filters to execute only when `shouldLoadMore` is true
            .collect { loadMoreItems() }      // Calls the function to load more items
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState, // Pass the scroll state
        contentPadding = PaddingValues(16.dp)  // Add padding around the content
    ) {
        // Display each item in the list using a unique key
        itemsIndexed(
            items = items,
            key = { _, item -> item as Any }
        ) { index, item ->
            content(index, item)
        }

        // Show a loading indicator at the bottom when items are being loaded
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
