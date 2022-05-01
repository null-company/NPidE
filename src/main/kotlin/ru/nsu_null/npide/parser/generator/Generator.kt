package ru.nsu_null.npide.parser.generator

import org.antlr.v4.Tool
import java.io.File
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
    println("Provided grammar path is: $grammarPath")
    val lexerPath = Paths.get(
        grammarPath.parent.toString(),
        removeExt(filename.toString()) + "_lexer.g4"
    )
    println("Possible lexer file is: $lexerPath")
    if (File(lexerPath.toString()).exists()) {
        println("Addition lexer file was found")
        val args: Array<String> = arrayOf(lexerPath.toAbsolutePath().toString(), "-o", outputDir.toString())
        val antlr = Tool(args)
        antlr.processGrammarsOnCommandLine()
        println("Lexer rules was generated")
    } else {
        println("No lexer file was found")
    }
    val targetParserPath = Paths.get(outputDir.toString(), grammarPath.fileName.toString());
    File(grammarPath.toString()).copyTo(
        File(
            targetParserPath.toString()
        ).also { it.parentFile.mkdirs() }, overwrite = true
    )
    println("Parser file was copied to $targetParserPath")
    val args: Array<String> = arrayOf(targetParserPath.toAbsolutePath().toString(), "-o", outputDir.toString())
    val antlr = Tool(args)
    antlr.processGrammarsOnCommandLine()
}

fun removeExt(str: String): String {
    if (str.contains("."))
        return str.substring(0, str.lastIndexOf('.'))
    return str
}
