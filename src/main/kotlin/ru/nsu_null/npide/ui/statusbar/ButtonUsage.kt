package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.yaml.ConfigParser
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import java.io.IOException

private var parser = ConfigParser()

private fun runCommand(arguments : String, console: Console){
    val process = Runtime.getRuntime().exec(arguments)
    process.inputStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
    process.errorStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
}
fun usageButton(editors: Editors, console: Console, config: ConfigParser.Config) {
    try {
        for (i in 0 until config.build.count()) {
            val preCommand = listOfNotNull(config.build[i].exec,
                config.build[i].beforeFiles,
                (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                    config.build[i].changeExt)),
                config.build[i].afterFiles)
            val command = parser.addSpaces(preCommand)
            runCommand(command, console)
        }
    }catch(e: IOException){
        e.printStackTrace()
    }
}

fun usageCompile(editors: Editors, console: Console) {
    usageButton(editors, console, parser.resultBuild)
}

fun usageRun(editors: Editors, console: Console) {
    usageButton(editors, console, parser.resultRun)
}

fun usageDebug(editors: Editors, console: Console) {
    usageButton(editors, console, parser.resultDebug)
}
