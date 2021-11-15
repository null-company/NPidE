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

    fun usage() {
        if(fileExt(editors.openedFile.name) == "c") {
            runCommand("gcc", editors.openedFile.filepath, "-o", editors.openedFile.parentpath + "/out.out")
            try {
                runCommand(editors.openedFile.parentpath + "/out.out")
            } catch (e: IOException) {
                // usually for the case of compilation failure
                e.printStackTrace()
            }
        } else {
            println("It's not c file")
        }
    }
}