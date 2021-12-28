package ru.nsu_null.npide

import MainView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ru.nsu_null.npide.ui.menubar.configureMenuBar

@ExperimentalFoundationApi
@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Code Viewer",
        state = WindowState(width = 1280.dp, height = 768.dp),
        icon = BitmapPainter(useResource("ic_launcher.png", ::loadImageBitmap)),
    ) {
        MainView()
        configureMenuBar()
    }
}