package ru.nsu_null.npide.parser.compose_support

import TokenHighlighter
import me.tomassetti.kanvas.*
import me.tomassetti.kolasu.parsing.Parser
import org.antlr.v4.runtime.CharStreams.fromString
import org.antlr.v4.runtime.Vocabulary
import org.fife.ui.rsyntaxtextarea.SyntaxScheme

class CustomLanguageSupport(val tokenHighlighter: TokenHighlighter, val tokenNumToTokenName: Vocabulary) :
    BaseLanguageSupport<RootNode<String>>() {
    override val syntaxScheme: SyntaxScheme
        get() = LanguageSyntaxScheme(tokenHighlighter, tokenNumToTokenName)
    override val antlrLexerFactory: AntlrLexerFactory
        get() = object : AntlrLexerFactory {
            override fun create(code: String): CDM8Lexer = CDM8Lexer(fromString(code))
        }
    override val parser: Parser<RootNode<String>>
        get() = TODO("Not yet implemented")
    override val parserData: ParserData?
        get() = null
}