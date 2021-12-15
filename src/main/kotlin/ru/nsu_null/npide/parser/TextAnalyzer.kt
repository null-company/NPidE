//import ru.nsu_null.npide.parser.file_representation.Node
//import ru.nsu_null.npide.parser.file_representation.FilePositionSplitter
//import ru.nsu_null.npide.parser.file_representation.Position
//import org.antlr.v4.runtime.CharStreams.fromString
//import org.antlr.v4.runtime.CommonTokenStream
//import org.antlr.v4.runtime.tree.ParseTreeWalker
//import CDM8.*
//
//// Главный класс который будет заниматься анализировать исходной файла
//class TextAnalyzer() {
//    private var text: String = ""
//    private var tokenizedFile: FilePositionSplitter<String> = FilePositionSplitter()
//        get() {
//            return field
//        }
//    private var symbolTable: HashMap<Position, Position> = HashMap()
//    fun updateText(newText: String) {
//        text = newText
//        constructTokensStyle()
//        parseSymbolTable()
//    }
//
//    fun tokenizeText(): FilePositionSplitter<String> {
//        return tokenizedFile
//    }
//
//    fun goToDefinition(pos: Position): Position {
//        val line = tokenizedFile[pos.line]
//        var symbolStartPosition: Position = Position(-1, -1)
//        for (node in line.nodes) {
//            if (node.from <= pos.lineOffset && pos.lineOffset < node.to) {
//                symbolStartPosition = Position(pos.line, node.from)
//                break
//            }
//        }
//        if (symbolStartPosition.lineOffset == -1) {
//            return Position(-1, -1)
//        }
//        if (!symbolTable.containsKey(symbolStartPosition)) {
//            return Position(-1, -1)
//        }
//        return symbolTable.get(symbolStartPosition)!!
//    }
//
//    fun parseSymbolTable() {
//        val lexer = CDM8Lexer(fromString(text))
//        val tokens = CommonTokenStream(lexer)
//        val parser = CDM8Parser(tokens)
//        val tree = parser.s()
//        val walker = ParseTreeWalker()
//
//        val defPhaseListener = DefPhase()
//        walker.walk(defPhaseListener, tree)
//
//        val resolvePhaseListener = ResolvePhase(defPhaseListener.tokenToPosition)
//        walker.walk(resolvePhaseListener, tree)
//        symbolTable = resolvePhaseListener.symbolTable
//    }
//
//    fun constructTokensStyle() {
//        val lexer = CDM8Lexer(fromString(text))
//        for (token in lexer.allTokens) {
//            if (token.channel == 1) {
//                continue
//            }
//            println(token)
//            val type = lexer.vocabulary.getSymbolicName(token.type)
//            tokenizedFile[token.line - 1].nodes.add(
//                Node(
//                    token.charPositionInLine,
//                    token.charPositionInLine + token.stopIndex - token.startIndex + 1,
//                    type
//                )
//            )
//        }
//    }
//}
//
//class DefPhase(
//) : CDM8BaseListener() {
//    var tokenToPosition: HashMap<String, Position> = HashMap()
//    override fun enterDef(ctx: CDM8Parser.DefContext?) {
//        super.enterDef(ctx)
//        if (ctx != null) {
//            tokenToPosition.set(ctx.ID().text, Position(ctx.ID().symbol.line - 1, ctx.ID().symbol.charPositionInLine))
//        }
//    }
//}
//
//class ResolvePhase(
//    val tokenToPosition: HashMap<String, Position>,
//) : CDM8BaseListener() {
//    val symbolTable: HashMap<Position, Position> = HashMap()
//    override fun enterUsage(ctx: CDM8Parser.UsageContext?) {
//        super.enterUsage(ctx)
//        if (ctx != null) {
//            val identifierPosition = Position(ctx.ID().symbol.line - 1, ctx.ID().symbol.charPositionInLine)
//            if (tokenToPosition.containsKey(ctx.ID().text)) {
//                symbolTable.set(identifierPosition, tokenToPosition.get(ctx.ID().text)!!)
//            }
//        }
//    }
//}