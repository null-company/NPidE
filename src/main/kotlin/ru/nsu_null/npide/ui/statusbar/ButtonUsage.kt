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
        if (fileExt(editors.openedFile.name) == "c") {
            runCommand("gcc", editors.openedFile.filepath, "-o", editors.openedFile.parentpath + "/out.out")
        } else if (fileExt(editors.openedFile.name) == "asm") {
            runCommand("python", editors.openedFile.parentpath + "/cocas.py", editors.openedFile.filepath, "-l")
        } else {
            println("It's not c or asm file")
        }
    }
    fun usageRun() {
        if(fileExt(editors.openedFile.name) == "c") {
            usageCompile()
            try {
                runCommand(editors.openedFile.parentpath + "/out.out")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else if(fileExt(editors.openedFile.name) == "asm"){
            var new_filename:String = editors.openedFile.filepath.substringBeforeLast('.') + ".obj"
            usageCompile()
            try{
                runCommand("python",editors.openedFile.parentpath + "/cocol.py", new_filename)
                new_filename = editors.openedFile.filepath.substringBeforeLast('.') + ".img"
                runCommand("python",editors.openedFile.parentpath + "/cdm8_emu_main.py", new_filename)

            }catch (e: IOException) {
                e.printStackTrace()
            }
        }else {
            println("It's not c or asm file")
        }
    }
}