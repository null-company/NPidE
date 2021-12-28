package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.ui.config.ConfigParser
import java.io.IOException

private var parser = ConfigParser()

private fun runCommand(arguments : String, console: Console): Boolean {
    val process = Runtime.getRuntime().exec(arguments)
    process.inputStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
    process.errorStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
    if (process.exitValue() == 0)
        return true
    else
        return false
}
fun usageButton(editors: Editors, console: Console, config: List<ConfigParser.ConfigInternal>): Boolean {
    try {
        for (i in 0 until config.count()) {
            val preCommand = listOfNotNull(
                config[i].exec,
                config[i].beforeFiles,
                (editors.openedFile.parentPath + "/" + parser.changeExt(
                    editors.openedFile.name,
                    config[i].changeExt
                )),
                config[i].afterFiles
            )
            val command = parser.addSpaces(preCommand)
            return runCommand(command, console)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return false
}

fun usageCompile(editors: Editors, console: Console) {
    val flagBuilt = ConfigManager.readFileDirtiness(editors.openedFile.filepath)
    if (!flagBuilt!!) {
        ConfigManager.setFileDirtiness(editors.openedFile.filepath,
            !usageButton(editors, console, parser.resultBuild.build))
    }
}

fun usageRun(editors: Editors, console: Console) {
    usageCompile(editors, console)
    usageButton(editors, console, parser.resultRun.run)
}

fun usageDebug(editors: Editors, console: Console) {
    usageButton(editors, console, parser.resultDebug.debug)
}
