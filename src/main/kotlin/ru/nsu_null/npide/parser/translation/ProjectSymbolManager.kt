package ru.nsu_null.npide.parser.translation

import file_representation.Node
import file_representation.FilePositionSplitter
import org.antlr.v4.runtime.CharStreams.fromString

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import ru.nsu_null.npide.parser.generator.G4LanguageManager
import java.lang.IllegalArgumentException
import kotlin.collections.HashMap

// Главный класс который будет заниматься анализировать исходной файла
class ProjectSymbolManager(val g4LanguageManager: G4LanguageManager) {
    private val LexerClass = g4LanguageManager.loadLexerClass()
    private val ParserClass = g4LanguageManager.loadParserClass()

    private val pathToTranslationUnit = HashMap<String, TranslationUnit>()

    private var symbolTable: HashMap<Int, Int> = HashMap()
    fun updateText(path: String, newText: String) {
        pathToTranslationUnit[path]!!.text = newText
        pathToTranslationUnit[path]!!.constructTokensStyle()
        pathToTranslationUnit[path]!!.parseSymbolTable()
    }

    // TODO Изменить представление файла в соответствии с архитектурой RSyntaxTextArea
    fun goToDefinition(filename: String, caretPosition: Int): Pair<String, Int> {
        if (!pathToTranslationUnit.containsKey(filename)) {
            return Pair("", -1)
        }
        val symbolName: String = pathToTranslationUnit[filename]!!.getSymbolName(caretPosition)
        if (symbolName == "") {
            return Pair("", -1)
        }
        if (pathToTranslationUnit[filename]!!.innerSymbolTable.contains(symbolName)) {
            return Pair(filename, pathToTranslationUnit[filename]!!.innerSymbolTable.getSymbolPos(symbolName))
        }
        for (k in pathToTranslationUnit.keys) {
            if (pathToTranslationUnit.get(k)!!.outerSymbolTable.contains(symbolName)) {
                val pos = pathToTranslationUnit.get(k)!!.outerSymbolTable.getSymbolPos(symbolName)
                return Pair(k, pos)
            }
        }
        return Pair("", -1)
    }

    fun addFile(filename: String) {
        if (hasFile(filename)) {
            throw IllegalArgumentException("This file is already watched")
        }
        pathToTranslationUnit[filename] = TranslationUnit(g4LanguageManager)
    }
    fun hasFile(filename: String): Boolean {
        if (pathToTranslationUnit.containsKey(filename)) {
            return true
        }
        return false
    }
    fun removeFile(filename: String) {
        if (!hasFile(filename)) {
            throw IllegalArgumentException("Unknown filename")
        }
        pathToTranslationUnit.remove(filename)
    }

    fun getTranslationUnit(filename: String): TranslationUnit {
        if (!pathToTranslationUnit.containsKey(filename)) {
            throw IllegalArgumentException("Unknown filename")
        }
        return pathToTranslationUnit[filename]!!
    }
}