import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.TokenStreamRewriter
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import java.io.File
import java.nio.file.Paths

fun readFile(filename: String): String {
    return File(filename).inputStream().readBytes().toString(Charsets.UTF_8)
}

fun writeFile(filename: String, data: String) {
    File(filename).writeText(data);
}

fun main() {
    generateLexerParserFiles(
        Paths.get("./src/main/kotlin/ru/nsu_null/npide/parser/CDM8.g4"),
    )
}
