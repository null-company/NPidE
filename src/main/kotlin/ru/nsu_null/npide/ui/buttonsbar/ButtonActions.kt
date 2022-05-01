package ru.nsu_null.npide.ui.buttonsbar

import ru.nsu_null.npide.breakpoints.BreakpointStorage
import ru.nsu_null.npide.ui.config.ConfigParser
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import ru.nsu_null.npide.ui.npide.NPIDE
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicBoolean

private val parser = ConfigParser()

object DebugRunnableStepFlag : AtomicBoolean(true)

private fun debugRun(console: Console, command: String) {
    val process = Runtime.getRuntime().exec(command, arrayOf(), File(System.getProperty("user.dir")))
    var acc = ""
    val inputStreamReader = process.inputStream.reader(Charsets.UTF_8)
    val errorStreamReader = process.errorStream.reader(Charsets.UTF_8)
    val writer = process.outputStream.writer(Charsets.UTF_8)
    while (process.isAlive) {
        sleep(100)
        while (inputStreamReader.ready()) {
            val newChar = inputStreamReader.read()
            acc += Char(newChar)
        }

        while (errorStreamReader.ready()) {
            val newChar = inputStreamReader.read()
            acc += Char(newChar)
        }

        if (DebugRunnableStepFlag.get()) {
            DebugRunnableStepFlag.set(false)
            console.display(acc)
            acc = ""
            writer.write("s\n")
            writer.flush()
        }
    }
    while (inputStreamReader.ready()) {
        val newChar = inputStreamReader.read()
        acc += Char(newChar)
    }
    console.display(acc)
}

lateinit var DebugThread: Thread

private fun runCommand(arguments: String, console: Console): Boolean {
    val process = Runtime.getRuntime().exec(arguments, arrayOf(), File(System.getProperty("user.dir")))
    process.inputStream.reader(Charsets.UTF_8).use {
        console.display(it.readText())
    }
    process.errorStream.reader(Charsets.UTF_8).use {
        console.display(it.readText())
    }
    return process.exitValue() == 0
}

private fun runWithConfig(editors: Editors,
                  console: Console,
                  config: List<ConfigParser.ConfigInternal>): Boolean {
    try {
        for (i in 0 until config.count()) {
            val preCommand = listOfNotNull(
                config[i].exec,
                config[i].beforeFiles,
                ("\"" + File(
                    editors.openedFile.parentPath + "/" + parser.changeExt(
                        editors.openedFile.name,
                        config[i].changeExt
                    )
                ).absoluteFile + "\""),
                config[i].afterFiles
            )
            val command = parser.addSpaces(preCommand)
            if (!runCommand(command, console)) {
                return false
            }
        }
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

private fun debugWithConfig(editors: Editors, console: Console, config: List<ConfigParser.ConfigInternal>) {
    try {
        for (i in 0 until config.count()) {
            val preCommand = listOfNotNull(
                config[i].exec,
                config[i].beforeFiles,
                ("\"" + editors.openedFile.parentPath + "/" + parser.changeExt(
                    editors.openedFile.name,
                    config[i].changeExt
                ) + "\""),
                config[i].afterFiles
            )
            val command = parser.addSpaces(preCommand)
            DebugThread = Thread {
                debugRun(console, command)
            }.also { it.start() }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun buildWithConfig(editors: Editors,
                    console: Console,
                    config: List<ConfigParser.ConfigInternal>): Boolean {
    try {
        for (i in 0 until config.count()) {
            var bpStr = ""
            for (key in BreakpointStorage.map.keys) {
                bpStr += key
                bpStr += ":: "
                for (bp in BreakpointStorage.map[key]!!) {
                    bpStr += bp
                    bpStr += ", "
                }
                bpStr = bpStr.subSequence(0, bpStr.length - 2).toString()
                bpStr += "; "
            }
            bpStr = bpStr.subSequence(0, bpStr.length - 2).toString()
            val preCommand = listOfNotNull(
                config[i].exec,
                config[i].beforeFiles,
                ("\"" + File(
                    editors.openedFile.parentPath + "/" + parser.changeExt(
                        editors.openedFile.name,
                        config[i].changeExt
                    )
                ).absoluteFile + "\""),
                config[i].afterFiles,
                "-b \"$bpStr\""
            )
            val command = parser.addSpaces(preCommand)
            if(!runCommand(command, console)) {
                return false
            }
        }
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

fun build(editors: Editors, console: Console) {
    val flagBuilt = NPIDE.configManager.readFileDirtiness(editors.openedFile.filepath)
    if (!flagBuilt) {
        NPIDE.configManager.setFileDirtiness(
            editors.openedFile.filepath,
            !buildWithConfig(editors, console, parser.resultBuild.build)
        )
    }
}

fun run(editors: Editors, console: Console) {
    build(editors, console)
    runWithConfig(editors, console, parser.resultRun.run)
}

fun debug(editors: Editors, console: Console) {
    debugWithConfig(editors, console, parser.resultDebug.debug)
}
