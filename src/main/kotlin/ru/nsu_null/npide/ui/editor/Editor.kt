package ru.nsu_null.npide.ui.editor


import ru.nsu_null.npide.parser.compose_support.TokenHighlighter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.tomassetti.kanvas.AntlrTokenMaker
import org.antlr.v4.runtime.Vocabulary
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import readFile
import ru.nsu_null.npide.parser.compose_support.CustomLanguageSupport
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import ru.nsu_null.npide.parser.translation.TranslationUnit
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.util.SingleSelection
import java.awt.Color
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.nio.file.Paths


class Editor(
    val fileName: String,
    val readContents: (backgroundScope: CoroutineScope) -> String,
    val writeContents: (content: String) -> Unit,
    val languageManager: G4LanguageManager,
) {
    lateinit var gotoHandler: (String, Int) -> Unit
    var close: (() -> Unit)? = null
    lateinit var selection: SingleSelection
    val rtEditor: RTextScrollPane
    var content: String = ""
    lateinit var translationUnit: TranslationUnit

    init {
        val textArea = RSyntaxTextArea(20, 60)

        // This function generates files from grammar

        generateLexerParserFiles(Paths.get("./src/main/kotlin/ru/nsu_null/npide/parser/CDM8.g4"))
        // This function loads this stuff and set color scheme

        val LexerClass = languageManager.loadLexerClass()
        val ls = CustomLanguageSupport(
            TokenHighlighter(readFile("src/main/kotlin/ru/nsu_null/npide/parser/colors.json")),
            LexerClass.getField("VOCABULARY").get(null) as Vocabulary,
            LexerClass
        )


        (textArea.document as RSyntaxDocument).setSyntaxStyle(AntlrTokenMaker(ls.antlrLexerFactory))


        textArea.syntaxScheme = ls.syntaxScheme
        textArea.isCodeFoldingEnabled = true
        textArea.antiAliasingEnabled = true
        textArea.background = Color.DARK_GRAY
        textArea.foreground = Color.WHITE
        textArea.caretColor = Color.BLACK
        textArea.selectionColor = Color.PINK
        textArea.currentLineHighlightColor = Color.LIGHT_GRAY
        textArea.caretPosition = textArea.caretPosition
        val sp = RTextScrollPane(textArea)
        var flag = false

        sp.textArea.addKeyListener(object : KeyListener {
            override fun keyTyped(arg0: KeyEvent?) {}
            override fun keyReleased(arg0: KeyEvent?) {}
            override fun keyPressed(event: KeyEvent?) {
                if (event != null) {
                    if (event.isControlDown && event.keyCode == KeyEvent.VK_B) {
                        gotoHandler(fileName, sp.textArea.caretPosition)
                    }
                }
            }
        })

        sp.textArea.addCaretListener {
            translationUnit.updateText(sp.textArea.text)
            content = sp.textArea.text
        }

        rtEditor = sp
    }

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    fun setPosition(position: Int) {
        rtEditor.textArea.caret.isVisible = true;
        rtEditor.textArea.caret.dot = position;
        rtEditor.textArea.caretPosition = position;
    }

    val isCode = fileName.endsWith(".kt", ignoreCase = true)
}

fun Editor(
    file: File,
    languageManager: G4LanguageManager
) = Editor(
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
    }, languageManager
)
