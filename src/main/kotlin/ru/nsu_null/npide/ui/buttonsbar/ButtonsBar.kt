package ru.nsu_null.npide.ui.buttonsbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {

        ButtonsBarButton("Build") { build(editors, console) }

        ButtonsBarButton("Run") { run(editors, console) }

        ButtonsBarButton("Debug") { debug(editors, console) }

        ButtonsBarButton("Save") { editors.active!!.writeContents(editors.active!!.content) }

        ButtonsBarButton("Step") { DebugRunnableStepFlag.set(true) }

    }
}

@Composable
fun RowScope.ButtonsBarButton(name: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        modifier = Modifier.align(Alignment.CenterVertically)
    ) {
        Text(
            text = name,
            color = LocalContentColor.current.copy(alpha = 0.60f),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

private fun Density.scale(scale: Float) = Density(density * scale, fontScale * scale)
private operator fun TextUnit.minus(other: TextUnit) = (value - other.value).sp
private operator fun TextUnit.div(other: TextUnit) = value / other.value
