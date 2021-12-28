package ru.nsu_null.npide.ui.editor

import androidx.compose.runtime.mutableStateListOf
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import ru.nsu_null.npide.parser.translation.ProjectSymbolManager
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.util.SingleSelection
import java.lang.Thread.sleep
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture

class Editors {
    private val selection = SingleSelection()
    var editors = mutableStateListOf<Editor>()
    private val languageManager = G4LanguageManager("CDM8")
    private val projectSymbolManager = ProjectSymbolManager(languageManager)
    val active: Editor? get() = selection.selected as Editor?

    lateinit var openedFile: File

    fun open(file: File) {
        try {
            val alreadyOpenedEditor = editors.first { it.filePath == file.filepath }
            alreadyOpenedEditor.activate()
            return
        } catch (ignored: NoSuchElementException) { }

        val ext = java.io.File(file.name).extension

        val editor = Editor(file, languageManager)
        openedFile = file
        editor.selection = selection

        // TODO: Entry Point


        // TODO: add watched files by external way
        if (!projectSymbolManager.hasFile(file.name)) {
            projectSymbolManager.addFile(file.filepath)
        }

        editor.translationUnit = projectSymbolManager.getTranslationUnit(file.filepath)
        editor.gotoHandler = { filename: String, caretPosition: Int ->
            val pair = projectSymbolManager.goToDefinition(filename, caretPosition)
            println(pair)
            val gotoFilename = pair.first
            val position = pair.second
            if (position != -1) {
                try {
                    editors.first { it.filePath == gotoFilename }.also {
                        // TODO:  аоаоаыы💀⚰☠⚱аыаоыаоооаыаыаооаыа
                        it.activate()
                        CompletableFuture.runAsync {
                            sleep(100)
                            it.setPosition(position + 1)
                        }
                    }
                } catch (ignored: NoSuchElementException) { }
            }
        }
        editor.close = {
            projectSymbolManager.removeFile(file.name)
            close(editor)
        }

        editors.add(editor)
        editor.activate()
    }

    private fun close(editor: Editor) {
        val index = editors.indexOf(editor)
        editors.remove(editor)
        if (editor.isActive) {
            selection.selected = editors.getOrNull(index.coerceAtMost(editors.lastIndex))
        }
    }
}