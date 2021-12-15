package ru.nsu_null.npide.parser.compose_support

import org.antlr.v4.runtime.Vocabulary
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.SyntaxScheme
import ru.nsu_null.npide.parser.TokenHighlighter
import java.awt.Color

class LanguageSyntaxScheme(val tokenHighlighter: TokenHighlighter, val tokenNumToTokenName: Vocabulary) :
    SyntaxScheme(false) {

    override fun getStyle(index: Int): Style {
        val tokenName: String = if (tokenNumToTokenName.getSymbolicName(index) == null) {
            "ID"
        } else {
            tokenNumToTokenName.getSymbolicName(index);
        }

        val color: Color = Color.decode(tokenHighlighter.getColor(tokenName))
        return Style(color)
    }

}