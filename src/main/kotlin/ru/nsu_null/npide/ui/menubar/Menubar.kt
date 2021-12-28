package ru.nsu_null.npide.ui.menubar

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import ru.nsu_null.npide.ui.menubar.ConfigureProjectAction.*
import ru.nsu_null.npide.ui.config.ConfigManager
import javax.swing.JFileChooser

private enum class ConfigureProjectAction {
    ChooseBuild,
    ChooseRun,
    ChooseDebug,
    ChooseGrammar,
    ChooseSyntaxHighlighter,
}

@Composable
fun FrameWindowScope.configureMenuBar() = MenuBar {
            Menu("Configurations", mnemonic = 'C') {
                Item("Config Build", onClick = {fileChooser(ChooseBuild) })
                Item("Config Run", onClick = {fileChooser(ChooseRun) })
                Item("Config Debug", onClick = {fileChooser(ChooseDebug) })
                Item("Config Grammar", onClick = {fileChooser(ChooseGrammar) })
                Item("Config Syntax Highlighter", onClick = {fileChooser(ChooseSyntaxHighlighter) })
                Item("Check", onClick = {ConfigManager.readFileBuilt("cock.txt")})
            }
}

private fun fileChooser(action: ConfigureProjectAction){
    JFileChooser(System.getProperty("user.home")).apply {
        showOpenDialog(null)
        if (selectedFile != null) {
            when (action) {
                ChooseBuild -> {
                    ConfigManager.currentProjectConfig.build = selectedFile.toString()
                }
                ChooseRun -> {
                    ConfigManager.currentProjectConfig.run = selectedFile.toString()
                }
                ChooseDebug -> {
                    ConfigManager.currentProjectConfig.debug = selectedFile.toString()
                }
                ChooseGrammar ->{
                    ConfigManager.currentProjectConfig.grammar = selectedFile.toString()
                }
                ChooseSyntaxHighlighter -> {
                    ConfigManager.currentProjectConfig.syntaxHighlighter = selectedFile.toString()
                }
            }
        }
    }
}
