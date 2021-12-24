package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.yaml.ConfigParser
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import java.io.IOException

private var parser = ConfigParser()
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
            for (i in 0 until parser.resultBuild.build.count()) {
                val preCommand = listOfNotNull(parser.resultBuild.build[i].exec,
                    parser.resultBuild.build[i].beforeFiles,
                    (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                        parser.resultBuild.build[i].changeExt)),
                    parser.resultBuild.build[i].afterFiles)
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
                for (i in 0 until parser.resultRun.run.count()) {
                    val preCommand = listOfNotNull(parser.resultRun.run[i].exec,
                        parser.resultRun.run[i].beforeFiles,
                        (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                            parser.resultRun.run[i].changeExt)),
                        parser.resultRun.run[i].afterFiles)
                    println(parser.resultRun.run[i].exec+
                            parser.resultRun.run[i].beforeFiles+
                            (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                                parser.resultRun.run[i].changeExt))+
                            parser.resultRun.run[i].afterFiles)
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
                for (i in 0 until parser.resultDebug.debug.count()) {
                    val preCommand = listOfNotNull(parser.resultDebug.debug[i].exec,
                        parser.resultDebug.debug[i].beforeFiles,
                        (editors.openedFile.parentPath + "/" + parser.changeExt(editors.openedFile.name,
                            parser.resultDebug.debug[i].changeExt)),
                        parser.resultDebug.debug[i].afterFiles)
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
