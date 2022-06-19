package ru.nsu_null.npide.ide.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import ru.nsu_null.npide.ide.projectchooser.ProjectChooser
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths
import kotlin.reflect.KProperty

class ConfigManager(private val project: ProjectChooser.Project) {
    private val projectConfigPath: String = project.rootFolder.filepath + "/config.yaml"
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
    }

    private fun sync() {
        storeConfig()
        updateLanguageSystem()
    }

    private fun updateLanguageSystem() {
        for (lexerPath in currentProjectConfig.grammarConfigs.map { it.grammar }) {
            generateLexerParserFiles(
                Paths.get(lexerPath)
            )
        }
    }

    class AutoUpdatedProjectConfig internal constructor(private val configManager: ConfigManager,
                                   projectConfig: ProjectConfig) : ProjectConfig(
        projectConfig.build,
        projectConfig.run,
        projectConfig.debug,
        projectConfig.filePathToDirtyFlag,
        projectConfig.projectFilePaths,
        projectConfig.grammarConfigs
    ) {

        private inner class AutoUpdateDelegate<T>(initialValue: T) {
            var content: T = initialValue
            operator fun getValue(
                autoUpdatedProjectConfig: AutoUpdatedProjectConfig,
                property: KProperty<*>
            ): T {
                return content
            }
            operator fun setValue(
                autoUpdatedProjectConfig: AutoUpdatedProjectConfig,
                property: KProperty<*>,
                newContent: T
            ) {
                content = newContent
                configManager.sync()
            }
        }

        override var build: String by AutoUpdateDelegate(super.build)
        override var run: String by AutoUpdateDelegate(super.run)
        override var debug: String by AutoUpdateDelegate(super.debug)
        override var filePathToDirtyFlag: HashMap<String, Boolean> by AutoUpdateDelegate(super.filePathToDirtyFlag)
        override var projectFilePaths: List<String> by AutoUpdateDelegate(super.projectFilePaths)
        override var grammarConfigs: List<GrammarConfig> by AutoUpdateDelegate(super.grammarConfigs)
    }

    fun AutoUpdatedProjectConfig(projectConfig: ProjectConfig): AutoUpdatedProjectConfig =
        AutoUpdatedProjectConfig(this, projectConfig)

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
        File(projectConfigPath).writeText(result)
    }

    private fun readConfig() {
        val fileExists: Boolean = File(projectConfigPath).createNewFile()
        if (fileExists) {
            storeConfig()
        }
        val configStream = FileInputStream(projectConfigPath)
        val result = Yaml.default.decodeFromStream(ProjectConfig.serializer(), configStream)
        currentProjectConfig = AutoUpdatedProjectConfig(result)
    }

    fun setFileDirtiness(file: String, isDirty: Boolean) {
        currentProjectConfig.filePathToDirtyFlag[file] = isDirty
        storeConfig()
    }

    fun readFileDirtiness(file: String): Boolean {
        return currentProjectConfig.filePathToDirtyFlag[file] ?: false
    }

    fun isProjectFile(filePath: String): Boolean = filePath in currentProjectConfig.projectFilePaths

    fun addGrammar(ext: String, grammarPath: String, syntaxHighlighter: String) {
        val newItem = GrammarConfig(ext, grammarPath, syntaxHighlighter)
        currentProjectConfig.grammarConfigs += newItem
    }

    fun findGrammarConfigByExtension(extension: String): GrammarConfig {
        return currentProjectConfig.grammarConfigs.firstOrNull { it.ext == extension } ?: throw NoSuchElementException()
    }
}
