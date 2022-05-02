package ru.nsu_null.npide.ui.console

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotTouch
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.npide.NPIDE


@Preview
@Composable
fun ConsolePane(settings: Settings, console: Console) {
    Row(Modifier.fillMaxSize()) {
        ConsoleView(Modifier.weight(0.7f), settings, console)
        ConsoleControlPanelView(Modifier.weight(0.3f), settings, console)
    }
}

@Composable
fun ConsoleView(modifier: Modifier, settings: Settings, console: Console) {
    with(LocalDensity.current) {
        val lines = console.content.lineSequence().toList()
        val lineHeight = settings.fontSize.toDp() * 1.6f
        Column(modifier, verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                Modifier.fillMaxWidth()
                    .background(AppTheme.colors.backgroundDark)
            ) {
                val scrollState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState
                ) {
                    items(lines.size) { index ->
                        Box(Modifier.height(lineHeight)) {
                            Text(lines[index], modifier = Modifier.padding(5.dp, 0.dp))
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
            Box(Modifier.requiredHeight(lineHeight).fillMaxWidth().background(Color.Black)) {
                val input = remember(NPIDE.currentProject) { mutableStateOf("") }
                val onChange = fun(enteredValue: String) {
                    if (enteredValue.endsWith('\n')) {
                        console.send(enteredValue)
                        input.value = ""
                    } else {
                        input.value = enteredValue
                    }
                }
                BasicTextField(input.value,
                    onValueChange = onChange,
                    textStyle = TextStyle.Default + TextStyle(Color.White),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.padding(5.dp, 0.dp).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ConsoleControlPanelView(modifier: Modifier, settings: Settings, console: Console) {
    Box(Modifier.fillMaxSize().padding(15.dp).then(modifier)) {
        Column {
            Row(horizontalArrangement = Arrangement.Start) {
                val processMessage = if (!console.processIsAttached)
                    "No process attached" else "Process '${console.attachedProcessLabel}' is attached"
                if (console.processIsAttached) {
                    Icon(Icons.Default.Done, "Process is attached", tint = Color.Green)
                } else {
                    Icon(Icons.Default.DoNotTouch, "No process attached", tint = Color.Red)
                }
                Spacer(Modifier.padding(3.dp))
                Text(processMessage, textAlign = TextAlign.Center)
            }
            Divider(Modifier.padding(0.dp, 15.dp))
            Icon(Icons.Default.Stop, "Stop process", tint = Color.Red,
                modifier = Modifier.clickable {
                    console.detachCurrentProcess()
                }.scale(1.5f))
        }
    }
}
