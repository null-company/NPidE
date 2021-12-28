package ru.nsu_null.npide.ui.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileInputStream

object ConfigManager {
    private const val projectFilePath: String = "config.yaml"
    var currentProjectConfig: AutoUpdatedProjectConfig =
        AutoUpdatedProjectConfig(
            ProjectConfig("", "", "", hashMapOf(), listOf(), listOf())
        )
        set(value) {
            field = value
            storeConfig()
        }

    init {
        readConfig()
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
                storeConfig()
            }
        override var run: String = super.run
            set(value) {
                field = value
                storeConfig()
            }
        override var debug: String = super.debug
            set(value) {
                field = value
                storeConfig()
            }
        override var filePathToDirtyFlag: HashMap<String, Boolean> = super.filePathToDirtyFlag
            set(value) {
                field = value
                storeConfig()
            }
        override var projectFilePaths: List<String> = super.projectFilePaths
            set(value) {
                field = value
                storeConfig()
            }
        override var grammarConfigs: List<GrammarConfig> = super.grammarConfigs
            set(value) {
                field = value
                storeConfig()
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
        val ext:String,
        val grammar: String,
        val syntaxHighlighter:String,
    )

    fun storeConfig() {
        val result = Yaml.default.encodeToString(
            ProjectConfig.serializer(), currentProjectConfig
        )
        File(projectFilePath).writeText(result)
    }

    fun readConfig() {
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

    fun readFileBuilt(file: String): Boolean {
        return currentProjectConfig.filePathToDirtyFlag[file] ?: throw NoSuchElementException()
    }

    fun isProjectFile(filePath: String): Boolean = filePath in currentProjectConfig.projectFilePaths

    fun addGrammar(ext: String, grammarPath: String, syntaxHighlighter: String){
        val newItem = GrammarConfig(ext, grammarPath, syntaxHighlighter)
        currentProjectConfig.grammarConfigs += newItem
    }
}
