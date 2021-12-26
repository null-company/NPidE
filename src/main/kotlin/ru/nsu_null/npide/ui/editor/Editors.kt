package ru.nsu_null.npide.ui.editor

import androidx.compose.runtime.mutableStateListOf
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.translation.ProjectSymbolManager
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.util.SingleSelection
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture


class Editors {
    private val selection = SingleSelection()
    private val fileTypeToProjectSymbolManager = HashMap<String, ProjectSymbolManager>()
    var editors = mutableStateListOf<Editor>()
        private set
    private val fileTypeToG4LanguageManager = HashMap<String, G4LanguageManager>()
    val active: Editor? get() = selection.selected as Editor?

    lateinit var openedFile: File

    fun open(file: File) {
        for (e in editors) {
            if (e.fileName == file.name) {
                e.activate()
                return
            }
        }
        val ext = java.io.File(file.name).extension
        if (!fileTypeToProjectSymbolManager.containsKey(ext)) {
            val languageManager = G4LanguageManager("CDM8");
            fileTypeToG4LanguageManager.set(ext, languageManager)
            fileTypeToProjectSymbolManager.set(ext, ProjectSymbolManager(languageManager))
        }
        val projectSymbolManager = fileTypeToProjectSymbolManager.get(ext)!!
        val languageManager = fileTypeToG4LanguageManager.get(ext)!!
        val editor = Editor(file, languageManager)
        openedFile = file
        editor.selection = selection

        // TODO: Entry Point


        // TODO: add watched files by external way
        if (!projectSymbolManager.hasFile(file.name)) {
            projectSymbolManager.addFile(file.name)
        }

        editor.translationUnit = projectSymbolManager.getTranslationUnit(file.name)
        editor.gotoHandler = { filename: String, caretPosition: Int ->
            val pair = projectSymbolManager.goToDefinition(filename, caretPosition)
            val gotoFilename = pair.first
            val position = pair.second
            if (position != -1) {
                for (e in editors) {
                    if (e.fileName == gotoFilename) {
                        e.activate()
                        // TODO:  аоаоаыыаыаоыаоооаыаыаооаыа
                        CompletableFuture.runAsync {
                            sleep(100)
                            e.setPosition(position + 1)
                        }
                    }
                }
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