package ru.nsu_null.npide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import ru.nsu_null.npide.ide.codeviewer.ProjectView
import ru.nsu_null.npide.ide.menubar.CustomMenuBar
import ru.nsu_null.npide.ide.npide.NPIDE
import ru.nsu_null.npide.ide.npide.NPIDE.State.CHOOSING_PROJECT
import ru.nsu_null.npide.ide.npide.NPIDE.State.IN_PROJECT
import ru.nsu_null.npide.ide.projectchooser.ProjectChooserView

val icon = BitmapPainter(useResource("npide.png", ::loadImageBitmap))
const val npideTitle = "NPidE"

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
fun main() = application {

    var isFullscreen by remember { mutableStateOf(false) }
    val switchFullscreen = {
        isFullscreen = !isFullscreen
    }
    val onClose = {
        exitApplication()
    }

    if (isFullscreen) {
        FullscreenWindow(onClose, switchFullscreen) {
            NpideView()
        }
    } else {
        FloatingWindow(onClose, switchFullscreen) {
            NpideView()
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FloatingWindow(onCloseRequest: () -> Unit,
                   onSwitchFullscreenRequest: () -> Unit,
                   content: @Composable FrameWindowScope.() -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        title = npideTitle,
        undecorated = false,
        state = WindowState(width = 1280.dp, height = 768.dp),
        icon = icon,
        onKeyEvent = { e ->
            if (e.key == Key.F11 && e.type == KeyEventType.KeyDown) {
                onSwitchFullscreenRequest()
                true
            } else {
                false
            }
        }
    ) {
        content()
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FullscreenWindow(onCloseRequest: () -> Unit,
                     onSwitchFullscreenRequest: () -> Unit,
                     content: @Composable FrameWindowScope.() -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        title = npideTitle,
        undecorated = true,
        state = WindowState(placement = WindowPlacement.Fullscreen),
        icon = icon,
        onKeyEvent = { e ->
            if (e.key == Key.F11 && e.type == KeyEventType.KeyDown) {
                onSwitchFullscreenRequest()
                true
            } else {
                false
            }
        }
    ) {
        content()
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FrameWindowScope.NpideView() {
    CustomMenuBar()
    when(NPIDE.state) {
        CHOOSING_PROJECT -> ProjectChooserView()
        IN_PROJECT -> ProjectView()
    }
}
