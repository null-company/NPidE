package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ru.nsu_null.npide.ui.menubar.configdialog.ConfigDialog

@ExperimentalComposeUiApi
@Composable
fun FrameWindowScope.configureMenuBar() = MenuBar {
    val configDialogIsOpen = remember { mutableStateOf(false) }
    if (configDialogIsOpen.value) {
        ConfigDialog(configDialogIsOpen)
    }
    Menu("Configurations", mnemonic = 'C') {
        Item("Config", onClick = { configDialogIsOpen.value = true })
    }
}
