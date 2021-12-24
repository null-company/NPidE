package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import javax.swing.JFileChooser
import ru.nsu_null.npide.ui.yaml.ConfigManager

@Composable
fun FrameWindowScope.configureMenuBar() = MenuBar {
            Menu("Configurations", mnemonic = 'C') {
                Item("Config Build", onClick = {fileChooser(0) })
                Item("Config Run", onClick = {fileChooser(1) })
                Item("Config Debug", onClick = {fileChooser(2) })
                Item("add cockTrue", onClick = {ConfigManager.setFileBuilt("cock.txt", true)})
                Item("add cockFalse", onClick = {ConfigManager.setFileBuilt("cock.txt", false)})
                Item("Check", onClick = {ConfigManager.readFileBuilt("cock.txt")})

            }
}

fun fileChooser(configButtonState :Int){
    JFileChooser(System.getProperty("user.home")).apply {
        showOpenDialog(null)
        if (selectedFile != null) {
            when (configButtonState) {
                0 -> {
                    ConfigManager.buildPath = selectedFile.toString()
                    ConfigManager.storeConfig()
                }
                1 -> {
                    ConfigManager.runPath = selectedFile.toString()
                    ConfigManager.storeConfig()
                }
                2 -> {
                    ConfigManager.debugPath = selectedFile.toString()
                    ConfigManager.storeConfig()
                }
            }
        }
    }
}
