package ru.nsu_null.npide.parser.generator

import org.antlr.v4.runtime.Lexer
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Paths

class G4LanguageManager(
    val baseDir: String,
    val languageName: String
) {
    fun loadLexerClass(): Class<*> {
        val file = File(Paths.get(baseDir, languageName).toString())
        val url: URL = file.toURI().toURL()
        val urlClassLoader = URLClassLoader.newInstance(
            arrayOf(
                url
            ), Lexer::class.java.classLoader
        )
        return urlClassLoader.loadClass("${languageName}Lexer")
    }
}