package ru.nsu_null.npide

import ProjectView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import ru.nsu_null.npide.ui.npide.NPIDE
import ru.nsu_null.npide.ui.npide.NPIDE.State
import ru.nsu_null.npide.ui.npide.NPIDE.State.*
import ru.nsu_null.npide.ui.projectchooser.ProjectChooserView

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
fun main() = singleWindowApplication(
    title = "NPidE",
    state = WindowState(width = 1280.dp, height = 768.dp),
    icon = BitmapPainter(useResource("npide.png", ::loadImageBitmap)),
) {
    when(NPIDE.state) {
        CHOOSING_PROJECT -> ProjectChooserView()
        IN_PROJECT -> ProjectView()
    }
}