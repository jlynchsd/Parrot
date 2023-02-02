package com.fowlplay.parrot.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.fowlplay.parrot.model.Screech
import com.fowlplay.parrot.viewmodel.ParrotViewModel

@Composable
fun Cacophony(lazyPagingItems: LazyPagingItems<Screech>, viewModel: ParrotViewModel, tag: String = "") {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        state = persistedLazyScrollState(viewModel, tag)
    ) {
        items(
            items = lazyPagingItems,
            key = {screech: Screech -> screech.hashCode() } //TODO have seen hash collision
        ) {
            screech ->
            if (screech != null) {
                ScreechCard(screech = screech, viewModel)
            }
        }
    }
}

@Composable
fun persistedLazyScrollState(viewModel: ParrotViewModel, tag: String): LazyListState {
    val initialScrollState = viewModel.state.value.scrollState[tag] ?: Pair(0, 0)
    val scrollState = rememberLazyListState(
        initialScrollState.first,
        initialScrollState.second
    )

    DisposableEffect(key1 = null) {
        onDispose {
            viewModel.updateState(
                viewModel.state.value.also { state ->
                    state.scrollState[tag] = Pair(
                        scrollState.firstVisibleItemIndex,
                        scrollState.firstVisibleItemScrollOffset
                    )
                }
            )
        }
    }
    return scrollState
}

