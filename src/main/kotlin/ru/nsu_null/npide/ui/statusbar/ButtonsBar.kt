package ru.nsu_null.npide.ui.statusbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors

private val MinFontSize = 6.sp
private val MaxFontSize = 40.sp

@Composable
fun ButtonsBar(settings: Settings, editors: Editors, console: Console) = Box(
    Modifier
        .height(32.dp)
        .fillMaxWidth()
        .padding(4.dp)
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {

        ButtonsBarButton("Compile") { usageCompile(editors, console) }

        ButtonsBarButton("Run") { usageRun(editors, console) }

        ButtonsBarButton("Debug") { usageDebug(editors, console) }

        ButtonsBarButton("Save file") { editors.active!!.writeContents(editors.active!!.content) }

        Row {
            Text(
                text = "Text size",
                modifier = Modifier.align(Alignment.CenterVertically),
                color = LocalContentColor.current.copy(alpha = 0.60f),
                fontSize = 12.sp
            )

            Spacer(Modifier.width(8.dp))


            CompositionLocalProvider(LocalDensity provides LocalDensity.current.scale(0.5f)) {
                Slider(
                    (settings.fontSize - MinFontSize) / (MaxFontSize - MinFontSize),
                    onValueChange = { settings.fontSize = lerp(MinFontSize, MaxFontSize, it) },
                    modifier = Modifier.width(240.dp).align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun RowScope.ButtonsBarButton(name: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        contentPadding = PaddingValues(),
        modifier = Modifier.align(Alignment.CenterVertically)) {
        Text(
            text = name,
            color = LocalContentColor.current.copy(alpha = 0.60f),
            fontSize = 12.sp
        )
    }
}

private fun Density.scale(scale: Float) = Density(density * scale, fontScale * scale)
private operator fun TextUnit.minus(other: TextUnit) = (value - other.value).sp
private operator fun TextUnit.div(other: TextUnit) = value / other.value
