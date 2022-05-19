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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.GitBranchTellerView
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
        val lineHeight = settings.fontSize.toDp() * 1.6f
        Column(modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(
                // weight is essential so that the lazycolumn doesn't eat up all the space
                Modifier.fillMaxWidth().weight(0.1f)
                    .background(AppTheme.colors.backgroundDark.copy(alpha = 0.3f))
            ) {
                val scrollState = rememberLazyListState()
                val lines = console.content
                rememberCoroutineScope().launch {
                    if (lines.isNotEmpty()) {
                        scrollState.animateScrollToItem(lines.lastIndex)
                    }
                }

                LazyColumn(
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
            Box(Modifier.height(lineHeight).background(Color.Black.copy(alpha = 0.5f))) {
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
            Spacer(Modifier.padding(10.dp))
        }
    }
}

@Composable
fun ConsoleControlPanelView(modifier: Modifier, settings: Settings, console: Console) {
    Box(Modifier.fillMaxSize().padding(15.dp).then(modifier)) {
        Column(Modifier.fillMaxSize()) {
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
            Column(Modifier.fillMaxSize()) {
                IconBar(Modifier.weight(0.8f), console)
                GitBranchTellerView(Modifier.weight(0.2f))
            }
        }
    }
}

@Composable
private fun IconBar(modifier: Modifier = Modifier, console: Console) {
    val isWindows = remember { "Win" in System.getProperty("os.name") }
    fun launchShell() {
        val shell = Runtime.getRuntime().let {
            if (isWindows) it.exec("powershell") else it.exec("bash")
        }
        console.runProcess(shell, "shell")
    }
    @Composable
    fun IconsSpacer() {
        Spacer(Modifier.padding(5.dp))
    }

    Row(horizontalArrangement = Arrangement.Start, modifier = modifier.fillMaxWidth()) {
        Icon(Icons.Default.Stop, "Stop process", tint = Color.Red,
            modifier = Modifier.clickable {
                console.detachCurrentProcess()
            })
        IconsSpacer()
        Icon(Icons.Default.DeleteSweep, "Clear terminal", tint = Color.White,
            modifier = Modifier.clickable {
                console.clear()
            })
        IconsSpacer()
        Icon(Icons.Default.Computer, "Clear terminal", tint = Color.White,
            modifier = Modifier.clickable(onClick = ::launchShell)
        )
    }
}
