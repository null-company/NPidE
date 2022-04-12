package ru.nsu_null.npide.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FilenameFilter

interface File {
    val name: String
    val filepath: String
    val parentPath: String
    val isDirectory: Boolean
    val children: List<File>
    val hasChildren: Boolean

    fun readContents(scope: CoroutineScope): String
    fun writeContents(scope: CoroutineScope, content: String)
}

val HomeFolder: File get() = java.io.File(System.getProperty("user.home")).toProjectFile()

fun java.io.File.toProjectFile(): File = object : File {
    override val name: String get() = this@toProjectFile.name

    override val filepath: String get() = this@toProjectFile.absolutePath

    override val parentPath: String get() = this@toProjectFile.parent


    override val isDirectory: Boolean get() = this@toProjectFile.isDirectory

    override val children: List<File>
        get() = this@toProjectFile
            .listFiles(FilenameFilter { _, name -> !name.startsWith(".") })
            .orEmpty()
            .map { it.toProjectFile() }

    override val hasChildren: Boolean
        get() = isDirectory && listFiles()?.size ?: 0 > 0


    override fun readContents(scope: CoroutineScope): String {
        require(this@toProjectFile.length() < 50L * 1024 * 1024) {
            "big files aren't supported"
        }
        // todo consider using scope with io dispatcher for faster return
        return this@toProjectFile.readText()
    }

    override fun writeContents(scope: CoroutineScope, content: String) {
        scope.launch(Dispatchers.IO) {
            this@toProjectFile.writeText(content)
        }
    }
}


