package ru.nsu_null.npide.parser.translation

import file_representation.FilePositionSplitter
import file_representation.Node
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import ru.nsu_null.npide.parser.generator.G4LanguageManager

class TranslationUnit(val g4LanguageManager: G4LanguageManager) {
    var innerSymbolTable = SymbolTable()
    var outerSymbolTable = SymbolTable()
    val LexerClass = g4LanguageManager.loadLexerClass()
    val ParserClass = g4LanguageManager.loadParserClass()


    var text = ""

    fun updateText(text: String) {
        this.text = text
        constructTokensStyle()
        parseSymbolTable()
    }

    private var tokenizedFile: FilePositionSplitter<String> = FilePositionSplitter()
        get() {
            return field
        }

    fun parseSymbolTable() {
        val lexer = LexerConstructor()
        val tokens = CommonTokenStream(lexer)
        val parser = ParserConstructor(tokens)
        val tree = ParserClass.getMethod("s").invoke(parser) as ParseTree;
        val walker = ParseTreeWalker()

        val defPhaseListener = DefPhase(g4LanguageManager)
        walker.walk(defPhaseListener, tree)

        innerSymbolTable = defPhaseListener.innerSymbolTable
        outerSymbolTable = defPhaseListener.outerSymbolTable
    }

    fun constructTokensStyle() {
        val lexer = LexerConstructor()
        tokenizedFile = FilePositionSplitter()
        for (token in lexer.allTokens) {
            if (token.channel == 1) {
                continue
            }
            var name = token.text
            if (name == null) {
                name = "UNSPECIFIED";
            }
            tokenizedFile.nodes.add(
                Node(
                    token.startIndex,
                    token.stopIndex,
                    name
                )
            )
        }
    }

    private fun LexerConstructor(): Lexer {
        val lexer = LexerClass.getConstructor(CharStream::class.java).newInstance(CharStreams.fromString(text)) as Lexer
        return lexer
    }

    private fun ParserConstructor(tokens: TokenStream): Parser {
        return ParserClass.getConstructor(TokenStream::class.java).newInstance(tokens) as Parser
    }

    fun getSymbolName(caretPosition: Int): String {
        var symbolName = ""
        for (node in tokenizedFile.nodes) {
            // TODO Почему каретка себя так странно ведет
            // TODO binary search?
            if (node.from <= caretPosition + 1 && caretPosition - 1 <= node.to + 1) {
                symbolName = node.value
                break
            }
        }
        return symbolName;
    }

}

class DefPhase(val loader: G4LanguageManager) : ParseTreeListener {
    var innerSymbolTable = SymbolTable()
    var outerSymbolTable = SymbolTable()
    override fun visitTerminal(node: TerminalNode?) {

    }

    override fun visitErrorNode(node: ErrorNode?) {

    }

    override fun enterEveryRule(ctx: ParserRuleContext?) {
        if (ctx != null) {
            try {
                val DefContextClass = loader.loadParserSubclass("DefContext") ?: return;
                val defCtx = DefContextClass.cast(ctx)
                val id = defCtx.javaClass.getMethod("ID").invoke(defCtx) as TerminalNode
                innerSymbolTable.setSymbol(id.text, id.symbol.startIndex)

            } catch (e: ClassCastException) {

            }
            try {
                val GlobalDefContextClass = loader.loadParserSubclass("Global_defContext") ?: return
                val defCtx = GlobalDefContextClass.cast(ctx)
                val id = defCtx.javaClass.getMethod("ID").invoke(defCtx) as TerminalNode
                outerSymbolTable.setSymbol(id.text, id.symbol.startIndex)

            } catch (e: ClassCastException) {

            }

        }
    }

    override fun exitEveryRule(ctx: ParserRuleContext?) {

    }
}


