package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

@Composable
fun FrameWindowScope.configureMenuBar() = MenuBar {
    val state = remember { mutableStateOf(false) }
    if (state.value){
        val c = ConfigDialog()
        c.run(state)
    }
    Menu("Configurations", mnemonic = 'C') {
                Item("Config", onClick = { state.value = true})
            }
}
