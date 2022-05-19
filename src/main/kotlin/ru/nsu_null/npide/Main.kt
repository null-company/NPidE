package ru.nsu_null.npide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import ru.nsu_null.npide.ui.ProjectView
import ru.nsu_null.npide.ui.menubar.CustomMenuBar
import ru.nsu_null.npide.ui.npide.NPIDE
import ru.nsu_null.npide.ui.npide.NPIDE.State.CHOOSING_PROJECT
import ru.nsu_null.npide.ui.npide.NPIDE.State.IN_PROJECT
import ru.nsu_null.npide.ui.projectchooser.ProjectChooserView

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
fun main() {

    lateinit var windowCaptured: ComposeWindow
    fun switchFullscreen() {
        if (windowCaptured.placement == WindowPlacement.Fullscreen) {
            windowCaptured.placement = WindowPlacement.Floating
        } else {
            windowCaptured.placement = WindowPlacement.Fullscreen
        }
    }

    singleWindowApplication(
        title = "NPidE",
        state = WindowState(width = 1280.dp, height = 768.dp),
        icon = BitmapPainter(useResource("npide.png", ::loadImageBitmap)),
        onKeyEvent = { e ->
            if (e.key == Key.F11 && e.type == KeyEventType.KeyDown) {
                switchFullscreen()
                true
            } else {
                false
            }
        }
    ) {
        windowCaptured = window
        CustomMenuBar()
        when(NPIDE.state) {
            CHOOSING_PROJECT -> ProjectChooserView()
            IN_PROJECT -> ProjectView()
        }
    }
}
