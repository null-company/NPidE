package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.editor.Editors

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
            runCommand(editors.openedFile.parentpath + "/out.out")
        } else {
            println("It's not c file")
        }
    }
}