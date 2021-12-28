package ru.nsu_null.npide.parser.generator

import org.antlr.v4.Tool
import java.nio.file.Path
import java.nio.file.Paths

val outputBaseDirectory: Path = Paths.get("src/main/java")

fun generateLexerParserFiles(grammarPath: Path) {
    val filename = grammarPath.fileName
    val outputDir = Paths.get(
        Paths.get("").toAbsolutePath().toString(),
        outputBaseDirectory.toString(),
        removeExt(filename.toString())
    )

    println(outputDir.toString())
    val args: Array<String> = arrayOf(grammarPath.toAbsolutePath().toString(), "-o", outputDir.toString())
    val antlr = Tool(args)
    antlr.processGrammarsOnCommandLine()
}

fun removeExt(str: String): String {
    if (str.contains("."))
        return str.substring(0, str.lastIndexOf('.'))
    return str
}
