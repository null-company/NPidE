package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.editor.Editors
import java.io.IOException

class ButtonUsage(private val editors: Editors) {
    private fun fileExt(filename: String): String {
        return filename.substringAfterLast('.')
    }

    private fun runCommand(vararg arguments : String?){
        val process = ProcessBuilder(*arguments).start()
        process.inputStream.reader(Charsets.UTF_8).use {
            println(it.readText())
        }
        process.errorStream.reader(Charsets.UTF_8).use {
            println(it.readText())
        }
    }


    fun usageCompile() {
        when(fileExt(editors.openedFile.name)) {
            "c" -> {
                runCommand("gcc", editors.openedFile.filepath, "-o", editors.openedFile.parentPath + "/out.out")
            }
            "asm" -> {
                runCommand("python", editors.openedFile.parentPath + "/cocas.py", editors.openedFile.filepath, "-l")
            }
            else -> {
            println("It's not c or asm file")
        }
    }
}   fun usageRun() {
        when(fileExt(editors.openedFile.name)) {
            "c" -> {
                usageCompile()
                try {
                    runCommand(editors.openedFile.parentPath + "/out.out")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            "asm" -> {
                var new_filename: String = editors.openedFile.filepath.substringBeforeLast('.') + ".obj"
                usageCompile()
                try {
                    runCommand("python", editors.openedFile.parentPath + "/cocol.py", new_filename)
                    new_filename = editors.openedFile.filepath.substringBeforeLast('.') + ".img"
                    runCommand("python", editors.openedFile.parentPath + "/cdm8_emu_main.py", new_filename)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            else -> {
                println("It's not c or asm file")
            }
        }
    }
}