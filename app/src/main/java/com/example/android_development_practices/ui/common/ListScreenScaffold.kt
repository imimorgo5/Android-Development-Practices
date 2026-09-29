package com.example.android_development_practices.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> ListScreenScaffold(
    title: String,
    onBack: () -> Unit,
    uiState: UiState<List<T>>,
    onRetry: () -> Unit,
    itemKey: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    ScreenScaffold(title = title, onBack = onBack) { contentModifier ->
        UiStateContent(uiState = uiState, onRetry = onRetry, modifier = contentModifier) { items ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = items, key = itemKey) { item ->
                    itemContent(item)
                }
            }
        }
    }
}