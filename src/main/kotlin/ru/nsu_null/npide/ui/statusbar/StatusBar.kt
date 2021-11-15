package ru.nsu_null.npide.ui.statusbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.editor.Editors
import ru.nsu_null.npide.ui.statusbar.ButtonUsage

private val MinFontSize = 6.sp
private val MaxFontSize = 40.sp

@Composable
fun StatusBar(settings: Settings, editors: Editors) = Box(
    Modifier
        .height(32.dp)
        .fillMaxWidth()
        .padding(4.dp)
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {

        Button(onClick = {var buttonUsage = ButtonUsage(editors)
            buttonUsage.usage()}, contentPadding = PaddingValues(), modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = "Compile",
                color = LocalContentColor.current.copy(alpha = 0.60f),
                fontSize = 12.sp
            )
        }

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

private fun Density.scale(scale: Float) = Density(density * scale, fontScale * scale)
private operator fun TextUnit.minus(other: TextUnit) = (value - other.value).sp
private operator fun TextUnit.div(other: TextUnit) = value / other.value