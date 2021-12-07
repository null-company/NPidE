package ru.nsu_null.npide.ui.editor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.util.SingleSelection
import java.awt.Color

class Editor(
    val fileName: String,
    val readContents: (backgroundScope: CoroutineScope) -> String,
    val writeContents: (content: String) -> Unit
) {
    var close: (() -> Unit)? = null
    lateinit var selection: SingleSelection
    val rtEditor: RTextScrollPane

    var content: String = ""

    init {
        val textArea = RSyntaxTextArea(20, 60)
        textArea.isCodeFoldingEnabled = true
        textArea.antiAliasingEnabled = true
        textArea.background = Color.DARK_GRAY
        textArea.foreground = Color.WHITE
        textArea.caretColor = Color.BLACK
        textArea.selectionColor = Color.PINK
        textArea.currentLineHighlightColor = Color.LIGHT_GRAY

        val sp = RTextScrollPane(textArea)
        sp.textArea.addCaretListener { content = sp.textArea.text }

        rtEditor = sp
    }

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    val isCode = fileName.endsWith(".kt", ignoreCase = true)
}

fun Editor(file: File) = Editor(
    fileName = file.name
, { backgroundScope ->
    try {
        file.readContents(backgroundScope)
    } catch (e: Throwable) {
        e.printStackTrace()
        ""
    }
}, { content ->
    try {
        // todo FIX THIS MONSTROSITY
        file.writeContents(MainScope(), content)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
})
