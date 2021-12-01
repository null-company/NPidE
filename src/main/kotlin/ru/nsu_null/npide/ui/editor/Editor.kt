package ru.nsu_null.npide.ui.editor

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.util.SingleSelection

class Editor(
    val fileName: String,
    val fileContents: (backgroundScope: CoroutineScope) -> MutableState<String>,
) {
    var close: (() -> Unit)? = null
    lateinit var selection: SingleSelection

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    val isCode = fileName.endsWith(".kt", ignoreCase = true)
}

fun Editor(file: File) = Editor(
    fileName = file.name
) { backgroundScope ->
    try {
        file.getContents(backgroundScope)
    } catch (e: Throwable) {
        e.printStackTrace()
        mutableStateOf("")
    }
}
