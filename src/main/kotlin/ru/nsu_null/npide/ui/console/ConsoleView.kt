package ru.nsu_null.npide.ui.console

import SimpleOutlinedTextFieldSample
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.npide.NPIDE
import java.awt.event.KeyEvent


@Preview
@Composable
fun ConsolePanelView(settings: Settings, console: Console) {
    Row(Modifier.fillMaxSize()) {
        ConsoleView(Modifier.weight(0.7f), settings, console)
        ConsoleStatusView(Modifier.weight(0.3f), settings, console)
    }
}

@Composable
fun ConsoleView(modifier: Modifier, settings: Settings, console: Console) {
    with(LocalDensity.current) {
        val lines = console.content.value.lineSequence().toList()
        val lineHeight = settings.fontSize.toDp() * 1.6f
        Column(modifier) {
            Box(
                Modifier.weight(0.8f).fillMaxWidth()
                    .background(AppTheme.colors.backgroundDark)
            ) {
                val scrollState = rememberLazyListState()

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
            Box(
                Modifier.weight(0.2f)
            ) {
                Box(Modifier.height(lineHeight).fillMaxWidth().background(Color.Black)) {
                    val input = remember(NPIDE.currentProject) { mutableStateOf("") }
                    val onChange = fun(enteredValue: String) {
                        if (enteredValue.endsWith('\n')) {
                            console.display(enteredValue)
                            input.value = ""
                        } else {
                            input.value = enteredValue
                        }
                    }
                    BasicTextField(input.value,
                        onValueChange = onChange,
                        textStyle = TextStyle.Default + TextStyle(Color.White),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.padding(5.dp, 0.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConsoleStatusView(modifier: Modifier, settings: Settings, console: Console) {
    Box(Modifier.fillMaxSize().then(modifier)) {
        Text("Console status (wip)",
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center))
    }
}
