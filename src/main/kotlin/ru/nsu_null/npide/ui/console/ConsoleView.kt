package ru.nsu_null.npide.ui.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.common.Settings


@Composable
fun ConsoleView(settings: Settings, console: Console) {
    val lines = console.content.value.lineSequence().toList()

    with(LocalDensity.current) {
        Box(
            Modifier.fillMaxSize()
                .background(AppTheme.colors.backgroundDark)
        ) {
            val scrollState = rememberLazyListState()
            val lineHeight = settings.fontSize.toDp() * 1.6f

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState
            ) {
                items(lines.size) { index ->
                    Box(Modifier.height(lineHeight)) {
                        Text(lines[index])
                    }
                }
            }

            VerticalScrollbar(
                Modifier.align(Alignment.CenterEnd),
                scrollState,
                lines.size,
                lineHeight
            )
        }
    }
}
