package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import javax.swing.JFileChooser
import ru.nsu_null.npide.ui.yaml.ProjectFile

@Composable
fun FrameWindowScope.configureMenuBar() = MenuBar {
            Menu("Configurations", mnemonic = 'C') {
                Item("Config Build", onClick = {fileChooser(0) })
                Item("Config Run", onClick = {fileChooser(1) })
                Item("Config Debug", onClick = {fileChooser(2) })
            }
}

fun fileChooser(configButtonState :Int){
    JFileChooser(System.getProperty("user.home")).apply {
        showOpenDialog(null)
        if (selectedFile != null) {
            when (configButtonState) {
                0 -> {
                    ProjectFile.buildPath = selectedFile.toString()
                    ProjectFile.serialize()
                }
                1 -> {
                    ProjectFile.runPath = selectedFile.toString()
                    ProjectFile.serialize()
                }
                2 -> {
                    ProjectFile.debugPath = selectedFile.toString()
                    ProjectFile.serialize()
                }
            }
        }
    }
}
