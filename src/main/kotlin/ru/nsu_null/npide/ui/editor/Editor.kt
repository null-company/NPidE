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
import ru.nsu_null.npide.util.SingleSelection
import java.awt.Color
import java.nio.file.Paths

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
        scrollPane.textArea.addCaretListener { content = scrollPane.textArea.text }

        rtEditor = scrollPane
    }

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    val isCode = fileName.endsWith(".kt", ignoreCase = true)
}

fun Editor(file: File) = Editor(
    fileName = file.name, { backgroundScope ->
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
