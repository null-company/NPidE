import file_representation.Node
import file_representation.FilePositionSplitter
import org.antlr.v4.runtime.CharStreams.fromString

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import kotlin.collections.HashMap

// Главный класс который будет заниматься анализировать исходной файла
class TextAnalyzer(val g4LanguageManager: G4LanguageManager) {
    val LexerClass = g4LanguageManager.loadLexerClass()
    val ParserClass = g4LanguageManager.loadParserClass()

    private var text: String = ""
    private var tokenizedFile: FilePositionSplitter<String> = FilePositionSplitter()
        get() {
            return field
        }
    private var symbolTable: HashMap<Int, Int> = HashMap()
    fun updateText(newText: String) {
        text = newText
        constructTokensStyle()
        parseSymbolTable()
    }

    fun tokenizeText(): FilePositionSplitter<String> {
        return tokenizedFile
    }

    // TODO Линейный поиск это конечно замечательно, но бинарный поиск ещё лучше
    // TODO Изменить представление файла в соответствии с архитектурой RSyntaxTextArea

    fun goToDefinition(caretPosition: Int): Int {
        var symbolStartPosition = -1
        for (node in tokenizedFile.nodes) {
            // TODO Почему каретка себя так странно ведет
            if (node.from <= caretPosition + 1 && caretPosition - 1 <= node.to + 1) {
                symbolStartPosition = node.from
                break
            }
        }
        if (!symbolTable.containsKey(symbolStartPosition)) {
            return -1
        }
        return symbolTable.get(symbolStartPosition)!!
    }

    private fun LexerConstructor(): Lexer {
        val lexer = LexerClass.getConstructor(CharStream::class.java).newInstance(fromString(text)) as Lexer
        return lexer
    }

    private fun ParserConstructor(tokens: TokenStream): Parser {
        return ParserClass.getConstructor(TokenStream::class.java).newInstance(tokens) as Parser
    }

//    private fun getListenerClass(): Class<*> {
//
//    }

    fun parseSymbolTable() {
        val lexer = LexerConstructor()
        val tokens = CommonTokenStream(lexer)
        val parser = ParserConstructor(tokens)
        val tree = ParserClass.getMethod("s").invoke(parser) as ParseTree;
        val walker = ParseTreeWalker()

        val defPhaseListener = DefPhase(g4LanguageManager)
        walker.walk(defPhaseListener, tree)

        val resolvePhaseListener = ResolvePhase(g4LanguageManager, defPhaseListener.tokenToPosition)
        walker.walk(resolvePhaseListener, tree)
        symbolTable = resolvePhaseListener.symbolTable
    }

    fun constructTokensStyle() {
        val lexer = LexerConstructor()
        tokenizedFile = FilePositionSplitter()
        for (token in lexer.allTokens) {
            if (token.channel == 1) {
                continue
            }
            val type = lexer.vocabulary.getSymbolicName(token.type)
            tokenizedFile.nodes.add(
                Node(
                    token.startIndex,
                    token.stopIndex,
                    type
                )
            )
        }
    }
}

class DefPhase(val loader: G4LanguageManager) : ParseTreeListener {
    var tokenToPosition: HashMap<String, Int> = HashMap()

    override fun visitTerminal(node: TerminalNode?) {

    }

    override fun visitErrorNode(node: ErrorNode?) {

    }

    override fun enterEveryRule(ctx: ParserRuleContext?) {
        if (ctx != null) {
            try {
                val DefContextClass = loader.loadParserSubclass("DefContext") ?: return
                val defCtx = DefContextClass.cast(ctx)
                val id = defCtx.javaClass.getMethod("ID").invoke(defCtx) as TerminalNode
                tokenToPosition[id.text] = id.symbol.startIndex
            } catch (e: ClassCastException) {
            }

        }
    }

    override fun exitEveryRule(ctx: ParserRuleContext?) {

    }
}

class ResolvePhase(
    val loader: G4LanguageManager,
    val tokenToPosition: HashMap<String, Int>,
) : ParseTreeListener {
    val symbolTable: HashMap<Int, Int> = HashMap()

    override fun visitTerminal(node: TerminalNode?) {

    }

    override fun visitErrorNode(node: ErrorNode?) {

    }

    override fun enterEveryRule(ctx: ParserRuleContext?) {
        if (ctx != null) {
            try {
                val UsageContextClass = loader.loadParserSubclass("UsageContext") ?: return
                val defCtx = UsageContextClass.cast(ctx)
                val id = defCtx.javaClass.getMethod("ID").invoke(defCtx) as TerminalNode
                val identifierPosition = id.symbol.startIndex
                if (tokenToPosition.containsKey(id.text)) {
                    symbolTable.set(identifierPosition, tokenToPosition.get(id.text)!!)
                }
            } catch (e: ClassCastException) {

            }
        }
    }

    override fun exitEveryRule(ctx: ParserRuleContext?) {

    }
}