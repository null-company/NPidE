package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ru.nsu_null.npide.ui.menubar.configdialog.ConfigDialog
import ru.nsu_null.npide.ui.npide.NPIDE

@ExperimentalComposeUiApi
@Composable
fun FrameWindowScope.CustomMenuBar() = MenuBar {
    val configDialogIsOpen = remember { mutableStateOf(false) }
    if (configDialogIsOpen.value) {
        ConfigDialog(configDialogIsOpen)
    }
    Menu("Menu", mnemonic = 'C') {
        Item("Config", onClick = { configDialogIsOpen.value = true })
        Item("Choose Project", onClick = { NPIDE.openChooseProject() })
    }
}
