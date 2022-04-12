package ru.nsu_null.npide.ui.editor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import me.tomassetti.kanvas.AntlrTokenMaker
import org.antlr.v4.runtime.Vocabulary
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import readFile
import ru.nsu_null.npide.breakpoints.BreakpointStorage
import ru.nsu_null.npide.parser.compose_support.CustomLanguageSupport
import ru.nsu_null.npide.parser.compose_support.TokenHighlighter
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.translation.TranslationUnit
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.util.SingleSelection
import java.awt.Color
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.AbstractAction
import javax.swing.KeyStroke
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class Editor(
    val filePath: String,
    readContents: (backgroundScope: CoroutineScope) -> String,
    val writeContents: (content: String) -> Unit,
    languageManager: G4LanguageManager?,
) {
    val fileExtension = java.io.File(filePath).extension
    lateinit var gotoHandler: (String, Int) -> Unit
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
    val breakpointHighlightColor = Color(0xC5, 0x53, 0x50, 200)
    var content = ""
    var translationUnit: TranslationUnit? = null

    init {
        val textArea = RSyntaxTextArea(20, 60)


        try {
            val lexerClass = languageManager!!.loadLexerClass()

            if (!ConfigManager.isProjectFile(filePath)) {
                throw NoSuchElementException()
            }

            val grammarConfig = ConfigManager.findGrammarConfigByExtension(fileExtension)

            val languageSupport = CustomLanguageSupport(
                TokenHighlighter(readFile(grammarConfig.syntaxHighlighter)),
                lexerClass.getField("VOCABULARY").get(null) as Vocabulary,
                lexerClass
            )
            (textArea.document as RSyntaxDocument).setSyntaxStyle(AntlrTokenMaker(languageSupport.antlrLexerFactory))
            textArea.syntaxScheme = languageSupport.syntaxScheme
        } catch (ignored: NoSuchElementException) {

        } catch (ignored: NullPointerException) {

        }

        textArea.isCodeFoldingEnabled = true
        textArea.antiAliasingEnabled = true
        textArea.background = Color.DARK_GRAY
        textArea.foreground = Color.WHITE
        textArea.caretColor = Color.BLACK
        textArea.selectionColor = Color.PINK
        textArea.currentLineHighlightColor = Color.LIGHT_GRAY
        val scrollPane = RTextScrollPane(textArea)

        scrollPane.textArea.addKeyListener(object : KeyListener {
            override fun keyTyped(arg0: KeyEvent?) {}
            override fun keyReleased(arg0: KeyEvent?) {}
            override fun keyPressed(event: KeyEvent?) {
                if (event != null) {
                    if (event.isControlDown && event.keyCode == KeyEvent.VK_B) {
                        gotoHandler(filePath, scrollPane.textArea.caretPosition)
                    }
                }
            }
        })

        BreakpointStorage.loadBreakpoints()

        class BreakpointAction : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                if (BreakpointStorage.map[filePath]?.contains(scrollPane.textArea.caretLineNumber) == true) {
                    BreakpointStorage.removeBreakpoint(filePath, scrollPane.textArea.caretLineNumber)
                    scrollPane.textArea.removeLineHighlight(scrollPane.textArea.caretLineNumber)
                    scrollPane.textArea.removeAllLineHighlights()
                    for (line in BreakpointStorage.map[filePath]!!) {
                        scrollPane.textArea.addLineHighlight(line, breakpointHighlightColor)
                    }
                } else {
                    scrollPane.textArea.addLineHighlight(scrollPane.textArea.caretLineNumber, breakpointHighlightColor)
                    BreakpointStorage.addBreakpoint(filePath, scrollPane.textArea.caretLineNumber)
                }
                BreakpointStorage.storeBreakpoints()
            }
        }
        scrollPane.textArea.actionMap.put("breakpointAction", BreakpointAction())
        scrollPane.textArea.inputMap.put(KeyStroke.getKeyStroke("F6"), "breakpointAction")

        rtEditor = scrollPane

        doneLoadingCallback = {
            rtEditor.textArea.document.addDocumentListener(
                SingleCallbackDocumentListenerAfterAWrite {
                    content = rtEditor.textArea.text
                    if (isProjectFile) {
                        ConfigManager.setFileDirtiness(filePath, true)
                    }
                }
            )
            rtEditor.textArea.document.addDocumentListener(
                SingleCallbackDocumentListener {
                    translationUnit?.updateText(rtEditor.textArea.text)
                }
            )
        }
    }

    class SingleCallbackDocumentListener(val callback: () -> Unit) : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            callback()
        }

        override fun removeUpdate(e: DocumentEvent?) {
            callback()
        }

        override fun changedUpdate(e: DocumentEvent?) {}
    }

    private class SingleCallbackDocumentListenerAfterAWrite(val callback: () -> Unit) : DocumentListener {
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

        override fun changedUpdate(e: DocumentEvent?) {}
    }

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    fun setPosition(position: Int) {
        rtEditor.textArea.caret.isVisible = true
        rtEditor.textArea.caret.dot = position
        rtEditor.textArea.caretPosition = position
    }

    val isCode = filePath.endsWith(".kt", ignoreCase = true)
}

fun Editor(
    file: File,
    languageManager: G4LanguageManager?
) = Editor(
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
    }, languageManager
)
