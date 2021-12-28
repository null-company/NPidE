package ru.nsu_null.npide.ui.editor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.tomassetti.kanvas.AntlrTokenMaker
import org.antlr.v4.runtime.Vocabulary
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import readFile
import ru.nsu_null.npide.parser.TokenHighlighter
import ru.nsu_null.npide.parser.compose_support.CustomLanguageSupport
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.util.SingleSelection
import java.awt.Color
import java.nio.file.Paths
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class Editor(
    val filePath: String,
    readContents: (backgroundScope: CoroutineScope) -> String,
    val writeContents: (content: String) -> Unit
) {
    private val isProjectFile: Boolean = ConfigManager.isProjectFile(filePath)
    private lateinit var doneLoadingCallback: () -> Unit
    val readContents: (backgroundScope: CoroutineScope) -> String = { backgroundScope ->
        val res = readContents(backgroundScope)
        doneLoadingCallback()
        res
    }
    var close: (() -> Unit)? = null
    lateinit var selection: SingleSelection
    val rtEditor: RTextScrollPane

    var content: String = ""

    init {
        val textArea = RSyntaxTextArea(20, 60)

        generateLexerParserFiles(
            Paths.get("./src/main/kotlin/ru/nsu_null/npide/parser/CDM8.g4"),
        )

        val languageManager = G4LanguageManager("./src/main/java", "CDM8")
        val lexerClass = languageManager.loadLexerClass()

        val languageSupport = CustomLanguageSupport(
            TokenHighlighter(readFile("src/main/kotlin/ru/nsu_null/npide/parser/colors.json")),
            lexerClass.getField("VOCABULARY").get(null) as Vocabulary,
            lexerClass
        )

        (textArea.document as RSyntaxDocument).setSyntaxStyle(AntlrTokenMaker(languageSupport.antlrLexerFactory))

        languageSupport.contextCreator.create()

        textArea.syntaxScheme = languageSupport.syntaxScheme
        textArea.isCodeFoldingEnabled = true
        textArea.antiAliasingEnabled = true
        textArea.background = Color.DARK_GRAY
        textArea.foreground = Color.WHITE
        textArea.caretColor = Color.BLACK
        textArea.selectionColor = Color.PINK
        textArea.currentLineHighlightColor = Color.LIGHT_GRAY


        val scrollPane = RTextScrollPane(textArea)
        rtEditor = scrollPane

        doneLoadingCallback = {
            rtEditor.textArea.document.addDocumentListener(
                SingleCallbackDocumentListener {
                    content = scrollPane.textArea.text
                    if (isProjectFile) {
                        ConfigManager.setFileDirtiness(filePath, true)
                    }
                }
            )
        }
    }

    private class SingleCallbackDocumentListener(val callback: () -> Unit) : DocumentListener {
        var hasText: Boolean = false
        private fun callbackIfEdit() {
            synchronized(this) {
                if (hasText) {
                    callback()
                } else {
                    hasText = true
                }
            }
        }
        override fun insertUpdate(e: DocumentEvent?) {
            callbackIfEdit()
        }
        override fun removeUpdate(e: DocumentEvent?) {
            callbackIfEdit()
        }
        override fun changedUpdate(e: DocumentEvent?) { }
    }

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    val isCode = filePath.endsWith(".kt", ignoreCase = true)
}

fun Editor(file: File) = Editor(
    filePath = file.filepath, { backgroundScope ->
        try {
            val res = file.readContents(backgroundScope)
            res
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
