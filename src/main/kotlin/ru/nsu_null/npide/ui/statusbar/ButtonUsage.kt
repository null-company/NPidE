package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.Parser
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import java.io.IOException

private var parser = Parser("CocoIDE-V1.91/config.yml")
private fun fileExt(filename: String): String {
    return filename.substringAfterLast('.')
}

private fun runCommand(arguments : String, console: Console){
    val process = Runtime.getRuntime().exec(arguments)
    process.inputStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
    process.errorStream.reader(Charsets.UTF_8).use {
        console.add(it.readText())
    }
}


fun usageCompile(editors: Editors, console: Console) {
    when(fileExt(editors.openedFile.name)) {
        "c" -> {
            runCommand("gcc" + editors.openedFile.filepath + "-o" + editors.openedFile.parentPath + "/out.out",
                console)
        }
        "asm" -> {
            for (i in 0 until parser.result.build.count()) {
                val preCommand = listOfNotNull(parser.result.build[i].exec,
                    parser.result.build[i].beforeFiles,
                    (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                        parser.result.build[i].changeExt)),
                    parser.result.build[i].afterFiles)
                val command = parser.addSpaces(preCommand)
                runCommand(command, console)
            }
        }
        else -> {
            println("It's not c or asm file")
        }
    }
}

fun usageRun(editors: Editors, console: Console) {
    when(fileExt(editors.openedFile.name)) {
        "c" -> {
            usageCompile(editors, console)
            try {
                runCommand(editors.openedFile.parentPath + "/out.out", console)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        "asm" -> {
            try {
                for (i in 0 until parser.result.run.count()) {
                    val preCommand = listOfNotNull(parser.result.run[i].exec,
                        parser.result.run[i].beforeFiles,
                        (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                            parser.result.run[i].changeExt)),
                        parser.result.run[i].afterFiles)
                    println(parser.result.run[i].exec+
                            parser.result.run[i].beforeFiles+
                            (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                                parser.result.run[i].changeExt))+
                            parser.result.run[i].afterFiles)
                    val command = parser.addSpaces(preCommand)
                    runCommand(command, console)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else -> {
            println("It's not c or asm file")
        }
    }
}

fun usageDebug(editors: Editors, console: Console) {
    when(fileExt(editors.openedFile.name)) {
        "asm" -> {
            try {
                for (i in 0 until parser.result.debug.count()) {
                    val preCommand = listOfNotNull(parser.result.debug[i].exec,
                        parser.result.debug[i].beforeFiles,
                        (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                            parser.result.debug[i].changeExt)),
                        parser.result.debug[i].afterFiles)
                    val command = parser.addSpaces(preCommand)
                    runCommand(command, console)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else -> {
            println("It's not asm file")
        }
    }
}
