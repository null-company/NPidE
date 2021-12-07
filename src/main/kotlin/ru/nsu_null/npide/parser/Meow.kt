import org.antlr.v4.runtime.TokenStreamRewriter
import java.io.File

fun readFile(filename: String): String {
    return File(filename).inputStream().readBytes().toString(Charsets.UTF_8)
}

fun writeFile(filename: String, data: String) {
    File(filename).writeText(data);
}

fun main() {
    val highlightRulesString = readFile("colors.json")
    val highlightRules = TokenHighlighter(highlightRulesString)

    val textAnalyzer = TextAnalyzer()
    textAnalyzer.updateText(readFile("ex.asm"))
    writeFile(
        "index.html", convertToHtml(
            readFile("ex.asm").lines() as ArrayList<String>,
            textAnalyzer
        )
    )
}


class LangParseTreeListener(
    val parser: CDM8Parser,
    val highlightRules: TokenHighlighter,
) : CDM8BaseListener() {
    var answer = TokenStreamRewriter(parser.tokenStream);
}
