package ru.nsu_null.npide.platform

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp


@Composable
fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: ScrollState
) = androidx.compose.foundation.VerticalScrollbar(
    rememberScrollbarAdapter(scrollState),
    modifier
)

@Composable
fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: LazyListState,
    itemCount: Int,
    averageItemSize: Dp
) = androidx.compose.foundation.VerticalScrollbar(
    rememberScrollbarAdapter(scrollState),
    modifier
)
