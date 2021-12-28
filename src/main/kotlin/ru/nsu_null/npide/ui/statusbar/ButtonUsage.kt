package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.breakpoints.BreakpointStorage
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.ui.config.ConfigParser
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicBoolean

private var parser = ConfigParser()
object DebugRunnableStepFlag : AtomicBoolean(true)

class DebugRunnable(
    val console: Console,
    private val command: String,
    ) : Runnable {
    override fun run() {
        val process = Runtime.getRuntime().exec(command, arrayOf(), File(System.getProperty("user.dir")))
        var acc = ""
        val inputStreamReader = process.inputStream.reader(Charsets.UTF_8)
        val errorStreamReader = process.errorStream.reader(Charsets.UTF_8)
        val writer = process.outputStream.writer(Charsets.UTF_8)
        while(process.isAlive) {
            sleep(100)
            while(inputStreamReader.ready()) {
                val newChar = inputStreamReader.read()
                acc += Char(newChar)
            }

            while(errorStreamReader.ready()) {
                val newChar = inputStreamReader.read()
                acc += Char(newChar)
            }

            if (DebugRunnableStepFlag.get()) {
                DebugRunnableStepFlag.set(false)
                console.add(acc)
                acc = ""
                writer.write("s\n")
                writer.flush()
            }
        }
        while(inputStreamReader.ready()) {
            val newChar = inputStreamReader.read()
            acc += Char(newChar)
        }
        console.add(acc)
    }
}

var DebugThread : Thread = Thread()

private fun runCommand(arguments : String, console: Console): Boolean {
    val process = Runtime.getRuntime().exec(arguments, arrayOf(), File(System.getProperty("user.dir")))
    process.inputStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
    process.errorStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
    return process.exitValue() == 0
}
fun usageButton(editors: Editors, console: Console, config: List<ConfigParser.ConfigInternal>): Boolean {
    try {
        for (i in 0 until config.count()) {
            val preCommand = listOfNotNull(
                config[i].exec,
                config[i].beforeFiles,
                ("\"" + File(editors.openedFile.parentPath + "/" + parser.changeExt(
                    editors.openedFile.name,
                    config[i].changeExt
                )).absoluteFile + "\""),
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

fun usageButtonDebug(editors: Editors, console: Console, config: List<ConfigParser.ConfigInternal>) {
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
            DebugThread = Thread(DebugRunnable(console, command))
            DebugThread.start()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun usageButtonCompile(editors: Editors, console: Console, config: List<ConfigParser.ConfigInternal>): Boolean {
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
                    ("\"" + File(editors.openedFile.parentPath + "/" + parser.changeExt(
                            editors.openedFile.name,
                            config[i].changeExt
                    )).absoluteFile + "\""),
                    config[i].afterFiles,
                    "-b \"$bpStr\""
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
            !usageButtonCompile(editors, console, parser.resultBuild.build))
    }
}

fun usageRun(editors: Editors, console: Console) {
    usageCompile(editors, console)
    usageButton(editors, console, parser.resultRun.run)
}

fun usageDebug(editors: Editors, console: Console) {
    usageButtonDebug(editors, console, parser.resultDebug.debug)
}
