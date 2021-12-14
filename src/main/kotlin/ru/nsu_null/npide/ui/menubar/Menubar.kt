package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

@Composable
fun FrameWindowScope.configureMenuBar() = MenuBar {
            Menu("File", mnemonic = 'F') {
                Item("Create", onClick = {})
                Item("Save", onClick = {})
            }
}
