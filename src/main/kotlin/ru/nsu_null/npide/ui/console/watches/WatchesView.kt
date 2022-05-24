package ru.nsu_null.npide.ui.console.watches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.nsu_null.npide.ui.GitBranchTellerView
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.console.Console

@Composable
fun WatchesView(
    modifier: Modifier,
    console: Console,
    onControlPanelSwitchRequest: () -> Unit,
    onCloseRequest: () -> Unit
) {
    Box(Modifier.fillMaxSize().padding(15.dp).then(modifier)) {
        Column(Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.Start) {
                    if (console.processIsAttached) {
                        Icon(Icons.Default.Done, "Process is attached", tint = Color.Green)
                    } else {
                        Icon(Icons.Default.DoNotTouch, "No process attached", tint = Color.Red)
                    }
                    Spacer(Modifier.padding(3.dp))
                    val processMessage = if (!console.processIsAttached)
                        "No process attached" else "Running '${console.attachedProcessLabel}'"
                    Text(processMessage, textAlign = TextAlign.Center)
                }
                Row(horizontalArrangement = Arrangement.End) {
                    Icon(Icons.Default.Settings, "Switch to control panel", tint = Color.LightGray,
                        modifier = Modifier.clickable { onControlPanelSwitchRequest() })
                    Spacer(Modifier.padding(3.dp))
                    Icon(Icons.Default.ArrowDownward, "Hide console", tint = Color.LightGray,
                        modifier = Modifier.clickable { onCloseRequest() })
                }
            }
            Divider(Modifier.padding(0.dp, 15.dp))
            Column(Modifier.fillMaxSize()) {
                val watchesRegex = Regex("([^= ]*)=([^= ]*)")

                val lastLinesN = 2
                val lastLines = console.content.takeLast(lastLinesN).map { it.text }.reduceRight(String::plus)

                val res = watchesRegex.findAll(lastLines).map { it.value }.reduceOrNull { acc, s -> "$acc,$s" }

                Box(
                    Modifier
                        .weight(0.8f)
                        .fillMaxWidth()
                        .background(AppTheme.colors.backgroundDark.copy(alpha = 0.5f))
                ) {
                    Text(res ?: "N/a", modifier = Modifier.padding(3.dp))
                }
                Spacer(Modifier.padding(2.dp))
                GitBranchTellerView(Modifier.weight(0.2f))
            }
        }
    }
}
