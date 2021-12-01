package ru.nsu_null.npide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.common.Fonts
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.util.loadableScoped
import ru.nsu_null.npide.util.withoutWidthConstraints
import java.awt.Color
import javax.swing.BoxLayout
import javax.swing.JPanel
import kotlin.text.Regex.Companion.fromLiteral

@Composable
fun EditorView(model: Editor, settings: Settings) = key(model) {
    with (LocalDensity.current) {
        SelectionContainer {
            Surface(
                Modifier.fillMaxSize(),
                color = AppTheme.colors.backgroundDark,
            ) {
                val fileContents by loadableScoped(model.fileContents)

                if (fileContents != null) {
                    Box {
                        CodeEditor(fileContents!!, Modifier.fillMaxSize())
                        Box(
                            Modifier
                                .offset(
                                    x = settings.fontSize.toDp() * 0.5f * settings.maxLineSymbols
                                )
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(AppTheme.colors.backgroundLight)
                        )
                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
var rtCodeEditor: RTextScrollPane? = null

@Composable
fun CodeEditor(code: MutableState<String>, modifier: Modifier = Modifier) {
    if (rtCodeEditor == null) {
        // create the scrollpane only once. Otherwise when text area value is
        // changed, the compose function is called from addCaretListener,
        // and a new code editor is created, with invalid caret position.
        val textArea = RSyntaxTextArea(20, 60)
        textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
        textArea.isCodeFoldingEnabled = true
        textArea.antiAliasingEnabled = true
        textArea.background = Color.DARK_GRAY
        textArea.foreground = Color.WHITE
        textArea.caretColor = Color.BLACK
        textArea.selectionColor = Color.PINK
        textArea.currentLineHighlightColor = Color.LIGHT_GRAY
        val i = textArea.getParser(0)

        val sp = RTextScrollPane(textArea)
        sp.textArea.text = code.value
        sp.textArea.addCaretListener { code.value = sp.textArea.text }

        rtCodeEditor = sp
    }

    Box(modifier = modifier) {
        SwingPanel(
            background = androidx.compose.ui.graphics.Color.White,
            factory = {
                JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    add(rtCodeEditor)
                }
            }
        )
    }
}



private fun codeString(str: String) = buildAnnotatedString {
    withStyle(AppTheme.code.simple) {
        val strFormatted = str.replace("\t", "    ")
        append(strFormatted)
        addStyle(AppTheme.code.punctuation, strFormatted, ":")
        addStyle(AppTheme.code.punctuation, strFormatted, "=")
        addStyle(AppTheme.code.punctuation, strFormatted, "\"")
        addStyle(AppTheme.code.punctuation, strFormatted, "[")
        addStyle(AppTheme.code.punctuation, strFormatted, "]")
        addStyle(AppTheme.code.punctuation, strFormatted, "{")
        addStyle(AppTheme.code.punctuation, strFormatted, "}")
        addStyle(AppTheme.code.punctuation, strFormatted, "(")
        addStyle(AppTheme.code.punctuation, strFormatted, ")")
        addStyle(AppTheme.code.punctuation, strFormatted, ",")
        addStyle(AppTheme.code.keyword, strFormatted, "fun ")
        addStyle(AppTheme.code.keyword, strFormatted, "val ")
        addStyle(AppTheme.code.keyword, strFormatted, "var ")
        addStyle(AppTheme.code.keyword, strFormatted, "private ")
        addStyle(AppTheme.code.keyword, strFormatted, "internal ")
        addStyle(AppTheme.code.keyword, strFormatted, "for ")
        addStyle(AppTheme.code.keyword, strFormatted, "expect ")
        addStyle(AppTheme.code.keyword, strFormatted, "actual ")
        addStyle(AppTheme.code.keyword, strFormatted, "import ")
        addStyle(AppTheme.code.keyword, strFormatted, "package ")
        addStyle(AppTheme.code.value, strFormatted, "true")
        addStyle(AppTheme.code.value, strFormatted, "false")
        addStyle(AppTheme.code.value, strFormatted, Regex("[0-9]*"))
        addStyle(AppTheme.code.annotation, strFormatted, Regex("^@[a-zA-Z_]*"))
        addStyle(AppTheme.code.comment, strFormatted, Regex("^\\s*//.*"))
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
    addStyle(style, text, fromLiteral(regexp))
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
    for (result in regexp.findAll(text)) {
        addStyle(style, result.range.first, result.range.last + 1)
    }
}