package ru.nsu_null.npide.ide.buttonsbar

import ru.nsu_null.npide.ide.breakpoints.BreakpointStorage
import ru.nsu_null.npide.ide.config.ConfigParser
import ru.nsu_null.npide.ide.console.Console
import ru.nsu_null.npide.ide.editor.Editors
import ru.nsu_null.npide.ide.npide.NPIDE
import java.io.File
import java.io.IOException
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

private val parser = ConfigParser()

object DebugRunnableStepFlag : AtomicBoolean(true)

fun debugRun(console: Console, command: List<String>) {
    val dir  = File(NPIDE.currentProject!!.rootFolder.filepath)
    val process = Runtime.getRuntime().exec(
        command.map { it.trim() }.filter { it.isNotEmpty() }.toTypedArray(),
        null,
        dir
    )
    val acc = StringBuilder("")
    val inputStreamReader = process.inputStream.reader(Charsets.UTF_8)
    val errorStreamReader = process.errorStream.reader(Charsets.UTF_8)
    val writer = process.outputStream.writer(Charsets.UTF_8)

    val readAndAccumulate = {
        while (inputStreamReader.ready()) {
            val newChar = inputStreamReader.read()
            if (newChar == -1)
                break
            acc.append(Char(newChar))
        }

        while (errorStreamReader.ready()) {
            val newChar = inputStreamReader.read()
            if (newChar == -1)
                break
            acc.append(Char(newChar))
        }
    }

    while (process.isAlive) {
        sleep(100)

        readAndAccumulate()
        console.display(acc.toString())
        acc.clear()

        if (DebugRunnableStepFlag.get()) {
            DebugRunnableStepFlag.set(false)
            try {
                writer.write("s\n")
                writer.flush()
            } catch (e: IOException) {
                println("[LOG] Debug process stdin failed:")
                print("[LOG] ")
                println(e)
                println("[LOG] Aborting process...")
                break
            }

        }
    }
    readAndAccumulate()
    console.display(acc.toString())
}

lateinit var DebugThread: Thread

private fun runCommand(arguments: List<String>, console: Console): Boolean {
    val argumentsArr = arguments.filter { it.trim().isNotEmpty() }.toTypedArray()

    val dir  = File(NPIDE.currentProject!!.rootFolder.filepath)

    val process = Runtime.getRuntime().exec(argumentsArr, null, dir)

    val result = process.onExit().thenApply { p ->
        p.inputStream.reader(Charsets.UTF_8).use {
            val s = it.readText()
            console.display(s)
        }
        p.errorStream.reader(Charsets.UTF_8).use {
            val s = it.readText()
            console.display(s)
        }
        p.exitValue() == 0
    }

    return result.get()
}

private fun buildCommand(
    python_file: String,
    flag: String,
    name: String,
    root_folder: String,
    files: List<String>,
    entry_point: String,
    ext: String,
    bpStr: String = ""
): List<String> {
    return listOf<String>()+ "python"+
        python_file +
        "-f" +
        flag +
        "-n" +
        name +
        "-d" +
        root_folder +
        "-p" +
        files +
        "-e" +
        entry_point +
        "-ext" +
        ext +
        (if (bpStr.isNotEmpty()) "-b" else "") +
        bpStr
}

private fun runWithConfig(editors: Editors,
                  console: Console,
                  config: List<ConfigParser.ConfigInternal>): Boolean {
    try {
        for (i in 0 until config.count()) {
            val command = buildCommand(
                config[i].python_file,
                "run",
                config[i].name,
                NPIDE.currentProject!!.rootFolder.filepath,
                NPIDE.configManager.currentProjectConfig.projectFilePaths,
                config[i].entry_point,
                config[i].ext
            )
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
            val command = buildCommand(
                config[i].python_file,
                "debug",
                config[i].name,
                NPIDE.currentProject!!.rootFolder.filepath,
                NPIDE.configManager.currentProjectConfig.projectFilePaths,
                config[i].entry_point,
                config[i].ext
            )
            DebugThread = thread {
                debugRun(console, command)
            }
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
            val bps = NPIDE.breakpointStorage
            var bpStr = ""
            for (key in bps.map.keys) {
                bpStr += key
                bpStr += ":: "
                for (bp in bps.map[key]!!) {
                    bpStr += bp
                    bpStr += ", "
                }
                bpStr = bpStr.subSequence(0, bpStr.length - 2).toString()
                bpStr += "; "
            }
            bpStr =
                if (bpStr.isEmpty()) ""
                else bpStr.subSequence(0, bpStr.length - 2).toString()
            val command =  buildCommand(
                config[i].python_file,
                "build",
                config[i].name,
                NPIDE.currentProject!!.rootFolder.filepath,
                NPIDE.configManager.currentProjectConfig.projectFilePaths,
                config[i].entry_point,
                config[i].ext,
                bpStr
            )
            println(command)
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
    // TODO fix dirty flag checking
    buildWithConfig(editors, console, parser.resultBuild.build)
}

fun run(editors: Editors, console: Console) {
    build(editors, console)
    runWithConfig(editors, console, parser.resultRun.run)
}

fun debug(editors: Editors, console: Console) {
    debugWithConfig(editors, console, parser.resultDebug.debug)
}
