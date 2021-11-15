package ru.nsu_null.npide.ui.statusbar

import ru.nsu_null.npide.ui.editor.Editors

class ButtonUsage(var editors: Editors) {
    fun fileExt(filename:String): String {
        return filename.substringAfterLast('.')

    }

    fun runCommand(vararg arguments : String?){
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
            runCommand(editors.openedFile.parentpath+"/out.out")

        }else if(fileExt(editors.openedFile.name) == "asm"){
            val new_filename:String = editors.openedFile.filepath.substringBeforeLast('.') + ".obj"
            runCommand("python",editors.openedFile.parentpath + "/cocas.py", editors.openedFile.filepath, "-l")
            runCommand("mv", editors.openedFile.filepath, new_filename)
            runCommand("python",editors.openedFile.parentpath + "/cocol.py", new_filename, "-l")
        }else{
            println("It's not c or asm file")
        }

    }
}