package ru.nsu_null.npide.ui.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import ru.nsu_null.npide.parser.translation.ProjectSymbolManager
import ru.nsu_null.npide.ui.config.ProjectSymbolProvider.languageManagerToProjectSymbolManager
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths

object ConfigManager {
    private const val projectFilePath: String = "config.yaml"
    var currentProjectConfig: AutoUpdatedProjectConfig =
        AutoUpdatedProjectConfig(
            ProjectConfig("", "", "", hashMapOf(), listOf(), listOf())
        )
        set(value) {
            field = value
            sync()
        }

    init {
        readConfig()
        updateLanguageSystem()
    }

    private fun sync() {
        storeConfig()
        updateLanguageSystem()
    }

    private fun ProjectSymbolManager.addFileIfNotWatched(filePath: String) {
        if (!hasFile(filePath)) {
            addFile(filePath)
        }
    }

    private fun updateLanguageSystem() {
        for (extension in currentProjectConfig.grammarConfigs.map { it.ext }) {
            val languageManager = LanguageManagerProvider.getLanguageManager(extension)
            ProjectSymbolProvider.getProjectSymbolManager(languageManager)
        }

        for (projectSymbolManager in languageManagerToProjectSymbolManager.values) {
            for (projectFilePath in currentProjectConfig.projectFilePaths) {
                projectSymbolManager.addFileIfNotWatched(projectFilePath)
            }
        }

        for (lexerPath in currentProjectConfig.grammarConfigs.map { it.grammar }) {
            generateLexerParserFiles(
                Paths.get(lexerPath)
            )
        }

    }

    class AutoUpdatedProjectConfig(projectConfig: ProjectConfig) : ProjectConfig(
        projectConfig.build,
        projectConfig.run,
        projectConfig.debug,
        projectConfig.filePathToDirtyFlag,
        projectConfig.projectFilePaths,
        projectConfig.grammarConfigs
    ) {

        override var build: String = super.build
            set(value) {
                field = value
                sync()
            }
        override var run: String = super.run
            set(value) {
                field = value
                sync()
            }
        override var debug: String = super.debug
            set(value) {
                field = value
                sync()
            }
        override var filePathToDirtyFlag: HashMap<String, Boolean> = super.filePathToDirtyFlag
            set(value) {
                field = value
                sync()
            }
        override var projectFilePaths: List<String> = super.projectFilePaths
            set(value) {
                field = value
                sync()
            }
        override var grammarConfigs: List<GrammarConfig> = super.grammarConfigs
            set(value) {
                field = value
                sync()
            }
    }

    @Serializable
    open class ProjectConfig(
        open var build: String,
        open var run: String,
        open var debug: String,
        open var filePathToDirtyFlag: HashMap<String, Boolean>,
        open var projectFilePaths: List<String>,
        open var grammarConfigs: List<GrammarConfig>
    )

    fun ProjectConfig(other: ProjectConfig): ProjectConfig {
        return ProjectConfig(
            other.build,
            other.run,
            other.debug,
            other.filePathToDirtyFlag,
            other.projectFilePaths,
            other.grammarConfigs
        )
    }

    @Serializable
    data class GrammarConfig(
        val ext: String,
        val grammar: String,
        val syntaxHighlighter: String,
    )

    private fun storeConfig() {
        val result = Yaml.default.encodeToString(
            ProjectConfig.serializer(), currentProjectConfig
        )
        File(projectFilePath).writeText(result)
    }

    private fun readConfig() {
        val fileExists: Boolean = File(projectFilePath).createNewFile()
        if(fileExists) {
            storeConfig()
        }
        val configStream = FileInputStream(projectFilePath)
        val result = Yaml.default.decodeFromStream(ProjectConfig.serializer(), configStream)
        currentProjectConfig = AutoUpdatedProjectConfig(result)
    }

    fun setFileDirtiness(file: String, isDirty: Boolean){
        currentProjectConfig.filePathToDirtyFlag[file] = isDirty
        storeConfig()
    }

    fun readFileDirtiness(file: String): Boolean {
        return currentProjectConfig.filePathToDirtyFlag[file] ?: false
    }

    fun isProjectFile(filePath: String): Boolean = filePath in currentProjectConfig.projectFilePaths

    fun addGrammar(ext: String, grammarPath: String, syntaxHighlighter: String){
        val newItem = GrammarConfig(ext, grammarPath, syntaxHighlighter)
        currentProjectConfig.grammarConfigs += newItem
    }

    fun findGrammarConfigByExtension(extension: String): GrammarConfig {
        return currentProjectConfig.grammarConfigs.firstOrNull { it.ext == extension } ?: throw NoSuchElementException()
    }
}
