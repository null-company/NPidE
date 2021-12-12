package ru.nsu_null.npide.parser.generator

import org.antlr.v4.Tool
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

val outputDirectory = Paths.get("./src/main/java")

fun generateLexerParserFiles(grammarPath: Path, outputPath: Path) {
    val filename = grammarPath.fileName
    val outputDir = Paths.get(outputPath.toString(), filename.toString())
    val args: Array<String> = arrayOf(grammarPath.toString(), "-o", outputDir.toString())
    Tool.main(args)
}