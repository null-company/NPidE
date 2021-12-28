package ru.nsu_null.npide.ui.editor

import androidx.compose.runtime.mutableStateListOf
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.translation.ProjectSymbolManager
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.platform.toProjectFile
import ru.nsu_null.npide.ui.config.LanguageManagerProvider
import ru.nsu_null.npide.ui.config.ProjectSymbolProvider
import ru.nsu_null.npide.util.SingleSelection
import java.lang.IllegalArgumentException
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture

class Editors {
    private val selection = SingleSelection()
    var editors = mutableStateListOf<Editor>()
    val active: Editor? get() = selection.selected as Editor?

    lateinit var openedFile: File

    fun open(file: File): Editor {
        try {
            val alreadyOpenedEditor = editors.first { it.filePath == file.filepath }
            alreadyOpenedEditor.activate()
            return alreadyOpenedEditor
        } catch (ignored: NoSuchElementException) { }


        val extension = java.io.File(file.filepath).extension
        var languageManager: G4LanguageManager? = null
        var projectSymbolManager: ProjectSymbolManager? = null
        try {
            languageManager = LanguageManagerProvider.getLanguageManager(extension)
            projectSymbolManager = ProjectSymbolProvider.getProjectSymbolManager(languageManager)
        } catch (ignored: NoSuchElementException) { }
        val editor = Editor(file, languageManager)
        openedFile = file
        editor.selection = selection

        try {
            projectSymbolManager?.let {
                editor.translationUnit = it.getTranslationUnit(file.filepath)
            }
        } catch (ignored: IllegalArgumentException) { }
        editor.gotoHandler = { filename: String, caretPosition: Int ->
            val pair = projectSymbolManager?.goToDefinition(filename, caretPosition) ?: Pair("", -1)
            val gotoFilename = pair.first
            val position = pair.second
            if (position != -1) {
                val editorToGoTo = open(java.io.File(gotoFilename).toProjectFile())
                // TODO:  аоаоаыы💀⚰☠⚱аыаоыаоооаыаыаооаыа
                CompletableFuture.runAsync {
                    sleep(100)
                    editorToGoTo.setPosition(position + 1)
                }
            }
        }

        editor.close = {
            close(editor)
        }

        editors.add(editor)
        editor.activate()
        return editor
    }

    private fun close(editor: Editor) {
        val index = editors.indexOf(editor)
        editors.remove(editor)
        if (editor.isActive) {
            selection.selected = editors.getOrNull(index.coerceAtMost(editors.lastIndex))
        }
    }
}