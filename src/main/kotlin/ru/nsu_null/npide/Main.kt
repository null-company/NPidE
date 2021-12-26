package ru.nsu_null.npide

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import MainView
import ru.nsu_null.npide.parser.generator.G4LanguageManager

@OptIn(ExperimentalComposeUiApi::class)
fun main() = singleWindowApplication(
    //TODO Where
    title = "Code Viewer",
    state = WindowState(width = 1280.dp, height = 768.dp),
    icon = BitmapPainter(useResource("ic_launcher.png", ::loadImageBitmap)),
) {
    G4LanguageManager.baseDir = "./src/main/java"
    MainView()
}